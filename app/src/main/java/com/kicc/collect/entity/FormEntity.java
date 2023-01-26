package com.kicc.collect.entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.databinding.BaseObservable;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 *<p>
 * 表单实体
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @Date: 2022/6/27
 */
@Data
@Accessors
public class FormEntity extends BaseObservable implements Parcelable {
    private String id;
    private String name;
    private String sex;
    private String Bir;
    private String hobby;
    private Boolean isMarry;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.sex);
        dest.writeString(this.Bir);
        dest.writeString(this.hobby);
        dest.writeValue(this.isMarry);
    }

    protected FormEntity(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.sex = in.readString();
        this.Bir = in.readString();
        this.hobby = in.readString();
        this.isMarry = (Boolean) in.readValue(Boolean.class.getClassLoader());
    }

    public static final Creator<FormEntity> CREATOR = new Creator() {
        @Override
        public FormEntity createFromParcel(Parcel source) {
            return new FormEntity(source);
        }

        @Override
        public FormEntity[] newArray(int size) {
            return new FormEntity[size];
        }
    };
}
