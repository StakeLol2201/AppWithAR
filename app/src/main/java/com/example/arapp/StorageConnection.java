package com.example.arapp;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Environment;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class StorageConnection {


    BaseActivity baseAct = new BaseActivity();

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();

    File newFile;

    int Timer;

    public void getModelFiles(String modelName) throws IOException, InterruptedException {

        File f = new File(Environment.getDataDirectory() + "/data/com.example.arapp/cache/");
        File[] files = f.listFiles();

        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            file.delete();
        }

        StorageReference gsPNGRef = storageRef.child("models/" + modelName + "/" + modelName + ".png");
        File filePNG = File.createTempFile("model", ".png");

        gsPNGRef.getFile(filePNG).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                newFile = new File("/data/data/com.example.arapp/cache/model.png");
                filePNG.renameTo(newFile);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

        Timer = 0;
        while (!filePNG.exists() && Timer < 30) {
            Thread.sleep(10000, 1000);
            Timer++;
        }

        StorageReference gsMTLRef = storageRef.child("models/" + modelName + "/" + modelName + ".mtl");
        File fileMTL = File.createTempFile("model", ".mtl");

        gsMTLRef.getFile(fileMTL).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                File newFile = new File("/data/data/com.example.arapp/cache/model.mtl");
                fileMTL.renameTo(newFile);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

        Timer = 0;
        while (!fileMTL.exists() && Timer < 30) {
            Thread.sleep(10000, 1000);
            Timer++;
        }

        StorageReference gsOBJRef = storageRef.child("models/" + modelName + "/" + modelName + ".obj");
        File fileOBJ = File.createTempFile("model", ".obj");

        gsOBJRef.getFile(fileOBJ).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                File newFile = new File("/data/data/com.example.arapp/cache/model.obj");
                fileOBJ.renameTo(newFile);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

        Timer = 0;
        while (!fileOBJ.exists() && Timer < 30) {
            Thread.sleep(10000, 1000);
            Timer++;
        }

        StorageReference gsSFARef = storageRef.child("models/" + modelName + "/" + modelName + ".sfa");
        File fileSFA = File.createTempFile("model", ".sfa");

        gsSFARef.getFile(fileSFA).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                File newFile = new File("/data/data/com.example.arapp/cache/model.sfa");
                fileSFA.renameTo(newFile);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

        Timer = 0;
        while (!fileSFA.exists() && Timer < 30) {
            Thread.sleep(10000, 1000);
            Timer++;
        }

        StorageReference gsSFBRef = storageRef.child("models/" + modelName + "/" + modelName + ".sfb");
        File fileSFB = File.createTempFile("model", ".sfb");

        gsSFBRef.getFile(fileSFB).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                File newFile = new File("/data/data/com.example.arapp/cache/model.sfb");
                fileSFB.renameTo(newFile);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

        Timer = 0;
        while (!fileSFB.exists() && Timer < 30) {
            Thread.sleep(10000, 1000);
            Timer++;
        }

    }

}
