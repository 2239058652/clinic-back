package com.dc.clinic.common.exception;

import com.dc.clinic.common.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 1. 专门处理业务异常 (ServiceException)
     */
    @ExceptionHandler(ServiceException.class)
    public Result<String> handleServiceException(ServiceException e) {
        // 使用你 Result 类里定义的 error 方法
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 2. 处理 Spring Security 权限不足 (返回 403)
     */
    @ExceptionHandler(AccessDeniedException.class)
    public Result<String> handleAccessDeniedException(AccessDeniedException e) {
        log.warn("用户访问受限: {}", e.getMessage());
        return Result.forbidden("权限不足，请联系管理员"); // 使用你 Result 里的 forbidden 方法
    }

    /**
     * 3. 处理参数校验异常 (比如 @NotBlank)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<String> handleValidationException(MethodArgumentNotValidException e) {
        String message = Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage();
        return Result.badRequest(message);
    }

    /**
     * 4. 兜底处理所有 RuntimeException (防止代码崩溃)
     */
    @ExceptionHandler(RuntimeException.class)
    public Result<String> handleRuntimeException(RuntimeException e) {
        log.error("运行时异常: ", e);
        return Result.internalError(e.getMessage());
    }
}