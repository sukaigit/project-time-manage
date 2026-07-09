package com.ptm.util;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseResult<T> {
    private int code;
    private String msg;
    private T data;

    public static <T> ResponseResult<T> success(T data) {
        return new ResponseResult<>(200, "success", data);
    }

    public static <T> ResponseResult<T> error(int code, String msg) {
        return new ResponseResult<>(code, msg, null);
    }

    public static <T> ResponseResult<T> unauthorized() {
        return new ResponseResult<>(401, "未登录或会话超时", null);
    }

    public static <T> ResponseResult<T> forbidden() {
        return new ResponseResult<>(403, "无权限", null);
    }
}
