package com.gproject.plus.binge;

public class model {
    String admin, img, link, message, name, official, date, button, vid, id, views;

    public model() {
    }

    public model(String admin, String img, String link, String message, String name, String official, String date, String id, String views) {
        this.admin = admin;
        this.img = img;
        this.link = link;
        this.message = message;
        this.name = name;
        this.official = official;
        this.date = date;
        this.id = id;
        this.views = views;
    }

    public String getViews() {
        return views;
    }

    public void setViews(String views) {
        this.views = views;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public model(String button) {
        this.button = button;
    }

    public String getButton() {
        return button;
    }

    public String getVid() {
        return vid;
    }

    public void setVid(String vid) {
        this.vid = vid;
    }

    public void setButton(String button) {
        this.button = button;
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

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOfficial() {
        return official;
    }

    public void setOfficial(String official) {
        this.official = official;
    }
}

