package com.collect.backend.utils.date_string_convert;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * date类型和string类型的相互转换类
 */
public class DateStringMutualConvertUtils {
    public static Date StrToDate(String str,String formatStr) {
        SimpleDateFormat format = new SimpleDateFormat(formatStr);
        Date date = null;
        try {
            date = format.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
}
