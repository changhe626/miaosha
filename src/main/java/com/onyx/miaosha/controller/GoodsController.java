package com.onyx.miaosha.controller;

import com.onyx.miaosha.domain.Goods;
import com.onyx.miaosha.domain.MiaoshaUser;
import com.onyx.miaosha.redis.GoodsKey;
import com.onyx.miaosha.redis.RedisService;
import com.onyx.miaosha.result.Result;
import com.onyx.miaosha.service.GoodsService;
import com.onyx.miaosha.service.MiaoshaUserService;
import com.onyx.miaosha.vo.GoodsVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.IContext;
import org.thymeleaf.spring4.context.SpringWebContext;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("goods")
public class GoodsController {


    @Autowired
    private RedisService redisService;
    @Autowired
    private MiaoshaUserService miaoshaUserService;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private ThymeleafViewResolver thymeleafViewResolver;
    @Autowired
    private ApplicationContext applicationContext;

    /**
     * 改进后,自动化进行Bean 的注入
     *
     * @param model
     * @return 优化前: QPS  1267
     */
    @RequestMapping(value = "to_list", produces = "text/html")
    @ResponseBody
    public String toList(Model model, MiaoshaUser user,
                         HttpServletRequest request, HttpServletResponse response) {
        model.addAttribute("user", user);
        List<GoodsVo> vos = goodsService.listGoodsVo();
        model.addAttribute("goodsList", vos);
        //return "goods_list";

        //取出缓存
        String html = redisService.get(GoodsKey.getGoodsList, "", String.class);
        if (html != null) {
            return html;
        }
        //为空,手动渲染
        IContext context = new SpringWebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap(), applicationContext);
        html = thymeleafViewResolver.getTemplateEngine().process("goods_list", context);
        if(StringUtils.isNoneBlank(html)){
            redisService.set(GoodsKey.getGoodsList,"",html);
        }
        return html;
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


    @RequestMapping(value = "to_detail/{id}",produces = "text/html")
    @ResponseBody
    public String detail(@PathVariable("id") long id,
                         Model model, MiaoshaUser user,
                         HttpServletRequest request, HttpServletResponse response) {

        //取出缓存
        String html = redisService.get(GoodsKey.getGoodsDetail, ""+id, String.class);
        if (html != null) {
            return html;
        }

        model.addAttribute("user", user);
        GoodsVo good = goodsService.getById(id);
        model.addAttribute("goods", good);
        long start = good.getStartDate().getTime();
        long end = good.getEndDate().getTime();
        long now = System.currentTimeMillis();

        int miaoshaStatus = 0;
        long remainSeconds = 0;

        if (now < start) {
            //秒杀未开始
            miaoshaStatus = 0;
            remainSeconds = (long) (start - now) / 1000;
        } else if (now > end) {
            //秒杀结束
            miaoshaStatus = 2;
            remainSeconds = -1;
        } else {
            //进行中
            miaoshaStatus = 1;
            remainSeconds = 0;
        }

        model.addAttribute("miaoshaStatus", miaoshaStatus);
        model.addAttribute("remainSeconds", remainSeconds);

        //return "goods_detail";

        //为空,手动渲染
        SpringWebContext context = new SpringWebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap(), applicationContext);
        html = thymeleafViewResolver.getTemplateEngine().process("goods_detail", context);
        if(StringUtils.isNoneBlank(html)){
            redisService.set(GoodsKey.getGoodsDetail,""+id,html);
        }

        return html;
    }


}
