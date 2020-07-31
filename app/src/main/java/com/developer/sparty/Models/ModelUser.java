package com.developer.sparty.Models;

public class ModelUser {

    String fullname,phone,image,search,uid,onlineStatus,typingTo;

    public ModelUser(String fullname, String phone, String image, String search, String uid, String onlineStatus, String typingTo) {
        this.fullname = fullname;
        this.phone = phone;
        this.image = image;
        this.search = search;
        this.uid = uid;
        this.onlineStatus = onlineStatus;
        this.typingTo = typingTo;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
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

    public String getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(String onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public String getTypingTo() {
        return typingTo;
    }

    public void setTypingTo(String typingTo) {
        this.typingTo = typingTo;
    }

    public ModelUser() {
    }

}
