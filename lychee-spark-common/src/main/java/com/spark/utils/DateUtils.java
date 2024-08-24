package com.spark.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * @author LYCHEE
 * 日期工具类
 */
public class DateUtils {

    public static final String DATE_FORMAT = "yyyy-MM-dd";

    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 格式化日期为指定格式
     * @param date 日期
     * @param format 格式化字符串
     * @return 格式化后的日期字符串
     */
    public static String format(LocalDate date, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return date.format(formatter);
    }


}
