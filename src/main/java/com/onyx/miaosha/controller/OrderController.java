package com.onyx.miaosha.controller;

import com.onyx.miaosha.domain.MiaoshaUser;
import com.onyx.miaosha.domain.OrderInfo;
import com.onyx.miaosha.redis.RedisService;
import com.onyx.miaosha.result.CodeMsg;
import com.onyx.miaosha.result.Result;
import com.onyx.miaosha.service.GoodsService;
import com.onyx.miaosha.service.OrderService;
import com.onyx.miaosha.vo.GoodsVo;
import com.onyx.miaosha.vo.OrderDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("order")
public class OrderController {

    @Autowired
    private RedisService redisService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private GoodsService goodsService;


    //@NeedLogin   通过注解,进行是否登录的拦截.写一个拦截器
    @RequestMapping("detail")
    @ResponseBody
    public Result<OrderDetailVo>  detail(MiaoshaUser user, @RequestParam("orderId")long orderId){
        if(user==null){
            return Result.fail(CodeMsg.NO_USER);
        }
        OrderInfo info=orderService.getOrderById(orderId);
        if(info==null){
            return Result.fail(CodeMsg.NO_ORDER);
        }
        GoodsVo goodsVo = goodsService.getById(info.getGoodsId());
        OrderDetailVo detailVo = new OrderDetailVo();
        detailVo.setGoods(goodsVo);
        detailVo.setOrder(info);
        return Result.success(detailVo);
    }


}
