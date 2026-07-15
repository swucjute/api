# 인증(카카오 로그인 / JWT) 개발·테스트 가이드

> 대상: 회원 외 다른 도메인(예배·플랫폼·홈 등)을 맡아 **액세스 토큰으로 인증이 필요한 API를 개발·테스트**하는 팀원.
> 이 문서만 보면 (1) 토큰을 어떻게 발급받고 (2) Swagger에서 회원으로 테스트하고 (3) 컨트롤러에서 `@AuthenticationPrincipal`로 현재 회원을 꺼내 쓰는 법을 알 수 있습니다.

---

## 0. 3줄 요약

1. 브라우저로 `http://localhost:8080/oauth2/authorization/kakao` 접속 → 카카오 로그인 → 응답 JSON의 `accessToken` 복사
2. Swagger 우상단 **Authorize** 버튼 → 토큰 붙여넣기 → 이제 잠긴 API를 회원 자격으로 호출 가능
3. 컨트롤러에서 현재 회원은 `@AuthenticationPrincipal MemberPrincipal principal` → `principal.memberId()`

---

## 1. 인증 구조 한눈에 보기

```
[클라이언트]                 [서비스 서버]                    [카카오]
    │  GET /oauth2/authorization/kakao │                          │
    │ ───────────────────────────────▶ │  인가 요청 리다이렉트     │
    │ ◀───────────────────────────────────────────────────────── │  카카오 로그인/동의
    │  /login/oauth2/code/kakao (인가코드) ─────────────────────▶ │
    │                                   │  토큰·사용자정보 조회 ◀─▶ │
    │                                   │  members find-or-create  │
    │                                   │  (신규면 USER / PENDING)  │
    │ ◀─────────────────────────────── │  JWT(access·refresh) 발급 │
    │      { accessToken, refreshToken }│                          │
    │                                   │                          │
    │  이후 모든 요청:                   │                          │
    │  Authorization: Bearer <accessToken>                         │
    │ ───────────────────────────────▶ │  JwtAuthenticationFilter │
    │                                   │  → SecurityContext에 회원 등록
```

- **인증 방식**: Stateless JWT. 세션 쿠키를 쓰지 않습니다(카카오 핸드셰이크 순간만 세션 사용).
- **액세스 토큰**: 요청마다 `Authorization: Bearer <accessToken>` 헤더로 전달.
- 회원가입은 별도 단계가 없습니다. **카카오 로그인 최초 시 `members` 행이 자동 생성**(권한 `USER`, 상태 `PENDING`, 프로필 없음)됩니다. 실명/프로필/교적 등록은 회원 파트 담당 범위입니다.

---

## 2. 토큰 사양

| 구분 | 값 |
|------|-----|
| 서명 | HS256 (대칭키, `jwt.secret`) |
| 액세스 토큰 subject | `memberId` (문자열) |
| 액세스 토큰 claim | `role` = `MemberRole` 이름 (`USER`/`LEADER`/`MANAGER`/`ADMIN`) |
| 액세스 토큰 만료 | 기본 **3600초(1시간)** — `jwt.access-token-validity-seconds` |
| 리프레시 토큰 | subject=`memberId`, role claim 없음, 기본 **1209600초(14일)** |
| 리프레시 저장 | 원문 저장 안 함. **SHA-256 해시**로 `refresh_tokens`에 회원당 1행, 재발급 시 rotation |

> 토큰 내용이 궁금하면 https://jwt.io 에 붙여넣어 payload(`sub`, `role`, `exp`)를 볼 수 있습니다. (서명 검증용 secret은 넣지 마세요.)

---

## 3. 인증 관련 엔드포인트

| 기능 | 메서드 · 경로 | 인증 | 요청 | 응답(data) |
|------|------|:---:|------|------|
| 카카오 로그인 시작 | `GET /oauth2/authorization/kakao` | ✕ | — | (브라우저 리다이렉트) |
| 로그인 완료 콜백 | `GET /login/oauth2/code/kakao` | ✕ | (카카오가 호출) | `AuthTokenResponse` JSON |
| 토큰 재발급 | `POST /api/v1/auth/refresh` | ✕ | `{ "refreshToken": "..." }` | `AuthTokenResponse` |
| 로그아웃 | `POST /api/v1/auth/logout` | ✔ | `{ "refreshToken": "..." }`(선택) | `null` (리프레시 토큰 폐기) |

**`AuthTokenResponse` 형태**
```json
{
  "status": 200,
  "message": "로그인 성공",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenType": "Bearer",
    "expiresIn": 3600
  }
}
```

### 공개(인증 불필요) 경로
`/api/v1/auth/**`, `/oauth2/**`, `/login/oauth2/**`, `/swagger-ui/**`, `/v3/api-docs/**`, `/actuator/health`,
그리고 `GET /api/v1/worships/**`, `GET /api/v1/home/**`, `GET /api/v1/platforms`·`/api/v1/platforms/*`.
**그 외 모든 요청은 액세스 토큰이 있어야 합니다.**

---

## 4. 액세스 토큰 발급받기 (실제 카카오 로그인)

일반 로그인(아이디/비번)은 없습니다. 토큰은 **카카오 로그인 플로우**로만 얻습니다.

### 4-1. 사전 준비
1. DB/Redis/Mongo 인프라 실행 (infra 레포 `docker-compose.local.yml`).
2. `.env`에 `KAKAO_CLIENT_ID`, `KAKAO_CLIENT_SECRET`, `JWT_SECRET`(32바이트 이상) 설정.
3. `local` 프로필로 앱 실행.

### 4-2. 토큰 받기
1. **브라우저**에서 접속: `http://localhost:8080/oauth2/authorization/kakao`
   - ⚠️ Swagger 안에서는 카카오 로그인이 안 됩니다(OAuth2 리다이렉트라 실제 브라우저 창이 필요).
2. 카카오 로그인·동의를 마치면, 콜백 후 **화면에 JSON**이 그대로 찍힙니다.
3. `data.accessToken` 값을 복사합니다. (필요하면 `refreshToken`도.)

### 4-3. 토큰 만료 시
- 액세스 토큰은 1시간 뒤 만료됩니다. 만료되면 보호 API가 `401`을 반환합니다.
- `POST /api/v1/auth/refresh`에 `refreshToken`을 담아 호출하면 새 access·refresh를 받습니다(기존 리프레시는 rotation).
- 리프레시도 만료(14일)됐으면 4-2를 다시 수행합니다.

```bash
# 재발급 예시
curl -X POST http://localhost:8080/api/v1/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{"refreshToken":"eyJhbGciOi..."}'
```

---

## 5. Swagger에서 회원으로 테스트하기

1. 앱 실행 후 Swagger 접속: `http://localhost:8080/swagger-ui.html`
2. **4번**에서 받은 `accessToken` 복사.
3. 우측 상단 **Authorize** 버튼 클릭.
4. `Bearer Token` 입력란에 **토큰만 붙여넣기** (앞에 `Bearer ` 붙이지 말 것 — HTTP bearer 스킴이라 Swagger가 자동으로 붙입니다) → **Authorize** → **Close**.
5. 이제 각 API의 `Try it out` → `Execute` 시 `Authorization: Bearer ...` 헤더가 자동 포함됩니다. 로그인한 회원 자격으로 호출됩니다.
6. 로그아웃/토큰 교체는 다시 **Authorize** → 값 지우거나 새 토큰 입력.

> `Authorize`에 넣은 토큰이 만료되면 호출이 `401`로 떨어집니다. 새 토큰으로 다시 Authorize 하세요.

### curl로 직접 호출할 때
```bash
curl http://localhost:8080/api/v1/members/me \
  -H "Authorization: Bearer eyJhbGciOi..."
```

---

## 6. 컨트롤러에서 현재 회원 사용하기 (`@AuthenticationPrincipal`)

인증 필터가 토큰을 검증한 뒤 **`MemberPrincipal`** 을 SecurityContext에 넣어둡니다.
컨트롤러 파라미터에 `@AuthenticationPrincipal`을 붙이면 주입됩니다.

```java
import com.swucjute.api.global.security.MemberPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@GetMapping("/me")
public ApiResponse<MyResponse> getMe(@AuthenticationPrincipal MemberPrincipal principal) {
  Long memberId = principal.memberId();   // 현재 로그인 회원 ID
  MemberRole role = principal.role();      // 권한
  return ApiResponse.success(myService.getMe(memberId));
}
```

`MemberPrincipal`은 다음 두 값을 가진 record입니다.

| 접근 | 설명 |
|------|------|
| `principal.memberId()` | 회원 PK (`Long`) — 서비스 조회 키로 사용 |
| `principal.role()` | `MemberRole` (권한 분기용) |

### 꺼내 쓰는 다른 방법
```java
// Authentication에서 직접 (getName()이 memberId 문자열을 반환)
Long memberId = Long.valueOf(authentication.getName());
```
> 참고: `@AuthenticationPrincipal(expression = "memberId")`처럼 SpEL 프로퍼티로 꺼내는 방식은
> `MemberPrincipal`이 record라 접근자가 `getMemberId()`가 아닌 `memberId()`여서 해석되지 않습니다.
> 위의 `MemberPrincipal` 직접 주입 방식을 사용하세요.

### 주의사항
- **서비스 계층에는 엔티티 전체가 아니라 `memberId`만 넘기는 것**을 권장합니다(불필요한 결합 방지). 실제 `Member`가 필요하면 서비스에서 `memberRepository.findById(memberId)`로 조회하세요.
- 공개 경로에서 `@AuthenticationPrincipal`을 쓰면 인증이 없을 때 `principal`이 **`null`** 입니다. 보호 경로에서만 non-null이 보장됩니다.
- `principal.role()`은 **토큰 발급 시점의 권한**입니다. DB에서 권한을 바꿔도, 그 회원이 새 토큰을 받기(재발급/재로그인) 전까지는 기존 토큰의 권한이 유지됩니다.

---

## 7. 권한(Role) 기반 접근 제어

- 필터가 `ROLE_<role>` 형태 권한을 부여합니다 (예: `USER` → `ROLE_USER`, `ADMIN` → `ROLE_ADMIN`).
- **현재 `SecurityConfig`는 보호 경로를 "인증만 되면 통과(authenticated)"로 둡니다. 역할별 제한은 아직 없습니다.** 관리자 전용 API 등 역할 제한이 필요하면 아래 중 하나로 추가하세요.

**방법 A — `SecurityConfig`에서 경로 단위 제한**
```java
.requestMatchers(HttpMethod.GET, "/api/v1/members").hasRole("ADMIN")
```

**방법 B — 메서드 단위(`@PreAuthorize`)**
> ⚠️ 현재 메서드 시큐리티가 꺼져 있습니다. 쓰려면 설정 클래스에 `@EnableMethodSecurity`를 먼저 추가해야 합니다.
```java
@EnableMethodSecurity            // 설정 클래스에 1회
...
@PreAuthorize("hasRole('ADMIN')")
@GetMapping
public ApiResponse<...> getMembers(...) { ... }
```

---

## 8. 응답 / 에러 포맷

모든 응답은 `ApiResponse<T>`로 통일됩니다.

```json
// 성공
{ "status": 200, "message": "성공", "data": { ... } }
```

인증 관련 에러:

| 상황 | status | message |
|------|:---:|------|
| 토큰 없음/인증 필요 | 401 | 인증이 필요합니다 |
| 토큰 위조·손상 | 401 | 유효하지 않은 토큰입니다 |
| 토큰 만료 | 401 | 만료된 토큰입니다 |
| 리프레시 토큰 불일치/폐기됨 | 401 | 유효하지 않은 토큰입니다 |
| 카카오 인증 실패 | 401 | 소셜 로그인 인증에 실패했습니다 |
| 권한 부족(역할 제한 추가 시) | 403 | 접근 권한이 없습니다 |

---

## 9. 자주 겪는 문제 (Troubleshooting)

**Q. Swagger에서 잠긴 API가 계속 401이에요.**
Authorize에 토큰을 넣었는지, 만료되진 않았는지 확인. 토큰 앞에 `Bearer `를 직접 붙이면 이중이 되어 실패합니다 — **토큰만** 넣으세요.

**Q. 관리자(ADMIN) 권한으로 테스트하고 싶어요.**
기본 발급 권한은 `USER`입니다. 로컬 DB에서 본인 `members.member_role`을 `ADMIN`으로 바꾼 뒤, **`POST /api/v1/auth/refresh`로 토큰을 재발급**받으면(또는 재로그인) 새 액세스 토큰의 `role`이 `ADMIN`이 됩니다. (기존 토큰은 그대로 `USER`)

**Q. `@AuthenticationPrincipal MemberPrincipal`이 null이에요.**
그 경로가 공개 경로거나, 요청에 유효한 `Authorization: Bearer` 헤더가 없는 경우입니다. 보호 경로 + 유효 토큰인지 확인하세요.

**Q. 카카오 로그인 후 JSON이 아니라 에러가 떠요.**
`.env`의 `KAKAO_CLIENT_ID`/`SECRET`과 카카오 콘솔의 Redirect URI(`http://localhost:8080/login/oauth2/code/kakao`) 일치 여부를 확인하세요.

**Q. 로그아웃했는데 액세스 토큰이 아직 먹혀요.**
로그아웃은 **리프레시 토큰을 폐기**해 재발급을 막는 방식입니다. 이미 발급된 액세스 토큰은 만료(최대 1시간) 전까지 유효합니다(무상태 JWT 특성). 즉시 무효화가 필요하면 별도 blacklist 도입이 필요합니다.

---

## 10. 관련 코드 위치

| 역할 | 파일 |
|------|------|
| 토큰 생성·검증 | `global/security/JwtTokenProvider.java` |
| 요청 인증 필터 | `global/security/JwtAuthenticationFilter.java` |
| 인증 주체(principal) | `global/security/MemberPrincipal.java` |
| 401 응답 처리 | `global/security/JwtAuthenticationEntryPoint.java` |
| 카카오 사용자 처리 | `domain/auth/oauth/KakaoOAuth2UserService.java` |
| 로그인 성공(토큰 발급) | `domain/auth/oauth/OAuth2LoginSuccessHandler.java` |
| 재발급·로그아웃 로직 | `domain/auth/service/AuthService.java` |
| 시큐리티/경로 설정 | `global/config/SecurityConfig.java` |
| Swagger 인증 스킴 | `global/config/SwaggerConfig.java` |
