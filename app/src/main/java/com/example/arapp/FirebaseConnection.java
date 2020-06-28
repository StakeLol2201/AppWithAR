package com.example.arapp;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class FirebaseConnection {

    public FirebaseAnalytics mFirebaseAnalytics;
    public FirebaseDatabase database = FirebaseDatabase.getInstance();

    public List<String> models;

    public ArrayList<String> listItems = new ArrayList<String>();
    public ArrayList<String> listKeys = new ArrayList<String>();

    public Boolean next;

    public void saveUserData(String uId, String email, String displayName) {

        DatabaseReference userRef, emailRef, nameRef, idEmpresaRef;

        String trimName = displayName.replaceAll("\\s","").toLowerCase();

        emailRef = database.getReference("users/" + trimName + "/" + "email");
        nameRef = database.getReference("users/" + trimName + "/" + "name");

        emailRef.setValue(email);
        nameRef.setValue(displayName);

    }

}
