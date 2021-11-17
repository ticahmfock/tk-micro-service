package com.tk.server.config.bean;

import com.tk.server.service.RedisClientDetailsService;
import javax.annotation.Resource;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 相关配置Bean
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
    log.info("声明Bean【RedisClientDetailsService】");
    RedisClientDetailsService redisClientDetailsService = new RedisClientDetailsService(dataSource);
    redisClientDetailsService.setRedisTemplate(redisTemplate);
    return redisClientDetailsService;
  }
}
