package com.example.arapp;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Post {

    public String email, username, phone;

    public Post() {

    }

    public Post(String email, String username, String phone) {
        this.email = email;
        this.username = username;
        this.phone = phone;
    }
}
