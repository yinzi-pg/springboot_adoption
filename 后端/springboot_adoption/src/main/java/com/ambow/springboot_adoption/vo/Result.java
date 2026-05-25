package com.ambow.springboot_adoption.vo;

public class Result<T> {
    private   Integer code;   //状态码
    private String message;   //携带的返回信息
    private T data;  //携带的返回数据

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
