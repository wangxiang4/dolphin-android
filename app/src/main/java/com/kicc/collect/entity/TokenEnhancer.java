package com.kicc.collect.entity;

import android.os.Parcel;
import android.os.Parcelable;
import com.kicc.core.entity.KiccUser;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 *<p>
 * 令牌增强输出对象
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @Date: 2022/6/28
 */
@Data
@Accessors
public class TokenEnhancer implements Parcelable {

    /** 访问token */
    private String access_token;

    /** 客户端ID */
    private String clientId;

    /** 过期时间(毫秒为单位) */
    private Integer expires_in;

    /** 证书字段 */
    private String license;

    /** 刷新token */
    private String refresh_token;

    /** 授权权限范围 */
    private String scope;

    /** token类型 */
    private String token_type;

    /** 用户信息 */
    private KiccUser user_info;

    public TokenEnhancer() {
    }

    /** 内存反序列化对象 */
    public TokenEnhancer(Parcel in) {
        access_token = in.readString();
        clientId = in.readString();
        expires_in = in.readInt();
        license = in.readString();
        refresh_token = in.readString();
        scope = in.readString();
        token_type = in.readString();
        user_info = in.readTypedObject(KiccUser.CREATOR);
    }

    /**
     * 内存序列化对象
     * @param dest 序列化对象 (包含序列化的一些操作)
     * @param flags 0或1 (1表示当前对象需要作为返回值返回,不能立即释放资源,几乎所有情况都为0)
     * @return void
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(access_token);
        dest.writeString(clientId);
        dest.writeInt(expires_in);
        dest.writeString(license);
        dest.writeString(refresh_token);
        dest.writeString(scope);
        dest.writeString(token_type);
        dest.writeTypedObject(user_info, flags);
    }

    /**
     * unix系统文件描述符,一般情况下为0就行
     * 0:标准输入文件stdin
     * 1:标准输出文件stdout
     * 2:标准错误输出文件stderr
     */
    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TokenEnhancer> CREATOR = new Creator() {
        @Override
        public TokenEnhancer createFromParcel(Parcel in) {
            return new TokenEnhancer(in);
        }

        @Override
        public TokenEnhancer[] newArray(int size) {
            return new TokenEnhancer[size];
        }
    };

}
