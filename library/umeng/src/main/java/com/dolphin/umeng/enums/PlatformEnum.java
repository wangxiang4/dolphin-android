package com.dolphin.umeng.enums;

import com.umeng.socialize.bean.SHARE_MEDIA;

/**
 *<p>
 * 友盟分享所支持第三方平台
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @since: 2022/10/15
 */
public enum PlatformEnum {

    /** 微信 */
    WECHAT(SHARE_MEDIA.WEIXIN, "com.tencent.mm"),
    /** 微信朋友圈 */
    CIRCLE(SHARE_MEDIA.WEIXIN_CIRCLE, "com.tencent.mm"),

    /** QQ */
    QQ(SHARE_MEDIA.QQ, "com.tencent.mobileqq"),
    /** QQ 空间 */
    QZONE(SHARE_MEDIA.QZONE, "com.tencent.mobileqq");

    /** 第三方平台 */
    private final SHARE_MEDIA mThirdParty;
    /** 第三方包名 */
    private final String mPackageName;

    PlatformEnum(SHARE_MEDIA thirdParty, String packageName) {
        mThirdParty = thirdParty;
        mPackageName = packageName;
    }

    public SHARE_MEDIA getThirdParty() {
        return mThirdParty;
    }

    public String getPackageName() {
        return mPackageName;
    }
}