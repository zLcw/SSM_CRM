package com.crm.commons.util;

import java.util.UUID;

public class UUIDUtils {
    /**
     * 获取UUID的字符串
     * @return
     */
    public static String getUUID(){
        return UUID.randomUUID().toString().replaceAll("-","");
    }
}
