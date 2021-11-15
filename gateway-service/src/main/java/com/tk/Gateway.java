package com.tk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * @author: TK
 * @date: 2021/11/15 15:35
 */
@EnableEurekaClient
@SpringBootApplication
public class Gateway {

  public static void main(String[] args) {
    SpringApplication.run(Gateway.class, args);
  }
}
