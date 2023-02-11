package com.dolphin.umeng.listener;

import androidx.annotation.Nullable;

import com.dolphin.umeng.enums.PlatformEnum;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.Map;

/**
 *<p>
 * 友盟第三方登录监听
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @since: 2022/10/15
 */
public final class UmengLoginListener implements UMAuthListener {

    private final PlatformEnum mPlatformEnum;

    private OnLoginListener mListener;

    public UmengLoginListener(SHARE_MEDIA platform, @Nullable OnLoginListener listener) {
        mListener = listener;
        switch (platform) {
            case QQ:
                mPlatformEnum = PlatformEnum.QQ;
                break;
            case WEIXIN:
                mPlatformEnum = PlatformEnum.WECHAT;
                break;
            default:
                throw new IllegalStateException("不支持此平台,请扩展此平台!");
        }
    }

    /** 登录成功后返回对象 */
    public final class LoginData {

        /** 用户 id */
        private final String mId;
        /** 昵称 */
        private final String mName;
        /** 性别 */
        private final String mSex;
        /** 头像 */
        private final String mAvatar;
        /** Token */
        private final String mToken;

        LoginData(Map<String, String> data) {
            // 第三方登录获取用户资料：https://developer.umeng.com/docs/66632/detail/66639#h3-qq-32
            mId = data.get("uid");
            mName =  data.get("name");
            mSex = data.get("gender");
            mAvatar = data.get("iconurl");
            mToken = data.get("accessToken");
        }

        public String getName() {
            return mName;
        }

        public String getSex() {
            return mSex;
        }

        public String getAvatar() {
            return mAvatar;
        }

        public String getId() {
            return mId;
        }

        public String getToken() {
            return mToken;
        }

        /** 判断当前的性别是否为男 */
        public boolean isMan() {
            return "男".equals(mSex);
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
     * 授权成功的回调
     * @param platform 平台名称
     * @param action 行为序号,开发者用不上
     * @param data 用户资料返回
     */
    @Override
    public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
        if (mListener == null) return;
        mListener.onSucceed(mPlatformEnum, new LoginData(data));
        mListener = null;
    }

    /**
     * 授权失败的回调
     * @param platform 平台名称
     * @param action 行为序号,开发者用不上
     * @param t 错误原因
     */
    @Override
    public void onError(SHARE_MEDIA platform, int action, Throwable t) {
        t.printStackTrace();
        if (mListener == null) return;
        mListener.onError(mPlatformEnum, t);
        mListener = null;
    }

    /**
     * 授权取消的回调
     * @param platform 平台名称
     * @param action 行为序号,开发者用不上
     */
    @Override
    public void onCancel(SHARE_MEDIA platform, int action) {
        if (mListener == null) return;
        mListener.onCancel(mPlatformEnum);
        mListener = null;
    }

    public interface OnLoginListener {

        /**
         * 授权开始
         * @param platformEnum 平台对象
         */
        default void onStart(PlatformEnum platformEnum) {}

        /**
         * 授权成功的回调
         * @param platformEnum 平台对象
         * @param data 用户资料返回
         */
        void onSucceed(PlatformEnum platformEnum, UmengLoginListener.LoginData data);

        /**
         * 授权失败的回调
         * @param platformEnum 平台对象
         * @param t 错误原因
         */
        default void onError(PlatformEnum platformEnum, Throwable t) {}

        /**
         * 授权取消的回调
         * @param platformEnum 平台对象
         */
        default void onCancel(PlatformEnum platformEnum) {}
    }
}