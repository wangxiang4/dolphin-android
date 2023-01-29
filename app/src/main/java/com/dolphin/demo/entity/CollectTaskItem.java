package com.dolphin.demo.entity;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p> 任务列表示例实体类 </p>
 * @Author: liuSiXiang
 * @since: 2022/11/15
 */
@Data
@Accessors
public class CollectTaskItem implements Parcelable {
    private String name;
    private String id;

    public CollectTaskItem(Parcel in) {
        name = in.readString();
        id = in.readString();
    }

    public CollectTaskItem(int id, String name) {
        this.name = name;
        this.id = String.valueOf(id);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(id);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CollectTaskItem> CREATOR = new Creator<CollectTaskItem>() {
        @Override
        public CollectTaskItem createFromParcel(Parcel in) {
            return new CollectTaskItem(in);
        }

        @Override
        public CollectTaskItem[] newArray(int size) {
            return new CollectTaskItem[size];
        }
    };
}
