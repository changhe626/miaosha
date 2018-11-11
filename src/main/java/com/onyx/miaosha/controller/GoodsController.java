package com.onyx.miaosha.controller;

import com.onyx.miaosha.domain.MiaoshaUser;
import com.onyx.miaosha.redis.RedisService;
import com.onyx.miaosha.service.MiaoshaUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("goods")
public class GoodsController {


    @Autowired
    private RedisService redisService;
    @Autowired
    private MiaoshaUserService miaoshaUserService;

    /**
     * 改进后,自动化进行Bean 的注入
     * @param model
     * @return
     */
    @RequestMapping("to_list")
    public String toList(Model model, MiaoshaUser user){
        model.addAttribute("user",user);
        return "goods_list";
    }


    /*  改进前
    @RequestMapping("to_list")
    public String toList(Model model,
                         @CookieValue(value = MiaoshaUserService.COOK_NAME_TOKEN,required = false)String cookieToken,
                         @RequestParam(value =MiaoshaUserService.COOK_NAME_TOKEN,required = false)String paramToken,
                         HttpServletResponse response){

        if(StringUtils.isBlank(cookieToken) && StringUtils.isBlank(paramToken)){
            return "login/to_login";
        }
        //优先的是cookie中的
        String token=StringUtils.isBlank(cookieToken)?paramToken:cookieToken;
        //没有直接在controller中 从redis中获取user,还是定义了一个方法,为了分层的清晰
        MiaohaUser user=miaoshaUserService.getByToken(token,response);
        model.addAttribute("user",user);
        return "goods_list";
    }*/



}