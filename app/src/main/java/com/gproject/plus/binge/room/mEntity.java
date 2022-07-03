package com.gproject.plus.binge.room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class mEntity {

    @PrimaryKey (autoGenerate = true)
    public int mid;

    @ColumnInfo(name = "m_key")
    public String key;

    @ColumnInfo(name = "m_date")
    public String date;

    @ColumnInfo(name = "m_admin")
    public String admin;

    @ColumnInfo(name = "m_name")
    public String name;

    @ColumnInfo(name = "m_img")
    public String img;

    @ColumnInfo(name = "m_message")
    public String message;

    @ColumnInfo(name = "m_link")
    public String link;

    @ColumnInfo(name = "m_vid")
    public String vid;

    @ColumnInfo(name = "m_views")
    public String views;

    @ColumnInfo(name = "m_addTime")
    public String addTime;


    public mEntity(int mid, String key, String date, String admin, String name, String img, String message, String link, String vid, String views, String addTime) {
        this.mid = mid;
        this.date = date;
        this.admin = admin;
        this.name = name;
        this.img = img;
        this.message = message;
        this.link = link;
        this.vid = vid;
        this.views = views;
        this.key = key;
        this.addTime = addTime;
    }

    public String getAddTime() {
        return addTime;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getMid() {
        return mid;
    }

    public void setMid(int mid) {
        this.mid = mid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getVid() {
        return vid;
    }

    public void setVid(String vid) {
        this.vid = vid;
    }

    public String getViews() {
        return views;
    }

    public void setViews(String views) {
        this.views = views;
    }
}
