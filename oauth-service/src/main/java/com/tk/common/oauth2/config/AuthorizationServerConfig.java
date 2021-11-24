package com.tk.common.oauth2.config;

import com.tk.common.oauth2.service.client.RedisClientDetailsService;
import com.tk.common.oauth2.store.RedisTokenStore;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;


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

  /**
   * 客户端详情
   */
  @Resource
  private RedisClientDetailsService redisClientDetailsService;

  /**
   * 支持密码模式授权
   */
  @Resource
  private AuthenticationManager authenticationManager;
  /**
   * Redis存储token
   */
  @Resource
  private RedisTokenStore redisTokenStore;

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
    redisClientDetailsService.getAllClientDetailsToRedis();
  }

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
        .checkTokenAccess("isAuthenticated()")
        //允许使用client和secret作为登录
        .allowFormAuthenticationForClients();
  }

  /**
   * 配置
   * 1、认证管理器(AuthenticationManager);
   * 2、授权服务器(AuthorizationServerTokenServices);
   * 3、token存储方式(tokenStore)
   * 令牌(token)的访问端点;
   * 令牌服务(token services);
   *
   * @param endpoints
   * @throws Exception
   */
  @Override
  public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
    endpoints
        //认证管理器
        .authenticationManager(authenticationManager)
        .tokenServices(defaultTokenServices())
        .tokenStore(redisTokenStore);
  }


  @Primary
  @Bean
  public DefaultTokenServices defaultTokenServices() {
    DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
    defaultTokenServices.setTokenStore(redisTokenStore);
    defaultTokenServices.setAccessTokenValiditySeconds(60 * 60 * 2);
    defaultTokenServices.setRefreshTokenValiditySeconds(60 * 60 * 7);
    //支持refresh_token
    defaultTokenServices.setSupportRefreshToken(true);
    return defaultTokenServices;
  }
}

