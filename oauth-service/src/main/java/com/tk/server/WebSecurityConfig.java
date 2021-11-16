package com.tk.server;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author: TK
 * @date: 2021/11/16 17:16
 */
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  @Bean
  PasswordEncoder passwordEncoder(){
    return new BCryptPasswordEncoder();
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.inMemoryAuthentication()
        .withUser("sang")
        .password(new BCryptPasswordEncoder().encode("123"))
        .roles("admin")
        .and()
        .withUser("javaboy")
        .password(new BCryptPasswordEncoder().encode("123"))
        .roles("user");
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.csrf().disable().formLogin();
  }
}
