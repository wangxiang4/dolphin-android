package com.kicc.core.constant;

/**
 *<p>
 * 应用程序相关常量
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @since: 2022/10/16
 */
public interface AppConstant {

    /** 启动指定活动类键 */
    String ACTIVITY_CLASS = "ACTIVITY_CLASS";

    /** 启动碎片容器活动指定碎片名称键 */
    String CONTAINER_FRAGMENT_NAME  = "CONTAINER_FRAGMENT_NAME";

    /** 进入活动所携带的数据 */
    String BUNDLE = "BUNDLE";

    /** 临时保存容器活动碎片类实例,完全退出程序会消失 */
    String TEMP_CONTAINER_FRAGMENT_INSTANCE_STATE = "TEMP_CONTAINER_FRAGMENT_INSTANCE_STATE";

    /** 刷新token */
    String ACCESS_TOKEN_NAME = "KICC_ACCESS_TOKEN";

    /** 访问token */
    String REFRESH_TOKEN_NAME = "KICC_REFRESH_TOKEN";

    /** 通用权限请求码(可以自定义请求权限对应各自的请求码) */
    Integer PERMISSION_REQUEST_CODE = 0;

    /** 定位任务间隔时间(默认5秒) */
    Long LOCATION_TASK_INTERVAL_TIME = 5 * 1000L;

    /** 后台定位前台服务通知ID */
    Integer LOCATION_FRONT_SERVICE_NOTIFICATION_ID = 1024;

    /** 默认线程池数量 */
    Integer defaultThreadPoolSize = 3;

    /** 底部标签栏默认选择下标 */
    String TAB_BAR_DEFAULT_INDEX = "TAB_BAR_DEFAULT_INDEX";

}
