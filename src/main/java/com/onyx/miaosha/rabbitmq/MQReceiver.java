package com.onyx.miaosha.rabbitmq;

import com.onyx.miaosha.domain.MiaoshaOrder;
import com.onyx.miaosha.domain.MiaoshaUser;
import com.onyx.miaosha.domain.OrderInfo;
import com.onyx.miaosha.redis.RedisService;
import com.onyx.miaosha.service.GoodsService;
import com.onyx.miaosha.service.MiaoshaService;
import com.onyx.miaosha.service.OrderService;
import com.onyx.miaosha.vo.GoodsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MQReceiver {
    private static Logger log= LoggerFactory.getLogger(MQReceiver.class);

    @Autowired
    private MiaoshaService miaoshaService;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private RedisService redisService;


    @RabbitListener(queues = MQConfig.MIAOSHA_QUEUE)
    public void receiverMiaosha(String message){
        log.info("receiver message:"+message);
        MiaoshaMessage bean = RedisService.stringToBean(message, MiaoshaMessage.class);
        long goodsId = bean.getGoodsId();
        MiaoshaUser user = bean.getUser();
        //判断库存
        GoodsVo goodsVo = goodsService.getById(goodsId);
        if(goodsVo.getStockCount()<1){
            return;
        }
        //判断是否已经秒杀到了
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
        if(order!=null){
            return;
        }
        //写入秒杀订单
        OrderInfo orderInfo = miaoshaService.miaosha(user, goodsVo);
    }




    /*@RabbitListener(queues = MQConfig.QUEUE_NAME)
    public void receiver(String message){
        log.info("receiver message:"+message);
    }

    @RabbitListener(queues = MQConfig.QUEUE_QUEUE1)
    public void topicQueue1(String message){
        log.info("topic queue1 message:"+message);
    }


    @RabbitListener(queues = MQConfig.QUEUE_QUEUE2)
    public void topicQueue2(String message){
        log.info("topic queue2 message:"+message);
    }


    @RabbitListener(queues = MQConfig.HEADERS_QUEUE)
    public void head(byte[] message){
        log.info("headers queue message:"+new String(message));
    }*/
}
