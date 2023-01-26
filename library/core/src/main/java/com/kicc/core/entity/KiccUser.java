package com.kicc.core.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;
import java.util.Map;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 *<p>
 * 扩展安全框架用户信息
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @Date: 2022/6/28
 */
@Data
@Accessors
public class KiccUser implements Parcelable {

    /** 用户id */
    private String id;

    /** 用户名称 */
    private String username;

    /** 用户密码 */
    private String password;

    /** 部门ID */
    private String deptId;

    /** 用户手机号 */
    private String phone;

    /** 账户是否被冻结 */
    private Boolean enabled;

    /** 多租户ID */
    private String tenantId;

    /** 用户按钮权限 */
    private List<Map<String, String>> authorities;

    /** 帐户未锁定 */
    private Boolean accountNonLocked;

    /** 帐户未过期 */
    private Boolean accountNonExpired;

    /** 凭证未过期 */
    private Boolean credentialsNonExpired;

    public KiccUser() {
    }

    /** 内存反序列化对象 */
    protected KiccUser(Parcel in) {
        id = in.readString();
        username = in.readString();
        password = in.readString();
        deptId = in.readString();
        phone = in.readString();
        enabled = in.readBoolean();
        tenantId = in.readString();
        authorities = in.readArrayList(Map.class.getClassLoader());
        accountNonLocked = in.readBoolean();
        accountNonExpired = in.readBoolean();
        credentialsNonExpired = in.readBoolean();
    }

    /**
     * 内存序列化对象
     * @param dest 序列化对象 (包含序列化的一些操作)
     * @param flags 0或1 (1表示当前对象需要作为返回值返回,不能立即释放资源,几乎所有情况都为0)
     * @return void
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(username);
        dest.writeString(password);
        dest.writeString(deptId);
        dest.writeString(phone);
        dest.writeBoolean(enabled);
        dest.writeString(tenantId);
        dest.writeList(authorities);
        dest.writeBoolean(accountNonLocked);
        dest.writeBoolean(accountNonExpired);
        dest.writeBoolean(credentialsNonExpired);
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

    public static final Creator<KiccUser> CREATOR = new Creator() {
        @Override
        public KiccUser createFromParcel(Parcel in) {
            return new KiccUser(in);
        }

        @Override
        public KiccUser[] newArray(int size) {
            return new KiccUser[size];
        }
    };

}
