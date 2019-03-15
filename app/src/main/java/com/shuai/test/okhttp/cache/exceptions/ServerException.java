package com.shuai.test.okhttp.cache.exceptions;

/**
 * Created by lixiaofan on 2017/7/19.
 */
public class ServerException extends RuntimeException {

    public int code;

    public String msg;

    public ServerException(String msg){
        super(msg);
        this.msg = msg;
    }

    public ServerException(int code, String msg){
        super(msg);
        this.msg = msg;
        this.code = code;
    }


}