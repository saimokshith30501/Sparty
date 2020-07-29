package com.developer.sparty.Models;

public class ModelUser {

    String username,phone,image,search,uid;

    public ModelUser() {
    }

    public ModelUser(String username, String phone, String image, String search, String uid) {
        this.username = username;
        this.phone = phone;
        this.image = image;
        this.search = search;
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
