package com.soccer.management;

import javax.annotation.PostConstruct;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.soccer.management.util.PlayerUtil;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author enes.boyaci
 */
@SpringBootApplication
@EnableSwagger2
public class SoccerManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(SoccerManagementApplication.class, args);
    }

    @PostConstruct
    public void initializePlayerValues() {
        PlayerUtil.initializePlayerValues();
    }

}
