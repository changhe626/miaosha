package com.onyx.miaosha.controller;


import com.onyx.miaosha.redis.RedisService;
import com.onyx.miaosha.result.Result;
import com.onyx.miaosha.service.MiaoshaUserService;
import com.onyx.miaosha.vo.LoginVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Controller
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private MiaoshaUserService miaoshaUserService;


    private static Logger log= LoggerFactory.getLogger(LoginController.class);

    @GetMapping("to_login")
    public String toLogin(){
        return "login";

    }


    @PostMapping("/do_login")
    @ResponseBody
    public Result<Object> login(@Valid LoginVo loginVo, HttpServletResponse response){
        log.info(loginVo.toString());
        System.out.println(loginVo.toString());
        //参数校验
        /*String password = loginVo.getPassword();
        String mobile = loginVo.getMobile();
        if(StringUtils.isBlank(mobile)){
            return Result.fail(CodeMsg.MOBILE_EMPTY);
        }
        if(StringUtils.isBlank(password)){
            return Result.fail(CodeMsg.PASSWORD_EMPTY);
        }
        if(!ValidatorUtil.isMobile(mobile)){
            return Result.fail(CodeMsg.MOBILE_ERROR);
        }
        使用注解的方式进行参数的校验
        */

        //CodeMsg msg= miaoshaUserService.login(loginVo);
        /*if(msg.getCode()==0){
            log.info("登陆成功");
            return Result.success(true);
        }else {
            log.info("登陆失败");
            return Result.fail(msg);
        }*/

        boolean login = miaoshaUserService.login(response,loginVo);
        System.out.println("success");
        return Result.success(true);

    }


    @ResponseBody
    @PostMapping("test")
    public String test(){
        System.out.println("1");
        return "hello";
    }





}
