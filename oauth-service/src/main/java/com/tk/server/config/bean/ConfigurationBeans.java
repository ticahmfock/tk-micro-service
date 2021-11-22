package com.tk.server.config.bean;

import com.tk.server.service.RedisAuthorizationCodeService;
import com.tk.server.service.RedisClientDetailsService;
import javax.annotation.Resource;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.provider.code.RandomValueAuthorizationCodeServices;

/**
 * oauth相关配置Bean
 *
 * @author: TK
 * @date: 2021/11/17 16:57
 */
@Configuration
public class ConfigurationBeans {

  private static final Logger log = LoggerFactory.getLogger(ConfigurationBeans.class);

  @Resource
  private DataSource dataSource;
  @Resource
  private RedisTemplate<String, Object> redisTemplate;

  /**
   * 声明 redis存储方式下的客户端详情
   *
   * @return
   */
  @Bean
  public RedisClientDetailsService redisClientDetailsService() {
    log.info("=====声明存储在Redis下的客户端详情=====");
    RedisClientDetailsService redisClientDetailsService = new RedisClientDetailsService(dataSource);
    redisClientDetailsService.setRedisTemplate(redisTemplate);
    return redisClientDetailsService;
  }

  /**
   * 声明 redis存储方式下的授权码详情
   *
   * @return
   */
  @Bean
  public RandomValueAuthorizationCodeServices redisAuthorizationCodeService() {
    log.info("=====声明授权和存储在Redis下的授权码服务=====");
    RedisAuthorizationCodeService redisAuthorizationCodeService = new RedisAuthorizationCodeService();
    redisAuthorizationCodeService.setRedisTemplate(redisTemplate);
    return redisAuthorizationCodeService;
  }
}
