package com.collect.backend.utils.file;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 文件相同重命名方法
 */
public class FileNameSameResetName {
    public static String resetNewNoRepeated(String fileName){
        // 获取当前时间
        Date currentDate = new Date();
        // 创建日期格式化对象
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        // 将日期转换成字符串
        String dateString = dateFormat.format(currentDate);
        return fileName + "_" + dateString;
    }
}
