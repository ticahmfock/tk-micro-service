package com.tk.common.oauth2.service;

import com.tk.common.oauth2.model.SecurityUser;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * 扩展多种登录验证基类
 *
 * @author: TK
 * @date: 2021/11/22 15:22
 */
public abstract class BaseUserDetailsService implements UserDetailsService {

  /**
   * 获取用户信息
   *
   * @param s
   * @return
   * @throws UsernameNotFoundException
   */
  @Override
  public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
    return getSecurityUser(s);
  }

  /**
   * 获取用户信息
   *
   * @param s
   * @return
   */
  protected abstract SecurityUser getSecurityUser(String s);

}
