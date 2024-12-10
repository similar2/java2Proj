package org.example.java2final.config;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Result<T> {
    int code;
    String message;
    T data;

    public static <T> Result<T> success(String message, T data) {
        return new Result<>(0, message, data);
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(0, "成功", data);
    }

    public static <T> Result<T> success() {
        return Result.success(null);
    }

    public static <T> Result<T> error(String message) {
        return new Result<>(-1, message, null);
    }
}
