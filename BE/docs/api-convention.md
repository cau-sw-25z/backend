# API Convention

## 1. 문서 목적

본 문서는 백엔드 API 응답 형식과 예외 처리 방식, HTTP 상태 코드 사용 규칙을 통일하기 위해 작성한다.

모든 API는 일관된 응답 구조를 사용하며, 성공 응답과 실패 응답 모두 `ApiResponse<T>` 형식을 따른다. 이를 통해 프론트엔드에서는 Axios 인터셉터 또는 공통 응답 처리 로직에서 `success`, `data`, `message`, `code` 값을 기준으로 응답을 일관되게 처리할 수 있다.

---

## 2. API Response Format

모든 API 응답은 아래의 공통 응답 형식을 따른다.

### 2.1 Success Response

```json
{
  "success": true,
  "data": {},
  "message": "요청이 성공했습니다.",
  "code": "SUCCESS"
}
```

### 2.2 Error Response

```json
{
  "success": false,
  "data": null,
  "message": "에러 메시지",
  "code": "ERROR_CODE"
}
```

---

## 3. ApiResponse Field Description

| Field | Type | Description |
|---|---|---|
| success | boolean | 요청 성공 여부를 나타낸다. 성공이면 `true`, 실패이면 `false`를 반환한다. |
| data | T | 실제 응답 데이터를 담는다. 실패 응답에서는 `null`을 반환한다. |
| message | String | 클라이언트에게 전달할 응답 메시지이다. |
| code | String | 응답 상태를 구분하기 위한 커스텀 코드이다. |

---

## 4. HTTP Status Code Convention

본 프로젝트는 API 응답의 일관성을 위해 아래 HTTP 상태 코드 규칙을 따른다.

| Status Code | Name | 사용 상황 |
|---|---|---|
| 200 | OK | 요청이 정상적으로 처리된 경우 사용한다. 조회, 수정, 삭제 성공 시 사용한다. |
| 201 | Created | 새로운 리소스가 생성된 경우 사용한다. 회원가입, 게시글 작성, 댓글 작성 등에 사용한다. |
| 400 | Bad Request | 클라이언트의 요청값이 잘못된 경우 사용한다. 필수값 누락, 형식 오류, 유효성 검증 실패 등에 사용한다. |
| 401 | Unauthorized | 인증이 필요한 요청에서 인증 정보가 없거나 유효하지 않은 경우 사용한다. |
| 403 | Forbidden | 인증은 되었지만 해당 리소스에 접근 권한이 없는 경우 사용한다. |
| 404 | Not Found | 요청한 리소스를 찾을 수 없는 경우 사용한다. |
| 500 | Internal Server Error | 서버 내부에서 예상하지 못한 오류가 발생한 경우 사용한다. |

---

## 5. Error Code Convention

공통 에러 코드는 `ErrorCode` enum에서 관리한다.

| Error Code | HTTP Status | Message |
|---|---|---|
| COMMON_400 | 400 | 잘못된 요청입니다. |
| COMMON_401 | 401 | 인증이 필요합니다. |
| COMMON_403 | 403 | 접근 권한이 없습니다. |
| COMMON_404 | 404 | 요청한 리소스를 찾을 수 없습니다. |
| COMMON_500 | 500 | 서버 내부 오류가 발생했습니다. |
| VALIDATION_400 | 400 | 입력값 검증에 실패했습니다. |

---

## 6. Exception Handling Convention

전역 예외 처리는 `@RestControllerAdvice`를 사용한 `GlobalExceptionHandler`에서 담당한다.

### 6.1 CustomException

서비스 로직에서 의도적으로 발생시키는 예외는 `CustomException`을 사용한다.

예시:

```text
throw new CustomException(ErrorCode.NOT_FOUND);
```

응답 예시:

```text
{
  "success": false,
  "data": null,
  "message": "요청한 리소스를 찾을 수 없습니다.",
  "code": "COMMON_404"
}
```

### 6.2 MethodArgumentNotValidException

`@Valid`를 사용한 요청값 검증에 실패하면 `MethodArgumentNotValidException`을 처리한다.

응답 예시:

```json
{
  "success": false,
  "data": null,
  "message": "email: 이메일 형식이 올바르지 않습니다.",
  "code": "VALIDATION_400"
}
```

### 6.3 Exception

예상하지 못한 서버 내부 오류는 `Exception` 핸들러에서 처리한다.

응답 예시:

```json
{
  "success": false,
  "data": null,
  "message": "서버 내부 오류가 발생했습니다.",
  "code": "COMMON_500"
}
```

---

## 7. Frontend 협업 사항

프론트엔드는 모든 API 응답에서 아래 필드를 기준으로 공통 처리한다.

| Field | 처리 방식 |
|---|---|
| success | 요청 성공 여부 판단 |
| data | 실제 화면에 사용할 응답 데이터 |
| message | 사용자에게 보여줄 메시지 또는 에러 토스트 문구 |
| code | 에러 유형 구분 및 분기 처리 |

프론트엔드 Axios 인터셉터에서는 `success` 값이 `false`인 경우 `message`를 이용해 공통 에러 토스트 또는 모달을 표시할 수 있다.

---

## 8. Backend 협업 사항

백엔드 팀은 기능 개발 중 새로운 예외 상황이 생기면 `ErrorCode` enum에 에러 코드를 추가한다.

에러 코드 추가 시 아래 기준을 따른다.

| Prefix | Meaning |
|---|---|
| COMMON | 공통 에러 |
| VALIDATION | 입력값 검증 에러 |
| AUTH | 인증/인가 관련 에러 |
| USER | 사용자 관련 에러 |
| POST | 게시글 관련 에러 |

예시:

```text
USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_404", "사용자를 찾을 수 없습니다.");
```

---

## 9. 정리

본 프로젝트는 모든 API 응답에 `ApiResponse<T>` 형식을 적용한다.

성공 응답은 `success: true`, 실패 응답은 `success: false`를 반환한다.  
예외 처리는 `GlobalExceptionHandler`에서 일괄 처리하며, 공통 에러 정보는 `ErrorCode` enum에서 관리한다.

이를 통해 API 응답 구조의 일관성을 유지하고, 프론트엔드와 백엔드 간 협업 효율을 높인다.