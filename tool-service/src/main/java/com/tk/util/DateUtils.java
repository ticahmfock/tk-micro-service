package com.tk.util;

import com.tk.common.StringCommons;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * 时间工具类
 *
 * @author: TK
 * @date: 2021/11/16 10:57
 */
public class DateUtils {

  /**
   * 获取当前时间
   *
   * @param pattern
   * @return
   */
  public static String getCurrentDateTime(String pattern) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
    return LocalDateTime.now().format(formatter);
  }

  /**
   * 获取开始时间和结束时间
   *
   * @param date
   * @param suffix
   * @return
   */
  public static String getDateTime(Object date, String suffix) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(StringCommons.DATE);
    if (date == null) {
      return LocalDateTime.now().format(formatter)
          .concat(StringCommons.SPACE).concat(suffix);
    }
    if (date instanceof String) {
      return ((String) date).concat(StringCommons.SPACE)
          .concat(suffix);
    }
    if (date instanceof Date) {
      Instant instant = ((Date) date).toInstant();
      ZoneId zoneId = ZoneId.systemDefault();
      LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zoneId);
      return localDateTime.format(formatter).concat(StringCommons.SPACE)
          .concat(suffix);
    }
    return "";
  }

  /**
   * 获取当前时间前几天或后几天时间
   *
   * @param days
   * @return
   */
  public static String getCurrentDateTimeMinusDays(int days) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(StringCommons.DATE_TIME_SECONDS);
    LocalDateTime localDateTime = LocalDateTime.now().minusDays(days);
    return localDateTime.format(formatter);
  }

  /**
   * 获取当前时间后几天时间
   *
   * @param days
   * @return
   */
  public static String getCurrentDateTimePlusDays(int days) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(StringCommons.DATE_TIME_SECONDS);
    LocalDateTime localDateTime = LocalDateTime.now().plusDays(days);
    return localDateTime.format(formatter);
  }

  /**
   * 获取当前时间前几月时间
   *
   * @param months
   * @return
   */
  public static String getCurrentDateTimeMinusMonths(int months) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(StringCommons.DATE_TIME_SECONDS);
    LocalDateTime localDateTime = LocalDateTime.now().minusMonths(months);
    return localDateTime.format(formatter);
  }

  /**
   * 获取当前时间后几月时间
   *
   * @param months
   * @return
   */
  public static String getCurrentDateTimePlusMonths(int months) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(StringCommons.DATE_TIME_SECONDS);
    LocalDateTime localDateTime = LocalDateTime.now().plusMonths(months);
    return localDateTime.format(formatter);
  }

  /**
   * 获取当前前几天
   *
   * @param days
   * @return
   */
  public static String getCurrentDateMinusDays(int days, String suffix) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(StringCommons.DATE);
    LocalDate localDate = LocalDate.now().minusDays(days);
    return localDate.format(formatter).concat(StringCommons.SPACE).concat(suffix);
  }

  /**
   * 获取当前后几天
   *
   * @param days
   * @return
   */
  public static String getCurrentDateTimePlusDays(int days, String suffix) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(StringCommons.DATE);
    LocalDate localDate = LocalDate.now().plusDays(days);
    return localDate.format(formatter).concat(StringCommons.SPACE).concat(suffix);
  }

  public static void main(String[] args) {
   /* System.out.println(getCurrentDateTime(StringCommons.DATE_TIME_SECONDS));
    System.out.println(getCurrentDateTime(StringCommons.DATE_TIME_MINUTES));
    System.out.println(getDateTime("2021-11-01", StringCommons.ZERO_TIME));
    System.out.println(getDateTime(new Date(), StringCommons.ZERO_TIME));
    System.out.println(getDateTime(null,StringCommons.LAST_TIME));
    System.out.println(getDateTime(null,""));
    System.out.println(getCurrentDateTimeToBeforeDays(1,StringCommons.LAST_TIME));
    System.out.println(getCurrentDateTimeMinusDays(2));
    System.out.println(getCurrentDateTimePlusDays(15));
    System.out.println(getCurrentDateTimePlusMonths(5));*/
  }
}
