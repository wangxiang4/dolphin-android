package com.kicc.collect.entity;

import android.os.Parcel;

import com.kicc.core.entity.CommonEntity;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p> 消息实体类 </p>
 * @Author: liuSiXiang
 * @since: 2022/11/18
 */
@Data
@Accessors
public class Message extends CommonEntity{

    /** 主键id */
    private String id;

    /** 消息名称 */
    private String name;

    /** 消息类型 */
    private String type;

    /** 消息状态 0-未读 1-已读 */
    private String status;

    /** 消息内容 */
    private String content;

    /** 内存反序列化对象 */
    public Message(Parcel in) {
        id = in.readString();
        name = in.readString();
        type = in.readString();
        status = in.readString();
        content = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(type);
        dest.writeString(status);
        dest.writeString(content);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Message> CREATOR = new Creator() {
        @Override
        public Message createFromParcel(Parcel in) {
            return new Message(in);
        }

        @Override
        public Message[] newArray(int size) {
            return new Message[size];
        }
    };
}
