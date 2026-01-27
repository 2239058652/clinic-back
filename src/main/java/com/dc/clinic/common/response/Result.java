package com.dc.clinic.common.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {
    private Integer code; // 状态码：200 成功，500 失败
    private String message; // 提示信息
    private T data; // 返回的数据
    private Long timestamp; // 时间戳

    public Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    // 成功相关
    public static <T> Result<T> success() {
        return success(null);
    }

    public static <T> Result<T> success(T data) {
        return success("操作成功", data);
    }

    public static <T> Result<T> success(String message, T data) {
        return new Result<>(200, message, data);
    }

    // 失败相关
    public static <T> Result<T> error() {
        return error("操作失败");
    }

    public static <T> Result<T> error(String message) {
        return error(500, message);
    }

    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message, null);
    }

    // 常用状态码
    public static <T> Result<T> badRequest(String message) {
        return error(400, message);
    }

    public static <T> Result<T> unauthorized(String message) {
        return error(401, message);
    }

    public static <T> Result<T> forbidden(String message) {
        return error(403, message);
    }

    public static <T> Result<T> notFound(String message) {
        return error(404, message);
    }

    public static <T> Result<T> methodNotAllowed(String message) {
        return error(405, message);
    }

    public static <T> Result<T> internalError(String message) {
        return error(500, message);
    }

    // 便捷方法
    public boolean isSuccess() {
        return code != null && code == 200;
    }

    public boolean isError() {
        return !isSuccess();
    }
}