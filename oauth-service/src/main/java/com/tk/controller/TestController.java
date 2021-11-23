package com.tk.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: TK
 * @date: 2021/11/23 10:45
 */
@RestController
public class TestController {


  @GetMapping(value = "/test")
  public String test() {
    return "hello oauth";
  }
}
