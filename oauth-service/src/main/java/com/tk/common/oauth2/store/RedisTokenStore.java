package com.tk.common.oauth2.store;

import java.util.Collection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.DefaultAuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.TokenStore;

/**
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


  @Override
  public OAuth2Authentication readAuthentication(OAuth2AccessToken token) {
    return null;
  }

  @Override
  public OAuth2Authentication readAuthentication(String token) {
    return null;
  }

  @Override
  public void storeAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {

  }

  @Override
  public OAuth2AccessToken readAccessToken(String tokenValue) {
    return null;
  }

  @Override
  public void removeAccessToken(OAuth2AccessToken token) {

  }

  @Override
  public void storeRefreshToken(OAuth2RefreshToken refreshToken, OAuth2Authentication authentication) {

  }

  @Override
  public OAuth2RefreshToken readRefreshToken(String tokenValue) {
    return null;
  }

  @Override
  public OAuth2Authentication readAuthenticationForRefreshToken(OAuth2RefreshToken token) {
    return null;
  }

  @Override
  public void removeRefreshToken(OAuth2RefreshToken token) {

  }

  @Override
  public void removeAccessTokenUsingRefreshToken(OAuth2RefreshToken refreshToken) {

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