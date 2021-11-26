package com.tk.common.oauth2.model;

import com.tk.common.StringCommons;
import java.util.Collection;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * 登录用户信息
 *
 * @author: TK
 * @date: 2021/11/26 14:12
 */
@Data
public class SecurityUser implements UserDetails {

  /**
   * 用户ID
   */
  private Long userId;

  /**
   * 用户名称
   */
  private String name;

  /**
   * 用户密码
   */
  private String password;

  /**
   * 用户状态
   */
  private String status;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return null;
  }

  /**
   * 获取密码
   *
   * @return
   */
  @Override
  public String getPassword() {
    return this.password;
  }

  /**
   * 获取用户名
   *
   * @return
   */
  @Override
  public String getUsername() {
    return this.name;
  }

  @Override
  public boolean isAccountNonExpired() {
    return false;
  }

  @Override
  public boolean isAccountNonLocked() {
    return false;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return false;
  }

  @Override
  public boolean isEnabled() {
    if (StringCommons.NORMAL.equals(this.status)) {
      return true;
    }
    return false;
  }
}
