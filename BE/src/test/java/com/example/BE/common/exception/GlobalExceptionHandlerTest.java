package com.example.BE.common.exception;

import com.example.BE.common.response.ApiResponse;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();

    @Test
    void handleCustomException() {
        CustomException exception = new CustomException(ErrorCode.NOT_FOUND);

        ResponseEntity<ApiResponse<Void>> response =
                globalExceptionHandler.handleCustomException(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getData()).isNull();
        assertThat(response.getBody().getMessage()).isEqualTo("요청한 리소스를 찾을 수 없습니다.");
        assertThat(response.getBody().getCode()).isEqualTo("COMMON_404");
    }

    @Test
    void handleValidationException() throws NoSuchMethodException {
        MethodParameter methodParameter = new MethodParameter(
                TestController.class.getDeclaredMethod("testMethod", TestRequest.class),
                0
        );

        TestRequest target = new TestRequest();
        BeanPropertyBindingResult bindingResult =
                new BeanPropertyBindingResult(target, "testRequest");

        bindingResult.addError(
                new FieldError("testRequest", "name", "이름은 필수입니다.")
        );

        MethodArgumentNotValidException exception =
                new MethodArgumentNotValidException(methodParameter, bindingResult);

        ResponseEntity<ApiResponse<Void>> response =
                globalExceptionHandler.handleValidationException(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getData()).isNull();
        assertThat(response.getBody().getMessage()).contains("이름은 필수입니다.");
        assertThat(response.getBody().getCode()).isEqualTo("VALIDATION_400");
    }

    @Test
    void handleUnexpectedException() {
        RuntimeException exception = new RuntimeException("unexpected server error");

        ResponseEntity<ApiResponse<Void>> response =
                globalExceptionHandler.handleException(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getData()).isNull();
        assertThat(response.getBody().getMessage()).isEqualTo("서버 내부 오류가 발생했습니다.");
        assertThat(response.getBody().getCode()).isEqualTo("COMMON_500");
    }

    static class TestController {
        public void testMethod(TestRequest request) {
        }
    }

    static class TestRequest {
        private String name;
    }
}