package com.eamon.rtbau;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@MapperScan("com.eamon.rtbau.**.mapper")
@EnableScheduling
@EnableSwagger2
@EnableDubbo
public class RtbauApplication {

    public static void main(String[] args) {
        SpringApplication.run(RtbauApplication.class, args);
    }

}
