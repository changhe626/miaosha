package com.onyx.miaosha.controller;

import com.onyx.miaosha.domain.model.User;
import com.onyx.miaosha.rabbitmq.MQSender;
import com.onyx.miaosha.redis.RedisService;
import com.onyx.miaosha.redis.UserKey;
import com.onyx.miaosha.result.CodeMsg;
import com.onyx.miaosha.result.Result;
import com.onyx.miaosha.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * controller  中两类
 * 1.是restAPI
 * 2.是页面跳转
 *
 *
 * 3.返回结果的封装
 * {
 *   "code":500
 *   "msg":"hahah"
 *   "data":{}
 * }
 */
@Controller
public class DemoController {


    @GetMapping("hi1")
    @ResponseBody
    public Result<Object>  Hello(){
        return Result.success("success");
    }

    @GetMapping("hi2")
    @ResponseBody
    public Result<Object>  Hello2(){
        return Result.fail(CodeMsg.SUCCESS);
    }

    @GetMapping("hi3")
    @ResponseBody
    public Result<Object>  fail(){
        return Result.fail(CodeMsg.SERVER_ERROR);
    }


    @RequestMapping("hello")
    public String thymeleaf(Model model){
        model.addAttribute("name","zahaojun");
        return "hello";

    }

    @Autowired
    private UserService userService;

    @GetMapping("db")
    @ResponseBody
    public Result<Object>  db(){
        User user = userService.getById(1);
        return Result.success(user);
    }



    @GetMapping("tran")
    @ResponseBody
    public Result<Object>  tran(){
        userService.insert();
        return Result.success("");
    }

    @Autowired
    private RedisService redisService;

    @GetMapping("redis/set")
    @ResponseBody
    public Result<Object>  redis(){
        User user = new User(3, "hhaha");
        redisService.set(UserKey.getByID,"key1",user);//UserKey:idkey1   这种类型的了
        return Result.success("age");
    }


    @GetMapping("redis/get")
    @ResponseBody
    public Result<Object>  redisGet(){
        User user = redisService.get(UserKey.getByID,"key1", User.class);
        return Result.success(user);
    }


    @Autowired
    private MQSender mqSender;

    /*@GetMapping("mq")
    @ResponseBody
    public void   mq(){
        mqSender.send("hello world");
    }


    @GetMapping("mq/topic")
    @ResponseBody
    public void   mqTopic(){
        mqSender.sendTopic("hello world");
    }

    @GetMapping("mq/fanout")
    @ResponseBody
    public void   mqFanout(){
        mqSender.sendFanout("hello world");
    }


    @GetMapping("mq/header")
    @ResponseBody
    public void   mqHeader(){
        mqSender.sendHeaders("hello world");
    }*/

}
