package com.kicc.umeng.listener;

import androidx.annotation.Nullable;

import com.kicc.umeng.enums.PlatformEnum;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

/**
 *<p>
 * 友盟第三方分享回调监听
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @since: 2022/10/15
 */
public final class UmengShareListener implements UMShareListener {

    private final PlatformEnum mPlatformEnum;

    private OnShareListener mListener;

    public UmengShareListener(SHARE_MEDIA platform, @Nullable OnShareListener listener) {
        mListener = listener;
        switch (platform) {
            case QQ:
                mPlatformEnum = PlatformEnum.QQ;
                break;
            case QZONE:
                mPlatformEnum = PlatformEnum.QZONE;
                break;
            case WEIXIN:
                mPlatformEnum = PlatformEnum.WECHAT;
                break;
            case WEIXIN_CIRCLE:
                mPlatformEnum = PlatformEnum.CIRCLE;
                break;
            default:
                throw new IllegalStateException("不支持此平台,请扩展此平台!");
        }
    }

    /**
     * 分享开始的回调
     * @param platform 平台名称
     */
    @Override
    public void onStart(SHARE_MEDIA platform) {
        if (mListener == null) return;
        mListener.onStart(mPlatformEnum);
    }

    /**
     * 分享成功的回调
     * @param platform 平台名称
     */
    @Override
    public void onResult(SHARE_MEDIA platform) {
        if (mListener == null) return;
        mListener.onSucceed(mPlatformEnum);
        mListener = null;
    }

    /**
     * 分享失败的回调
     * @param platform 平台名称
     * @param t 错误原因
     */
    @Override
    public void onError(SHARE_MEDIA platform, Throwable t) {
        t.printStackTrace();
        if (mListener == null) return;
        mListener.onError(mPlatformEnum, t);
        mListener = null;
    }

    /**
     * 分享取消的回调
     * @param platform 平台名称
     */
    @Override
    public void onCancel(SHARE_MEDIA platform) {
        if (mListener == null) return;
        mListener.onCancel(mPlatformEnum);
        mListener = null;
    }

    public interface OnShareListener {

        /**
         * 分享开始
         * @param platformEnum 平台对象
         */
        default void onStart(PlatformEnum platformEnum) {}

        /**
         * 分享成功的回调
         * @param platformEnum 平台对象
         */
        void onSucceed(PlatformEnum platformEnum);

        /**
         * 分享失败的回调
         * @param platformEnum 平台对象
         * @param t 错误原因
         */
        default void onError(PlatformEnum platformEnum, Throwable t) {}

        /**
         * 分享取消的回调
         * @param platformEnum 平台对象
         */
        default void onCancel(PlatformEnum platformEnum) {}
    }
}