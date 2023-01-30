package com.dolphin.demo.constant;

import com.dolphin.core.BuildConfig;

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
    String UMENG_PUSH_USER_ALIAS_TYPE = "DOLPHIN_UID";

    /** oss文件预览地址 */
    String OSS_FILE_URL = BuildConfig.HOST_URL+"system_proxy/system/file/getFile/%s/%s";

    /** 路线规划经纬度 */
    String ROUTE_PLAN_LAT_POINT = "ROUTE_PLAN_LAT_POINT";

}
