package com.tk.server;

import com.tk.server.service.RedisAuthorizationCodeService;
import com.tk.server.service.RedisClientDetailsService;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;

/**
 * 认证服务配置
 *
 * @author: TK
 * @date: 2021/11/16 16:31
 */
@EnableAuthorizationServer
@Configuration
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

  private static final Logger log = LoggerFactory.getLogger(AuthorizationServerConfig.class);

  @Resource
  private RedisClientDetailsService redisClientDetailsService;
  @Resource
  private RedisAuthorizationCodeService redisAuthorizationCodeService;


  /**
   * 配置令牌端点的安全约束
   *
   * @param security
   * @throws Exception
   */
  @Override
  public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
    security
        // 对 /oauth/token_key放权
        .tokenKeyAccess("permitAll()")
        // 对 /oauth/check_token 开启校验
        .checkTokenAccess("isAuthenticated()");
  }

  /**
   * 配置客户端详情服务,初始化客户端详情信息。
   *
   * @param clients
   * @throws Exception
   */
  @Override
  public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
    clients.withClientDetails(redisClientDetailsService);
    //将全部的客户端详情加载到redis中
    redisClientDetailsService.getAllClientDetailsToCache();
  }

  /**
   * 配置   授权(authorization);
   * 令牌(token)的访问端点;
   * 令牌服务(token services);
   * token的存储方式(tokenStore)；
   *
   * @param endpoints
   * @throws Exception
   */
  @Override
  public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
    endpoints.authorizationCodeServices(redisAuthorizationCodeService);
  }
}
