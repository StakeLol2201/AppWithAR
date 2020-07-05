package com.example.arapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class AdminActivity extends AppCompatActivity implements View.OnClickListener{

    Intent launchIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.addEnterprise) {
            launchIntent = new Intent(AdminActivity.this, EnterprisesModActivity.class);
            startActivityForResult(launchIntent,0);
        } else if (i == R.id.listUsers) {
            launchIntent = new Intent(AdminActivity.this, ModUsersActivity.class);
            startActivityForResult(launchIntent,0);
        }
    }

}