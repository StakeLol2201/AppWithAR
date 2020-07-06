package com.example.arapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AdminActivity extends AppCompatActivity{

    Intent launchIntent;

    Button listEnterprise, listUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        listEnterprise.findViewById(R.id.listEnterprises);
        listUsers.findViewById(R.id.listUsers);

        listEnterprise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchIntent = new Intent(AdminActivity.this, EnterprisesModActivity.class);
                startActivityForResult(launchIntent,0);
            }
        });

        listUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchIntent = new Intent(AdminActivity.this, ModUsersActivity.class);
                startActivityForResult(launchIntent,0);
            }
        });

    }

}