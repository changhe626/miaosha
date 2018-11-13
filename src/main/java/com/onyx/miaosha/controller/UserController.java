package com.onyx.miaosha.controller;

import com.onyx.miaosha.domain.MiaoshaUser;
import com.onyx.miaosha.result.Result;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("user")
public class UserController {


    /**
     * 测试用的接口,不断的获取user
     *
     * 根据压测的结果,调整redis的参数,mysql的参数
     *
     * @param user
     * @return
     */
    @RequestMapping("info")
    @ResponseBody
    public Result<Object> info(MiaoshaUser user){
        return Result.success(user);
    }



}
