package com.example.arapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.LinearLayout;

import java.util.List;

public class ChooseActivity extends AppCompatActivity {

    public List<String> models;

    LinearLayout chooserLay = findViewById(R.id.chooserLayout);
    FirebaseConnection firebaseConn = new FirebaseConnection();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);

        models = firebaseConn.getModels();

        

        chooserLay.setWeightSum(0);
    }
}