package com.crm.commons.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 对date类型数据进行处理的工具类
 */
public class DateUtil {
    /**
     * 对Date对象进行格式化的格式
     */
    private static final String DATA_TYPE = "yyyy-MM-dd HH:mm:ss";
    private static final String DATA_TIME_TYPE = "HH:mm:ss";
    private static final String DATA_DAY_TYPE = "yyyy-MM-dd";

    /**
     * 对指定的Date对象进行格式化
     * @param date 需要进行格式化的Date对象
     * @return
     */
    public static String formateDateTime(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat(DATA_TYPE);
        return sdf.format(date);
    }

    public static String formateTime(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat(DATA_TIME_TYPE);
        return sdf.format(date);
    }
    public static String formateDate(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat(DATA_DAY_TYPE);
        return sdf.format(date);
    }
}
