package com.example.tutor_project.Model;

public class ModelUser {
    String name,email,uid,image;

    public ModelUser() {
    }

    public ModelUser(String name, String email, String uid,String image) {
        this.name = name;
        this.email = email;
        this.uid = uid;
        this.image = image;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
