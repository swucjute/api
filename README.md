# swucjute/api

주뜨 청년부 앱 Spring Boot REST API 서버

## 기술 스택

| 항목 | 선택 | 버전 |
|------|------|------|
| 언어 | Java | 17 (LTS) |
| 프레임워크 | Spring Boot | 3.4.x |
| 보안 | Spring Security + JWT | Boot 내장 |
| API 문서 | springdoc-openapi (Swagger) | 2.8.x |
| ORM | Spring Data JPA + QueryDSL | 5.1.0 |
| 빌드 | Maven (Wrapper 3.3.4 포함) | 3.9.14 |
| 패키징 | JAR (내장 Tomcat) | - |
| 코드 포맷 | Spotless (Google Java Format) | 2.43.0 |

---

## 사전 준비

| 도구 | 버전 | 비고 |
|------|------|------|
| JDK | 17 | [Eclipse Temurin](https://adoptium.net/) 권장 |
| IntelliJ IDEA | 최신 | Community 또는 Ultimate |
| Docker Desktop | 4.x | DB 컨테이너 실행용 (infra 레포 참고) |

---

## 최초 세팅

```bash
# 1. 레포 클론
git clone https://github.com/swucjute/api.git

# 2. 환경변수 파일 생성
cd api
cp .env.example .env

# 3. DB 실행 (infra 레포의 docker-compose.local.yml 사용)
cd ../infra
docker compose -f docker-compose.local.yml up -d

# 4. IntelliJ에서 api 프로젝트 열기
#    File > Open > api 폴더 선택
#    Maven 프로젝트로 자동 인식됨
```

### IntelliJ Run Configuration 설정

1. Run > Edit Configurations
2. Spring Boot > ApiApplication 선택 (또는 새로 추가)
3. VM options:
```
-Dspring.profiles.active=local
```
4. Environment variables 옆 아이콘 클릭 > `.env` 파일 경로 지정
   또는 `.env` 파일 내용을 직접 붙여넣기
5. Run 클릭

---

## 로컬 DB 접속 정보

`.env.example`에 기본값이 정의되어 있다. infra 레포의 `docker-compose.local.yml`과 일치시켜야 한다.

`.env` 파일은 `.gitignore`에 포함되어 있으므로 Git에 커밋되지 않는다.

---

## 환경 프로필

| 프로필 | 파일 | 용도 | Swagger | ddl-auto |
|--------|------|------|---------|----------|
| local | application-local.yml | 개발자 PC | 활성화 | update |
| dev | application-dev.yml | dev 서버 (EC2) | 활성화 | update |
| prod | application-prod.yml | 프로덕션 (EC2) | 비활성화 | validate |

- 모든 환경에서 `${DB_USER}` 등 환경변수로 주입.
- local: `.env` 파일에서 관리. `.env.example`에서 복사.
- dev/prod: Docker Compose의 `.env`에서 관리.
- local/dev는 JPA Entity 기준으로 테이블을 자동 생성/수정한다.
- prod는 운영 안전성을 위해 `validate`로 두고, 운영 반영 전 DB 스키마 준비 여부를 확인한다.

---

## DB 스키마 관리

초기 개발 단계에서는 Flyway/Liquibase migration을 사용하지 않고 JPA Entity를 기준으로 스키마를 관리한다.

| 환경 | 방식 | 비고 |
|------|------|------|
| local | `ddl-auto: update` | 로컬 MariaDB에 Entity 기준 테이블 자동 반영 |
| dev | `ddl-auto: update` | dev DB에 Entity 변경 자동 반영 |
| prod | `ddl-auto: validate` | 운영 DB와 Entity 매핑 검증만 수행 |

도메인 구현 시 `entity`와 `repository` 패키지에 JPA Entity와 Spring Data JPA Repository를 추가한다.

```text
domain/{domain}/entity      <- JPA Entity
domain/{domain}/repository  <- Spring Data JPA Repository
domain/{domain}/service     <- 비즈니스 로직
domain/{domain}/controller  <- API 라우터
```

운영 데이터가 쌓이고 DB 변경 이력 관리가 필요해지는 시점에는 Flyway 같은 migration 도구 도입을 다시 검토한다.

---

## 프로젝트 구조

```
api/
├── .github/
│   ├── workflows/
│   │   └── ci.yml
│   ├── ISSUE_TEMPLATE/
│   └── PULL_REQUEST_TEMPLATE.md
├── src/main/java/com/swucjute/api/
│   ├── ApiApplication.java
│   ├── domain/                           <- 도메인별 패키지
│   │   ├── auth/
│   │   │   ├── controller/
│   │   │   ├── service/
│   │   │   ├── entity/
│   │   │   ├── repository/
│   │   │   └── dto/
│   │   ├── member/
│   │   ├── worship/
│   │   ├── platform/
│   │   └── home/
│   └── global/                           <- 공통 모듈
│       ├── common/
│       │   ├── ApiResponse.java          <- 통일 응답 형식
│       │   └── BaseEntity.java           <- createdAt, updatedAt 자동 관리
│       ├── config/
│       │   ├── SecurityConfig.java       <- Spring Security 설정
│       │   ├── SwaggerConfig.java        <- Swagger UI 설정
│       │   ├── RedisConfig.java          <- Redis 설정
│       │   └── JpaConfig.java            <- JPA Auditing 설정
│       ├── security/
│       │   ├── JwtTokenProvider.java     <- JWT 토큰 생성/검증
│       │   ├── JwtAuthenticationFilter.java
│       │   └── CustomUserDetailsService.java
│       └── exception/
│           ├── GlobalExceptionHandler.java <- 전역 예외 처리
│           ├── ErrorCode.java            <- 에러 코드 enum
│           └── CustomException.java      <- 커스텀 예외
├── src/main/resources/
│   ├── application.yml                   <- 공통 설정
│   ├── application-local.yml             <- 로컬 (환경변수 참조)
│   ├── application-dev.yml               <- dev 서버
│   └── application-prod.yml              <- 프로덕션
├── src/test/java/com/swucjute/api/
│   └── ApiApplicationTests.java
├── .env.example                         <- 환경변수 템플릿 (cp .env.example .env)
├── docs/
│   └── CONTRIBUTING.md                  <- 커밋/브랜치/PR/코딩 규칙
├── Dockerfile
├── mvnw / mvnw.cmd                      <- Maven Wrapper
├── pom.xml
└── .gitignore
```

---

## API 응답 형식

모든 API는 통일된 형식으로 응답한다:

```json
// 성공
{
  "status": 200,
  "message": "성공",
  "data": { ... }
}

// 실패
{
  "status": 400,
  "message": "제목은 필수입니다",
  "data": null
}
```

---

## API URL 규칙

```
기본 형식: /api/v1/{리소스 복수형}

GET    /api/v1/boards          <- 목록 조회
GET    /api/v1/boards/{id}     <- 단건 조회
POST   /api/v1/boards          <- 생성
PUT    /api/v1/boards/{id}     <- 수정
DELETE /api/v1/boards/{id}     <- 삭제
```

## 현재 API/DB 설계 산출물

최종 엑셀 명세를 기준으로 라우터 골격과 확인용 DB 파일을 먼저 잡아두었다.

| 항목 | 위치 | 비고 |
|------|------|------|
| API 라우트 골격 | `src/main/java/com/swucjute/api/domain/**/controller` | 현재 스텁은 `501 NOT_IMPLEMENTED` 반환 |
| 도메인 서비스 골격 | `src/main/java/com/swucjute/api/domain/**/service` | 담당자별 실제 로직 구현 시작점 |
| API 골격 설명 | `docs/api/route-scaffold.md` | 라우트/서비스 진입점 요약 |
| 확인용 DB DDL | 별도 전달 파일 | 레포의 실제 DB 구조는 JPA Entity 기준으로 관리 |

---

## 빌드 및 실행

### Maven Wrapper 사용

Maven을 직접 설치할 필요 없다. 프로젝트에 포함된 `mvnw`(Mac/Linux) 또는 `mvnw.cmd`(Windows)를 사용하면 Maven 3.9.14가 자동으로 다운로드되어 실행된다.

```bash
# 빌드
./mvnw clean package

# 테스트 제외 빌드
./mvnw clean package -DskipTests

# 코드 포맷 체크
./mvnw spotless:check

# 코드 포맷 자동 적용
./mvnw spotless:apply

# 실행 (IDE 없이)
java -Dspring.profiles.active=local -jar target/api-0.0.1-SNAPSHOT.jar
```

> Windows에서는 `./mvnw` 대신 `mvnw.cmd`를 사용한다.

### Docker 빌드

```bash
# JAR 빌드 후 Docker 이미지 생성
./mvnw clean package -DskipTests
docker build -t ghcr.io/swucjute/api:dev .
```

---

## 확인 URL

Spring Boot 실행 후:

| 항목 | URL |
|------|-----|
| Swagger UI | http://localhost:8080/swagger-ui.html |
| API Docs | http://localhost:8080/v3/api-docs |

---

## 기여 가이드

커밋 컨벤션, 브랜치 전략, PR 규칙, 코딩 규칙은 [CONTRIBUTING.md](docs/CONTRIBUTING.md)를 참고한다.
