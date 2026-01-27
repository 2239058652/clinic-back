package com.dc.clinic.common.exception;

import com.dc.clinic.common.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;

@Slf4j
@RestControllerAdvice // 👈 拦截所有 Controller 抛出的异常
public class GlobalExceptionHandler {

    /**
     * 处理自定义业务异常
     */
    @ExceptionHandler(RuntimeException.class)
    public Result<String> handleRuntimeException(RuntimeException e) {
        log.error("运行时异常: ", e);
        return Result.error(e.getMessage());
    }

    /**
     * 处理 Spring Security 权限异常
     */
    @ExceptionHandler(AccessDeniedException.class)
    public Result<String> handleAccessDeniedException(AccessDeniedException e) {
        return Result.error(403, "没有权限访问该资源");
    }

    /**
     * 处理参数校验异常 (比如 @NotBlank 没通过)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<String> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldError().getDefaultMessage();
        return Result.error(message);
    }

    /**
     * 处理系统未知的最大异常
     */
    @ExceptionHandler(Exception.class)
    public Result<String> handleException(Exception e) {
        log.error("系统未知异常: ", e);
        return Result.error("服务器开小差了，请稍后再试");
    }
}