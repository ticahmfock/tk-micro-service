package com.tk.server.service.login;

import com.tk.model.LoginUser;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * 多种登录方式实现基础类
 *
 * @author: TK
 * @date: 2021/11/22 15:22
 */
public abstract class BaseUserDetailsService implements UserDetailsService {

  @Override
  public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
    LoginUser loginUser = getLoginUser(s);
    return null;
  }

  protected abstract LoginUser getLoginUser(String s);
}
