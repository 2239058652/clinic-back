package com.dental.clinic.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "统一响应结果")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Result<T> {
    @Schema(description = "响应码", example = "200")
    private int code;

    @Schema(description = "响应消息", example = "success")
    private String message;

    @Schema(description = "响应数据")
    private T data;

    // 原有方法
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.code = 200;
        result.message = "success";
        result.data = data;
        return result;
    }

    // 新增方法：允许自定义消息和数据
    public static <T> Result<T> success(String message, T data) {
        Result<T> result = new Result<>();
        result.code = 200;
        result.message = message;
        result.data = data;
        return result;
    }

    // 新增方法：仅返回成功消息，无数据
    public static <T> Result<T> ok(String message) {
        Result<T> result = new Result<>();
        result.code = 200;
        result.message = message;
        // data 保持为 null
        return result;
    }

    // 原有方法
    public static <T> Result<T> error(String message) {
        Result<T> result = new Result<>();
        result.code = 500;
        result.message = message;
        // data 保持为 null
        return result;
    }

    // 原有方法
    public static <T> Result<T> error(int code, String message) {
        Result<T> result = new Result<>();
        result.code = code;
        result.message = message;
        // data 保持为 null
        return result;
    }
}