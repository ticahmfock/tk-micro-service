package com.tk.exception;

/**
 * 自定义系统异常
 *
 * @author: TK
 * @date: 2021/11/22 15:05
 */
public class MicroSystemException extends Exception{

  public MicroSystemException(String message) {
    super(message);
  }
}
