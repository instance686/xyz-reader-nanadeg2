package com.example.xyzreader.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;


import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 * Created by ayush on 1/12/17.
 */
@Entity(tableName = "data")
public class Data implements Parcelable{
    @PrimaryKey@NonNull
    public String id;
    public String title;
    public String author;
    public String body;
    public String thumb;
    public String photo;
    public String aspect_ratio;
    public String published_date;

    public Data(){}

    protected Data(Parcel in) {
        id = in.readString();
        title = in.readString();
        author = in.readString();
        body = in.readString();
        thumb = in.readString();
        photo = in.readString();
        aspect_ratio = in.readString();
        published_date = in.readString();
    }

    public static final Creator<Data> CREATOR = new Creator<Data>() {
        @Override
        public Data createFromParcel(Parcel in) {
            return new Data(in);
        }

        @Override
        public Data[] newArray(int size) {
            return new Data[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getAspect_ratio() {
        return aspect_ratio;
    }

    public void setAspect_ratio(String aspect_ratio) {
        this.aspect_ratio = aspect_ratio;
    }

    public String getPublished_date() {
        return published_date;
    }

    public void setPublished_date(String published_date) {
        this.published_date = published_date;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(title);
        parcel.writeString(author);
        parcel.writeString(body);
        parcel.writeString(thumb);
        parcel.writeString(photo);
        parcel.writeString(aspect_ratio);
        parcel.writeString(published_date);
    }
}