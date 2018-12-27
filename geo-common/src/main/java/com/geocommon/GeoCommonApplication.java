package com.geocommon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient  //若不需要注册中心，该注解需要注释掉或删除
@SpringBootApplication
@ServletComponentScan
public class GeoCommonApplication {

    public static void main(String[] args) {
        SpringApplication.run(GeoCommonApplication.class, args);
    }
}
