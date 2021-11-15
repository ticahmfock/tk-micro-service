package com.tk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * @author: TK
 * @date: 2021/11/15 15:07
 */
@EnableEurekaServer
@SpringBootApplication
public class Eureka {

  public static void main(String[] args) {
    SpringApplication.run(Eureka.class, args);
  }
}
