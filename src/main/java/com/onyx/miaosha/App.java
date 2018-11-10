package com.onyx.miaosha;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication

public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class,args);
    }

    /**
     * @EnableAutoConfiguration
     * 这样@EnableAutoConfiguration可以从逐层的往下搜索各个加注解的类，例如，你正在编写一个JPA程序
     * （如果你的pom里进行了配置的话），spring会自动去搜索加了@Entity注解的类，并进行调用
     *
     * 使用@SpringbootApplication注解  可以解决根类或者配置类（我自己的说法，就是main所在类）
     * 头上注解过多的问题，一个@SpringbootApplication相当于@Configuration
     * ,@EnableAutoConfiguration和 @ComponentScan 并具有他们的默认属性值
     */

}
