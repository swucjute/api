# API Route Scaffold

이 문서는 `DB_API_최종본.xlsx`를 기준으로 이 레포에 먼저 잡아둔 API 골격을 정리한다. 현재 컨트롤러와 서비스는 라우트 등록용 스텁이며, 실제 비즈니스 로직은 담당 도메인 구현 시 채운다.

## 구현 기준

- Controller는 요청 매핑과 DTO 바인딩만 담당한다.
- Service는 현재 `NOT_IMPLEMENTED`를 던진다. 실제 구현은 각 도메인 service에서 시작한다.
- 응답은 `ApiResponse<T>`로 통일한다.
- 앱 API는 `/api/v1/{복수 리소스}` 규칙을 따른다.
- Spring Security OAuth2 시작 경로인 `/oauth2/authorization/kakao`는 `/api/v1` 예외로 둔다.

## 도메인별 진입점

| 도메인 | Controller | Service |
| --- | --- | --- |
| Auth | `domain/auth/controller/AuthController.java` | `domain/auth/service/AuthService.java` |
| Member | `domain/member/controller/MemberController.java` | `domain/member/service/MemberService.java` |
| Worship | `domain/worship/controller/WorshipController.java` | `domain/worship/service/WorshipService.java` |
| Worship Summary | `domain/worship/controller/WorshipSummaryController.java` | `domain/worship/service/WorshipSummaryService.java` |
| Platform | `domain/platform/controller/PlatformController.java` | `domain/platform/service/PlatformService.java` |
| Home | `domain/home/controller/HomeController.java` | `domain/home/service/HomeService.java` |

## 현재 상태

- 공개 조회 API는 보안 설정에서 `permitAll`로 열어두었다.
- 쓰기/관리성 API는 인증 필요 상태로 둔다.
- JWT 필터와 권한 검증은 아직 실제 구현 전이므로, 담당자가 보안 모듈 구현 시 `SecurityConfig`를 함께 조정해야 한다.
- 모든 스텁 API는 현재 `501 NOT_IMPLEMENTED`를 반환한다.
