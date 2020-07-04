package com.example.arapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AdminActivity extends AppCompatActivity implements View.OnClickListener{

    Button bntEmpresas, btnUsuarios;

    Intent launchIntent = new Intent(AdminActivity.this, AdminModActivity.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.listEnterprises) {
            launchIntent.putExtra("listMod", "empresas");
        } else if (i == R.id.listUsers) {
            launchIntent.putExtra("listMod", "usuarios");
        }
    }

}