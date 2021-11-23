package com.tk.server;

import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author: TK
 * @date: 2021/11/16 17:16
 */
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.inMemoryAuthentication()
        .withUser("admin").password("$2a$10$ZrUj.KY55IiAqah21p2mAefxum5NwU7cPou.IbbJ02gRcNEzCoEum").roles("admin");
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.antMatcher("/oauth/**")
        .authorizeRequests()
        .antMatchers("/oauth/**").permitAll()
        .and().csrf().disable();
  }

  @Bean
  @Override
  protected AuthenticationManager authenticationManager() throws Exception {
    return super.authenticationManager();
  }

  @Bean
  @Override
  protected UserDetailsService userDetailsService() {
    return super.userDetailsService();
  }
}
