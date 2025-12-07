package com.example.noteapp.exception;

import com.example.noteapp.dto.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@ControllerAdvice
public class GlobalExceptionHandler {

    // 1. Lỗi Runtime chung chung (Giữ nguyên) -> 400 Bad Request
    @ExceptionHandler(value = RuntimeException.class)
    ResponseEntity<ApiResponse> handlingRuntimeException(RuntimeException exception) {
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(9999);
        apiResponse.setMessage(exception.getMessage());
        return ResponseEntity.badRequest().body(apiResponse);
    }

    // 2. Lỗi Validate dữ liệu (@Valid) -> 400 Bad Request
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse> handlingValidation(MethodArgumentNotValidException exception) {
        String enumKey = exception.getFieldError().getDefaultMessage();
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(400);
        apiResponse.setMessage(enumKey);
        return ResponseEntity.badRequest().body(apiResponse);
    }

    // 3. Lỗi Không có quyền truy cập (Access Denied) -> 403 Forbidden
    @ExceptionHandler(value = AccessDeniedException.class)
    ResponseEntity<ApiResponse> handlingAccessDenied(AccessDeniedException exception) {
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(403);
        apiResponse.setMessage("Bạn không có quyền thực hiện hành động này");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiResponse);
    }

    // 4. Lỗi Sai tài khoản/mật khẩu -> 401 Unauthorized
    @ExceptionHandler(value = BadCredentialsException.class)
    ResponseEntity<ApiResponse> handlingBadCredentials(BadCredentialsException exception) {
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(401);
        apiResponse.setMessage("Tài khoản hoặc mật khẩu không chính xác");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponse);
    }

    // 5. Lỗi Upload file quá nặng -> 413 Payload Too Large
    @ExceptionHandler(value = MaxUploadSizeExceededException.class)
    ResponseEntity<ApiResponse> handlingMaxUploadSize(MaxUploadSizeExceededException exception) {
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(413);
        apiResponse.setMessage("File tải lên quá lớn (Tối đa 10MB)");
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(apiResponse);
    }
}