package com.tk.server.service;


import com.alibaba.fastjson.JSONObject;
import java.util.List;
import javax.annotation.Resource;
import javax.sql.DataSource;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.common.exceptions.InvalidClientException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.NoSuchClientException;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.stereotype.Component;

/**
 * 采用redis来存储客户端信息
 *
 * @author: TK
 * @date: 2021/11/17 15:45
 */
public class RedisClientDetailsService extends JdbcClientDetailsService {

  private static final Logger log = LoggerFactory.getLogger(RedisClientDetailsService.class);

  /**
   * 存储客户端详情的key
   */
  private static final String CLIENT_KEY = "oauth_client_details";

  /**
   * 注入redisTemplate 采用hash结构存储客户端详情
   */
  private RedisTemplate<String, Object> redisTemplate;

  public RedisTemplate<String, Object> getRedisTemplate() {
    return redisTemplate;
  }

  public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  public RedisClientDetailsService(DataSource dataSource) {
    super(dataSource);
  }

  @Override
  public ClientDetails loadClientByClientId(String clientId) throws InvalidClientException {
    ClientDetails clientDetails = null;
    String value = (String) redisTemplate.boundHashOps(CLIENT_KEY).get(clientId);
    if (StringUtils.isEmpty(value)) {
      clientDetails = getClientDetailsAndCache(clientId);
    } else {
      clientDetails = JSONObject.parseObject(value, BaseClientDetails.class);
    }
    return clientDetails;
  }

  /**
   * 更新客户端详情
   *
   * @param clientDetails
   * @throws NoSuchClientException
   */
  @Override
  public void updateClientDetails(ClientDetails clientDetails) throws NoSuchClientException {
    super.updateClientDetails(clientDetails);
    getClientDetailsAndCache(clientDetails.getClientId());
  }

  /**
   * 更新客户端秘钥
   *
   * @param clientId
   * @param secret
   * @throws NoSuchClientException
   */
  @Override
  public void updateClientSecret(String clientId, String secret) throws NoSuchClientException {
    super.updateClientSecret(clientId, secret);
    getClientDetailsAndCache(clientId);
  }

  /**
   * 删除客户端详情
   *
   * @param clientId
   * @throws NoSuchClientException
   */
  @Override
  public void removeClientDetails(String clientId) throws NoSuchClientException {
    super.removeClientDetails(clientId);
    redisTemplate.boundHashOps(CLIENT_KEY).delete(clientId);
  }

  /**
   * 通过clientId获取客户端详情
   *
   * @param clientId
   * @return
   */
  private ClientDetails getClientDetailsAndCache(String clientId) {
    try {
      ClientDetails clientDetails = super.loadClientByClientId(clientId);
      if (clientDetails != null) {
        redisTemplate.boundHashOps(CLIENT_KEY).put(clientId, JSONObject.toJSONString(clientDetails));
        log.info("获取clientId:{}的客户端详情", clientId);
      }
      return clientDetails;
    } catch (NoSuchClientException e) {
      e.printStackTrace();
      log.error("无法获取clientId:{}的客户端详情", clientId);
    }
    return null;
  }

  /**
   * 将所有客户端详情加入到redis缓存中
   */
  public void getAllClientDetailsToCache() {
    if (!redisTemplate.hasKey(CLIENT_KEY)) {
      List<ClientDetails> list = super.listClientDetails();
      if (CollectionUtils.isEmpty(list)) {
        log.error("无法获取全部的客户端详情");
        return;
      }
      list.stream().forEach(clientDetails -> {
        redisTemplate.boundHashOps(CLIENT_KEY).put(clientDetails.getClientId(), JSONObject.toJSONString(clientDetails));
      });
      log.info("客户端详情已加载完毕,共：{}条", list.size());
    }
  }
}
