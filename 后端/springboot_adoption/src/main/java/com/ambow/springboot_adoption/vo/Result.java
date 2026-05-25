package com.ambow.springboot_adoption.vo;


import lombok.NoArgsConstructor;

@NoArgsConstructor
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



    public Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    //返回成功，不带数据(啥也不带的)
    public static Result success() {
        return new Result(0,"成功",null);
    }
    //返回成功，带数据
    public static <T> Result success(T data) {

        return new Result(0,"操作成功",data);
    }
    //返回失败
    public static Result error(String message) {

        return new Result(1,message,null);
    }
}
