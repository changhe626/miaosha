package com.onyx.miaosha.rabbitmq;

import com.onyx.miaosha.redis.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MQSender {
    private static Logger log= LoggerFactory.getLogger(MQSender.class);

    @Autowired
    private AmqpTemplate amqpTemplate;

    public void sendMiaoshaMessage(MiaoshaMessage mm) {
        String msg = RedisService.beanToString(mm);
        log.info("send message:"+msg);
        amqpTemplate.convertAndSend(MQConfig.MIAOSHA_QUEUE, msg);
    }


   /* public void send(Object message){
        String string = RedisService.beanToString(message);
        log.info("send :"+string);
        amqpTemplate.convertAndSend(MQConfig.QUEUE_NAME,string);
    }

    *//**
     * 默认guest 是不能远程连接的....只能本地连接
     * 或者在配置文件中加入:没有就新建
     * rabbitmq.config
     * 内容是:  [{rabbitmq,[{loopback_users,[]}]}],
     *
     * 再重启下就好了
     *
     *//*


    public void sendTopic(Object message){
        String string = RedisService.beanToString(message);
        log.info("Topic 模式:send :"+string);
        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE,MQConfig.ROUTING_KEY1,string+"1");
        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE,"topic.key2",string+"2");
    }



    public void sendFanout(Object message){
        String string = RedisService.beanToString(message);
        log.info("广播模式:send :"+string);
        amqpTemplate.convertAndSend(MQConfig.FANOUT_EXCHANGE,"",string+"1");
    }




    public void sendHeaders(Object message){
        String string = RedisService.beanToString(message);
        log.info("Headers模式:send :"+string);
        MessageProperties properties = new MessageProperties();
        properties.setHeader("head1","value1");
        properties.setHeader("head2","value2");
        Message obj = new Message(string.getBytes(),properties);
        amqpTemplate.convertAndSend(MQConfig.HEADERS_EXCHANGE,"",obj);
    }
*/


}
