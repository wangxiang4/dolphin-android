package com.kicc.collect.constant;

import com.kicc.core.BuildConfig;

/**
 *<p>
 * 通用常量
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @Date: 2022/6/24
 */
public interface CommonConstant {

    /** 友盟消息推送别名类型 */
    String UMENG_PUSH_USER_ALIAS_TYPE = "KICC_UID";

    String OSS_FILE_URL = BuildConfig.HOST_URL+"system_proxy/system/file/getFile/%s/%s";

    /** 行程规划传输点 */
    String ROUTE_PLAN_LAT_POINT = "ROUTE_PLAN_LAT_POINT";

    /** 用户当前所在城市 */
    String USER_NOW_CITY = "GPS_USER_NOW_CITY";

    /** 预设点规划传输点 */
    String PRESET_PLAN_LAT_POINT = "PRESET_PLAN_LAT_POINT";
}
