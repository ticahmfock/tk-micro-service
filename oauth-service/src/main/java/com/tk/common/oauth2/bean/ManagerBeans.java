package com.tk.common.oauth2.bean;

import com.tk.common.oauth2.service.client.RedisClientDetailsService;
import com.tk.common.oauth2.service.RedisAuthorizationCodeService;
import com.tk.common.oauth2.store.RedisTokenStore;
import javax.annotation.Resource;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.provider.code.RandomValueAuthorizationCodeServices;

/**
 * 管理自定义类型的相关Bean
 *
 * @author: TK
 * @date: 2021/11/23 16:03
 */
@Configuration
public class ManagerBeans {

  private static final Logger log = LoggerFactory.getLogger(ManagerBeans.class);

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
    log.info("oauth中心===>声明存储在Redis下的客户端详情");
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
    log.info("oauth中心===>声明授权和存储在Redis下的授权码服务");
    RedisAuthorizationCodeService redisAuthorizationCodeService = new RedisAuthorizationCodeService();
    redisAuthorizationCodeService.setRedisTemplate(redisTemplate);
    return redisAuthorizationCodeService;
  }

  /**
   * 声明 redis存储token
   * @return
   */
  @Bean
  public RedisTokenStore redisTokenStore() {
    log.info("oauth==>声明Redis存储token");
    RedisTokenStore redisTokenStore = new RedisTokenStore();
    redisTokenStore.setRedisTemplate(redisTemplate);
    return redisTokenStore;
  }

}
