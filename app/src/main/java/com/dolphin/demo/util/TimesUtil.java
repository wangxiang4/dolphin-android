package com.dolphin.demo.util;

import java.text.SimpleDateFormat;

/**
 *<p>
 *  获取各类时间工具类
 *</p>
 *
 * @Author: liuSiXiang
 * @since: 2022/10/24
 */
public class TimesUtil {

    /** 获取当前时间状态 早晨/中午/夜晚 */
    public static String getTimeState(){
        String type = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH");
        java.sql.Date curDate = new java.sql.Date(System.currentTimeMillis());
        Integer hour = Integer.valueOf((dateFormat.format(curDate)));
        if ( 0 <= hour && hour < 5 ){
            type = "凌晨好！";
        }else if ( 5 <= hour && hour < 6) {
            type = "早晨好！";
        }else if ( 7 <= hour && hour < 11) {
            type = "上午好！";
        }else if ( 11 <= hour && hour < 13){
            type = "中午好！";
        }else if ( 13 <= hour && hour < 16) {
            type = "下午好！";
        }else if ( 16 <= hour && hour < 19) {
            type = "傍晚好！";
        }else if ( 19 <= hour && hour < 24) {
            type = "晚上好！";
        }else {
            type = "你好！";
        }
        return type;
    }
}
