package com.onyx.miaosha.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class MQConfig {

    public static final String MIAOSHA_QUEUE = "miaosha.queue";
    public static final String QUEUE_NAME="queue";
    public static final String QUEUE_QUEUE1="topic.queue1";
    public static final String QUEUE_QUEUE2="topic.queue2";
    public static final String TOPIC_EXCHANGE="topicExchange";
    public static final String ROUTING_KEY1="topic.key1";
    public static final String ROUTING_KEY2="topic.#";// # 是通配符,零个或者1个
    public static final String FANOUT_EXCHANGE="fanoutExchange";
    public static final String HEADERS_EXCHANGE="headersExchange";
    public static final String HEADERS_QUEUE="headersQueue";


    /**
     * Direct 模式 交换机Exchange,每一个都需要新的
     */
    @Bean
    public Queue miaoshaQueue(){
        return new Queue(MIAOSHA_QUEUE,true);
    }

    @Bean
    public Queue queue(){
        return new Queue(QUEUE_NAME,true);
    }


    /**
     * Topic 模式 交换机Exchange
     */
    @Bean
    public Queue topic(){
        return new Queue(QUEUE_QUEUE1,true);
    }
    /**
     * Topic 模式 交换机Exchange
     */
    @Bean
    public Queue topic2(){
        return new Queue(QUEUE_QUEUE2,true);
    }
    @Bean
    public TopicExchange topicExchange(){
        return new TopicExchange(TOPIC_EXCHANGE);
    }
    @Bean
    public Binding topicBind(){
        return BindingBuilder.bind(topic()).to(topicExchange()).with(ROUTING_KEY1);
    }
    @Bean
    public Binding topicBind2(){
        return BindingBuilder.bind(topic2()).to(topicExchange()).with(ROUTING_KEY2);
    }

    /**
     * 广播模式Fanout
     */
    @Bean
    public FanoutExchange fanoutExchange(){
        return new FanoutExchange(FANOUT_EXCHANGE);
    }
    @Bean
    public Binding fanoutBinding(){
        return BindingBuilder.bind(topic()).to(fanoutExchange());
    }
    @Bean
    public Binding fanoutBinding2(){
        return BindingBuilder.bind(topic2()).to(fanoutExchange());
    }



    /**
     * Header模式
     */
    @Bean
    public HeadersExchange headersExchange(){
        return new HeadersExchange(HEADERS_EXCHANGE);
    }
    @Bean
    public Queue headerQueue(){
        return new Queue(HEADERS_QUEUE);
    }
    @Bean
    public Binding headersBinding(){
        Map<String, Object> map = new HashMap<>();
        map.put("head1","value1");
        map.put("head2","value2");
        return BindingBuilder.bind(headerQueue()).to(headersExchange()).whereAll(map).match();
    }



}
