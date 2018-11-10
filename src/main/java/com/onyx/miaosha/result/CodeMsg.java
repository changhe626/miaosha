package com.onyx.miaosha.result;

/**
 * 只要get不要set,进行更好的封装
 */
public class CodeMsg {

    private int code;
    private String msg;

    //通用的异常
    public static CodeMsg SUCCESS=new CodeMsg(0,"success");

    public static CodeMsg SERVER_ERROR=new CodeMsg(500,"服务端异常");

    //登陆模块异常....600
    //商品模块...700
    //订单...800

    private CodeMsg(int code, String msg) {
        this.code=code;
        this.msg=msg;
    }


    public int getCode() {
        return code;
    }



    public String getMsg() {
        return msg;
    }



}
