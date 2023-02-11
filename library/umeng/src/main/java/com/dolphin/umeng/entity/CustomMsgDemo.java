package com.dolphin.umeng.entity;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 *<p>
 * 演示
 * 自定义消息传输数据
 *</p>
 *
 * @Author: wangxiang4
 * @since: 2023/2/12
 */
@Data
@Accessors
public class CustomMsgDemo implements Parcelable {

    protected String title;

    protected String data;

    public CustomMsgDemo() {}

    public CustomMsgDemo(Parcel in) {
        title = in.readString();
        data = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(data);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CustomMsgDemo> CREATOR = new Creator() {
        @Override
        public CustomMsgDemo createFromParcel(Parcel in) {
            return new CustomMsgDemo(in);
        }

        @Override
        public CustomMsgDemo[] newArray(int size) {
            return new CustomMsgDemo[size];
        }
    };

}
