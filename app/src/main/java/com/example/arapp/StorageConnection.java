package com.example.arapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class StorageConnection {

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();

    File newFile, MTL,OBJ,PNG,SFA,SFB;

    int Timer;

    public void getModelFiles(String modelName) throws IOException, InterruptedException {

        MTL = new File("/data/data/com.example.arapp/cache/model.mtl");
        PNG = new File("/data/data/com.example.arapp/cache/model.png");
        OBJ = new File("/data/data/com.example.arapp/cache/model.obj");
        SFA = new File("/data/data/com.example.arapp/cache/model.sfa");
        SFB = new File("/data/data/com.example.arapp/cache/model.sfb");

        File f = new File(Environment.getDataDirectory() + "/data/com.example.arapp/cache/");
        File[] files = f.listFiles();

        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            file.delete();
        }

        StorageReference gsPNGRef = storageRef.child("models/" + modelName + "/" + modelName + ".png");
        //File filePNG = File.createTempFile("model", ".png");


        gsPNGRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {

            }
        });

        /*gsPNGRef.getFile(filePNG).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                newFile = new File("/data/data/com.example.arapp/cache/model.png");
                filePNG.renameTo(newFile);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        }).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                newFile = new File("/data/data/com.example.arapp/cache/model.png");
                filePNG.renameTo(newFile);
            }
        });*/

        /*Timer = 0;
        while (!filePNG.exists() && Timer < 60) {
            Thread.sleep(500000, 1000);
            Timer++;
        }*/

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
        }).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                File newFile = new File("/data/data/com.example.arapp/cache/model.mtl");
                fileMTL.renameTo(newFile);
            }
        });

        Timer = 0;
        while (!fileMTL.exists() && Timer < 60) {
            Thread.sleep(500000, 1000);
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
        }).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                File newFile = new File("/data/data/com.example.arapp/cache/model.obj");
                fileOBJ.renameTo(newFile);
            }
        });

        Timer = 0;
        while (!fileOBJ.exists() && Timer < 60) {
            Thread.sleep(500000, 1000);
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
        }).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                File newFile = new File("/data/data/com.example.arapp/cache/model.sfa");
                fileSFA.renameTo(newFile);
            }
        });

        Timer = 0;
        while (!fileSFA.exists() && Timer < 60) {
            Thread.sleep(500000, 1000);
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
        }).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                File newFile = new File("/data/data/com.example.arapp/cache/model.sfb");
                fileSFB.renameTo(newFile);
            }
        });

        Timer = 0;
        while (!fileSFB.exists() && Timer < 60) {
            Thread.sleep(500000, 1000);
            Timer++;
        }

    }

}
