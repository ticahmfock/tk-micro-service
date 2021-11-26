package com.tk.common.oauth2.store;

import java.util.Collection;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.DefaultAuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.TokenStore;

/**
 * 自定义store存储方式
 *
 * @author: TK
 * @date: 2021/11/24 9:21
 */
public class RedisTokenStore implements TokenStore {

  private static final String ACCESS = "access:";
  private static final String AUTH_TO_ACCESS = "auth_to_access:";
  private static final String AUTH = "auth:";
  private static final String REFRESH_AUTH = "refresh_auth:";
  private static final String ACCESS_TO_REFRESH = "access_to_refresh:";
  private static final String REFRESH = "refresh:";
  private static final String REFRESH_TO_ACCESS = "refresh_to_access:";
  private static final String CLIENT_ID_TO_ACCESS = "client_id_to_access:";
  private static final String UNAME_TO_ACCESS = "uname_to_access:";

  private RedisTemplate<String, Object> redisTemplate;

  private AuthenticationKeyGenerator authenticationKeyGenerator = new DefaultAuthenticationKeyGenerator();

  public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
    this.redisTemplate = redisTemplate;
  }


  /**
   * 读取认证信息
   *
   * @param token
   * @return
   */
  @Override
  public OAuth2Authentication readAuthentication(OAuth2AccessToken token) {
    return readAuthentication(token.getValue());
  }

  /**
   * 读取认证信息
   *
   * @param token
   * @return
   */
  @Override
  public OAuth2Authentication readAuthentication(String token) {
    return (OAuth2Authentication) this.redisTemplate.opsForValue().get(AUTH + token);
  }

  /**
   * 存储认证信息
   *
   * @param token
   * @param authentication
   */
  @Override
  public void storeAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {

  }

  @Override
  public OAuth2AccessToken readAccessToken(String tokenValue) {
    return null;
  }

  /**
   * 删除token
   *
   * @param token
   */
  @Override
  public void removeAccessToken(OAuth2AccessToken token) {
    removeAccessToken(token.getValue());
  }

  /**
   * 删除token
   *
   * @param token
   */
  public void removeAccessToken(String token) {

  }

  @Override
  public void storeRefreshToken(OAuth2RefreshToken refreshToken, OAuth2Authentication authentication) {

  }

  /**
   * 读取refreshToken
   *
   * @param tokenValue
   * @return
   */
  @Override
  public OAuth2RefreshToken readRefreshToken(String tokenValue) {
    return (OAuth2RefreshToken) this.redisTemplate.opsForValue().get(REFRESH + tokenValue);
  }

  /**
   * 通过refreshToken读取认证信息
   *
   * @param token
   * @return
   */
  @Override
  public OAuth2Authentication readAuthenticationForRefreshToken(OAuth2RefreshToken token) {
    return readAuthenticationForRefreshToken(token.getValue());
  }

  /**
   * 通过refreshToken读取认证信息
   *
   * @param token
   * @return
   */
  public OAuth2Authentication readAuthenticationForRefreshToken(String token) {
    return (OAuth2Authentication) this.redisTemplate.opsForValue().get(REFRESH_AUTH + token);
  }

  /**
   * 删除refreshToken
   *
   * @param token
   */
  @Override
  public void removeRefreshToken(OAuth2RefreshToken token) {
    removeRefreshToken(token.getValue());
  }

  /**
   * 删除 refreshToken
   *
   * @param token
   */
  public void removeRefreshToken(String token) {
    this.redisTemplate.delete(REFRESH + token);
    this.redisTemplate.delete(REFRESH_AUTH + token);
    this.redisTemplate.delete(REFRESH_TO_ACCESS + token);
    this.redisTemplate.delete(ACCESS_TO_REFRESH + token);
  }

  /**
   * 使用RefreshToken删除AccessToken
   *
   * @param refreshToken
   */
  @Override
  public void removeAccessTokenUsingRefreshToken(OAuth2RefreshToken refreshToken) {
    removeAccessTokenUsingRefreshToken(refreshToken.getValue());
  }

  /**
   * 使用RefreshToken删除AccessToken
   *
   * @param refreshToken
   */
  public void removeAccessTokenUsingRefreshToken(String refreshToken) {
    String token = (String) this.redisTemplate.opsForValue().get(REFRESH_TO_ACCESS + refreshToken);
    if (StringUtils.isNotEmpty(token)) {

    }
  }

  @Override
  public OAuth2AccessToken getAccessToken(OAuth2Authentication authentication) {
    return null;
  }

  @Override
  public Collection<OAuth2AccessToken> findTokensByClientIdAndUserName(String clientId, String userName) {
    return null;
  }

  @Override
  public Collection<OAuth2AccessToken> findTokensByClientId(String clientId) {
    return null;
  }
}
