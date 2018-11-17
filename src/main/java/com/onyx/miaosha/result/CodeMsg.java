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
    public static CodeMsg PASSWORD_EMPTY=new CodeMsg(501,"密码不能为空");
    public static CodeMsg MOBILE_EMPTY=new CodeMsg(502,"手机号不能为空");
    public static CodeMsg MOBILE_ERROR=new CodeMsg(503,"手机号格式错误");
    public static CodeMsg NO_USER = new CodeMsg(504,"用户不存在");
    public static CodeMsg PASSWORD_ERROR = new CodeMsg(505,"密码错误");

    public static CodeMsg BIND_ERROR = new CodeMsg(506,"绑定异常: %s");

    public static CodeMsg COUNT_EMPTY = new CodeMsg(507,"库存不足");

    public static CodeMsg  MIAOSHA_FAIL= new CodeMsg(508,"不能进行重复秒杀");
    public static CodeMsg  NO_ORDER= new CodeMsg(509,"订单不存在");
    public static CodeMsg  REQUEST_ILLEGAL= new CodeMsg(510,"请求非法");
    public static CodeMsg  CODE_ERROR= new CodeMsg(511,"验证码错误");
    public static CodeMsg  ACCESS_LIMIT= new CodeMsg(512,"访问太频繁,请一分钟之后再试");

    //登陆模块异常....600
    //商品模块...700
    //订单...800


    public CodeMsg fillArgs(Object...args){
        int code=this.code;
        String message=String.format(this.msg,args);
        return new CodeMsg(code,message);
    }

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


    //注意需要重写toString 方法,不然到前端页面是一个对象的地址....
    @Override
    public String toString() {
        return "CodeMsg{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                '}';
    }

}
