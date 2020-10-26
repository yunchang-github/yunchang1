package com.weiziplus.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * springboot启动类
 *
 * @author wanglongwei
 * @data 2019/5/6 15:50
 */

@SpringBootApplication()
@EnableSwagger2
@EnableScheduling
public class SpringbootApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootApplication.class, args);


        System.out.println("---------------------springboot start---------------------");
        System.out.println("---------------------springboot start---------------------");
        System.out.println("---------------------springboot start---------------------");
        System.out.println("---------------------springboot start---------------------");
        System.out.println("---------------------springboot start---------------------");
        System.out.println("---------------------springboot start---------------------");
        System.out.println("---------------------springboot start---------------------");
        System.out.println("---------------------springboot start---------------------");
        System.out.println("---------------------springboot start---------------------");
        System.out.println("---------------------springboot start---------------------");
    }

}
