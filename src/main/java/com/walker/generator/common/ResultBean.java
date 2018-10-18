package com.walker.generator.common;

import java.io.Serializable;

public class ResultBean<T> implements Serializable {

    private int code;

    private String message;

    private T data;

    private static final int DEFAULT_SUCCESS = 200;

    private static final String DEFAULT_SUCCESS_MESSAGE = "success";

    public ResultBean() {
    }

    public ResultBean(T data) {
        this.code = DEFAULT_SUCCESS;
        this.message = DEFAULT_SUCCESS_MESSAGE;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static <T> ResultBean<T> success() {
        return new ResultBean<>(null);
    }

    public static <T> ResultBean<T> success(T data) {
        return new ResultBean<>(data);
    }
}
