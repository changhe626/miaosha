package com.onyx.miaosha.controller;

import com.onyx.miaosha.domain.MiaoshaOrder;
import com.onyx.miaosha.domain.MiaoshaUser;
import com.onyx.miaosha.domain.OrderInfo;
import com.onyx.miaosha.result.CodeMsg;
import com.onyx.miaosha.service.GoodsService;
import com.onyx.miaosha.service.MiaoshaService;
import com.onyx.miaosha.service.OrderService;
import com.onyx.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("miaosha")
public class MiaoshaController {

    @Autowired
    private MiaoshaService miaoshaService;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private OrderService orderService;
    //尽量不要在MiaoShaService中引入orderDao,这样结构不清晰,要么就引入orderService....


    @RequestMapping(value = "do_miaosha",method = RequestMethod.POST)
    public String miaoSha(MiaoshaUser user, Model model,@RequestParam("goodsId") long id){
        if(user==null){
            return "login/to_login";
        }
        //判断库存
        GoodsVo goodsVo = goodsService.getById(id);

        if(goodsVo.getStockCount()<1){
            model.addAttribute("errmsg", CodeMsg.COUNT_EMPTY);
            return "miaosha_fail";
        }
        //判断是否已经秒杀到了
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), id);
        if(order!=null){
            model.addAttribute("errmsg", CodeMsg.MIAOSHA_FAIL);
            return "miaosha_fail";
        }

        //开始秒杀,减库存
        //下订单
        //写入秒杀订单
        OrderInfo orderInfo = miaoshaService.miaosha(user, goodsVo);

        model.addAttribute("user",user);
        model.addAttribute("orderInfo",orderInfo);
        model.addAttribute("goods",goodsVo);
        return "order_detail";

    }




}
