package com.example.arapp;

import androidx.annotation.NonNull;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

        DatabaseReference userRef, emailRef, nameRef, idEmpresaRef, userTypeRef, setUserType;

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        String trimName = displayName.replaceAll("\\s", "").toLowerCase();

        emailRef = database.getReference("users/" + trimName + "/email");
        nameRef = database.getReference("users/" + trimName + "/name");
        userTypeRef = database.getReference("users/" + trimName + "/userType");
        idEmpresaRef = database.getReference("users/" + trimName + "/idEmpresa");

        emailRef.setValue(email);
        nameRef.setValue(displayName);

        userTypeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String userType = dataSnapshot.getValue(String.class);

                if (userType != "admin")
                    userTypeRef.setValue("user");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                userTypeRef.setValue("user");

            }
        });

        idEmpresaRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String idEmpresa = dataSnapshot.getValue(String.class);

                if (idEmpresa == "")
                    idEmpresaRef.setValue("notEnterprise");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                idEmpresaRef.setValue("notEnterprise");

            }
        });


    }

}
