package com.example.noteapp.exception;

import com.example.noteapp.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Bắt tất cả các lỗi RuntimeException (ví dụ: lỗi logic code)
    @ExceptionHandler(value = RuntimeException.class)
    ResponseEntity<ApiResponse> handlingRuntimeException(RuntimeException exception) {
        ApiResponse apiResponse = new ApiResponse();

        apiResponse.setCode(9999); // Mã lỗi mặc định
        apiResponse.setMessage(exception.getMessage());

        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse> handlingValidation(MethodArgumentNotValidException exception) {
        // Lấy lỗi đầu tiên tìm thấy
        String enumKey = exception.getFieldError().getDefaultMessage();

        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(400); // Bad Request
        apiResponse.setMessage(enumKey); // Trả về câu thông báo lỗi (VD: "Email không hợp lệ")

        return ResponseEntity.badRequest().body(apiResponse);
    }
    // Bạn có thể thêm các ExceptionHandler khác ở đây (ví dụ MethodArgumentNotValidException)
}