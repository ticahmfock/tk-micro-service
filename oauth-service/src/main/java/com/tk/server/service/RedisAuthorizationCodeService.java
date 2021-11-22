package com.tk.server.service;

import com.tk.exception.MicroSystemException;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.code.RandomValueAuthorizationCodeServices;

/**
 * 采用redis来存储授权码
 *
 * @author: TK
 * @date: 2021/11/22 14:23
 */
public class RedisAuthorizationCodeService extends RandomValueAuthorizationCodeServices {

  private static final Logger log = LoggerFactory.getLogger(RedisAuthorizationCodeService.class);

  /**
   * 前缀
   */
  private static final String PREFIX = "oauth_code_";

  private RedisTemplate<String, Object> redisTemplate;

  public RedisTemplate<String, Object> getRedisTemplate() {
    return redisTemplate;
  }

  public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  /**
   * 替换JdbcAuthorizationCodeServices的存储策略 将授权码存储到redis，并设置过期时间，10分钟<br>
   *
   * @param code
   * @param authentication
   */
  @Override
  protected void store(String code, OAuth2Authentication authentication) {
    log.info("=====存储授权码到Redis中。code:{},authentication:{}=====", code, authentication);
    redisTemplate.opsForValue().set(getRedisKeyForCode(code), authentication, 10, TimeUnit.MINUTES);
  }

  /**
   * 去除授权码
   *
   * @param code
   * @return
   */
  @Override
  protected OAuth2Authentication remove(String code) {
    String key = getRedisKeyForCode(code);
    OAuth2Authentication authentication = (OAuth2Authentication) redisTemplate.opsForValue().get(key);
    if (authentication == null) {
      log.error("=====无效的授权码Code:{}", code);
    }
    redisTemplate.delete(key);
    return authentication;
  }

  /**
   * Redis存储key
   *
   * @param code
   * @return
   */
  private String getRedisKeyForCode(String code) {
    return PREFIX.concat(code);
  }
}
