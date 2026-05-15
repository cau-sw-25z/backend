# Swagger Convention

## 1. Swagger UI URL

로컬 실행 후 Swagger UI는 아래 주소에서 확인한다.

```text
http://localhost:8080/swagger-ui.html
```

환경에 따라 아래 주소로 리다이렉트될 수 있다.

```text 
http://localhost:8080/swagger-ui/index.html
```

## 2. API Tag Convention

Controller 단위로 `@Tag`를 사용한다.

```text
@Tag(name = "Health", description = "서버 상태 확인 API")
```

## 3. Tag Naming Rule

| 기능 영역 | Tag Name | Description |
|---|---|---|
| 서버 상태 확인 | Health | 서버 상태 확인 API |
| 인증/인가 | Auth | 로그인, 회원가입, 토큰 관련 API |
| 사용자 | User | 사용자 정보 관련 API |
| 포트폴리오 | Portfolio | 포트폴리오 관리 API |
| 종목 | Stock | 종목 조회 및 관리 API |
| 관심목록 | WatchList | 관심 종목 목록 API |
| 거래 기록 | Trade | 매수/매도 기록 API |

## 4. Operation Convention

각 API 메서드에는 `@Operation`을 작성한다.

```text
@Operation(summary = "Health Check", description = "서버 기동 상태를 확인합니다.")
```

`summary`에는 API의 짧은 설명을 작성한다.

`description`에는 API의 목적과 동작을 구체적으로 작성한다.