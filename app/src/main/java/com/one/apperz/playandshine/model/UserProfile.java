package com.one.apperz.playandshine.model;

import java.io.Serializable;

public class UserProfile implements Serializable {
    private String uid;
    private String name;
    private String email;
    private String sport;
    private String type;
    private String photoURL;
    private String achievement;
    private int age;
    private String phone;
    private String location;
    private String experience;

    public UserProfile(String uid,String name, String email, String sport, String type, String photoURL, String achievement, int age, String phone, String location,String experience) {
        this.name = name;
        this.email = email;
        this.sport = sport;
        this.type = type;
        this.photoURL = photoURL;
        this.achievement = achievement;
        this.age = age;
        this.phone = phone;
        this.location = location;
        this.uid = uid;
        this.experience = experience;
    }

    public UserProfile() {
        this.name = "";
        this.email = "";
        this.sport = "";
        this.type = "";
        this.photoURL = "";
        this.achievement = "";
        this.age = 0;
        this.phone = "";
        this.location = "";
        this.uid = "";
        this.experience = "";
    }

    public String getExperience() {
        return this.experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getUid() {
        return this.uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSport() {
        return this.sport;
    }

    public void setSport(String sport) {
        this.sport = sport;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

    public String getAchievement() {
        return achievement;
    }

    public void setAchievement(String achievement) {
        this.achievement = achievement;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

}
