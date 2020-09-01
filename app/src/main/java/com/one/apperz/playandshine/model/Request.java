package com.one.apperz.playandshine.model;

public class Request {

    private String uidAthlete;
    private String uidProfessional;
    private String name;
    private String title;
    private String photoURL;
    private String type;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    private String status;


    public Request() {
        uidAthlete = "";
        uidProfessional = "";
        name = "";
        title = "";
        photoURL = "";
        type = "";
        status = "";
    }

    public Request(String uidAthlete, String uidProfessional, String name, String title, String photoURL, String type, String status) {
        this.uidAthlete = uidAthlete;
        this.uidProfessional = uidProfessional;
        this.name = name;
        this.title = title;
        this.type = type;
        this.photoURL = photoURL;
        this.status = status;
    }


    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

    public String getUidAthlete() {
        return uidAthlete;
    }

    public void setUidAthlete(String uidAthlete) {
        this.uidAthlete = uidAthlete;
    }

    public String getUidProfessional() {
        return uidProfessional;
    }

    public void setUidProfessional(String uidProfessional) {
        this.uidProfessional = uidProfessional;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
