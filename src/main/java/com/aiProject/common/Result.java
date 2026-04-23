package com.aiProject.common;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Result<T> {
    // getter setter
    private int code;
    private String msg;
    private T data;

    public static <T> Result<T> success(T data) {
        Result<T> r = new Result<>();
        r.setCode(200);
        r.setMsg("成功");
        r.setData(data);
        return r;
    }

    public static <T> Result<T> error(String msg) {
        Result<T> r = new Result<>();
        r.setCode(500);
        r.setMsg(msg);
        return r;
    }

    public static <T> Result<T> error(T data) {
        Result<T> r = new Result<>();
        r.setCode(500);
        r.setMsg("服务异常");
        r.setData(data);
        return r;
    }

    public static <T> Result<T> error(int code, String msg, T data) {
        Result<T> r = new Result<>();
        r.setCode(code);
        r.setMsg(msg);
        r.setData(data);
        return r;
    }

}
