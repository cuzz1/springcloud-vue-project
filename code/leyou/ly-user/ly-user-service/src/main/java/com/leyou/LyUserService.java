package com.leyou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @Author: cuzz
 * @Date: 2018/11/16 16:36
 * @Description:
 */
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.leyou.user.mapper")
public class LyUserService {
    public static void main(String[] args) {
        SpringApplication.run(LyUserService.class, args);
    }
}
