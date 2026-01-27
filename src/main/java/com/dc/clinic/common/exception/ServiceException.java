package com.dc.clinic.common.exception;

import lombok.Getter;

/**
 * 自定义业务异常，用于手动抛出业务逻辑错误
 */
@Getter
public class ServiceException extends RuntimeException {
    private final Integer code;

    public ServiceException(String message) {
        this(500, message);
    }

    public ServiceException(Integer code, String message) {
        super(message);
        this.code = code;
    }
}