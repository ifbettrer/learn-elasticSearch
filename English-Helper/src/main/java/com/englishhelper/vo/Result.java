package com.englishhelper.vo;

public class Result<T> {  //<T>泛型
    public int code;
    public String msg;
    public T data;

    public static <T> Result success(){  //没有data参数，不传入data
        Result r = new Result(0, "suc", null);
        /*r.code = 0;
        r.msg = "suc";*/
        return  r;
    }

    public static <T> Result success(T data){
        Result r = new Result(0, "suc", data);
        /*r.code = 0;
        r.msg = "suc";
        r.data = data;*/
        return  r;
    }

    private Result(int code, String msg, T data){
        this.code = code;
        this.msg = msg;
        this.data = data;
    }
}
