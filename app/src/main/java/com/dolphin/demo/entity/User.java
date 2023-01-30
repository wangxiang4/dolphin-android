package com.dolphin.demo.entity;

import android.os.Parcel;

import com.dolphin.core.entity.CommonEntity;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 *<p>
 * 用户信息表
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @Date: 2022/2/24
 */
@Data
@Accessors
public class User extends CommonEntity {

    /** 用户id */
    private String id;

    /** 用户名 */
    private String userName;

    /** 昵称 */
    private String nickName;

    /** 用户类型 */
    private String userType;

    /** 头像 */
    private String avatar;

    /** 所属部门ID */
    private String deptId;

    /** 所属部门名称 */
    private String deptName;

    /** 邮箱 */
    private String email;

    /** 菜单按钮权限 */
    private String[] permissions;

    /** 角色ID权限 */
    private String[] roleIds;

    /** 手机号 */
    private String phone;

    /** 用户密码 */
    private String password;

    /** 用户性别 */
    private String sex;

    /** 最后登陆IP */
    private String loginIp;

    /** 最后登陆时间 */
    private String loginTime;

    /** 用户状态 */
    private String status;

    public User() {
    }

    /** 内存反序列化对象 */
    public User(Parcel in) {
        super(in);
        id = in.readString();
        userName = in.readString();
        nickName = in.readString();
        userType = in.readString();
        avatar = in.readString();
        deptId = in.readString();
        deptName = in.readString();
        email = in.readString();
        permissions = new String[in.readInt()];
        in.readStringArray(permissions);
        roleIds = new String[in.readInt()];
        in.readStringArray(roleIds);
        phone = in.readString();
        password = in.readString();
        sex = in.readString();
        loginIp = in.readString();
        loginTime = in.readString();
        status = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(id);
        dest.writeString(userName);
        dest.writeString(nickName);
        dest.writeString(userType);
        dest.writeString(avatar);
        dest.writeString(deptId);
        dest.writeString(deptName);
        dest.writeString(email);
        dest.writeInt(permissions.length);
        dest.writeStringArray(permissions);
        dest.writeInt(roleIds.length);
        dest.writeStringArray(roleIds);
        dest.writeString(phone);
        dest.writeString(password);
        dest.writeString(sex);
        dest.writeString(loginIp);
        dest.writeString(loginTime);
        dest.writeString(status);
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

    public static final Creator<User> CREATOR = new Creator() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

}
