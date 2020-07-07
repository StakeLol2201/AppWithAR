package com.example.arapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AdminActivity extends AppCompatActivity{

    Intent launchIntent;

    Button btnListEnterprises, btnListUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        btnListEnterprises = findViewById(R.id.listEnterprises);
        btnListUsers = findViewById(R.id.listUsers);

        btnListEnterprises.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchIntent = new Intent(AdminActivity.this, ListEnterpriseActivity.class);
                startActivityForResult(launchIntent,0);
            }
        });

        btnListUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchIntent = new Intent(AdminActivity.this, ListUsersActivity.class);
                startActivityForResult(launchIntent,0);
            }
        });

    }



}