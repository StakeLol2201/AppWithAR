package com.example.arapp;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseConnection {

    public FirebaseAnalytics mFirebaseAnalytics;

    public void saveUserData(String uId, String email, String displayName) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference userRef, emailRef, nameRef;

        String trimName = displayName.replaceAll("\\s","").toLowerCase();

        emailRef = database.getReference("users/" + trimName + "/" + "email");
        nameRef = database.getReference("users/" + trimName + "/" + "name");

        emailRef.setValue(email);
        nameRef.setValue(displayName);
    }

}
