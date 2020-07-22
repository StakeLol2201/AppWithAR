package com.example.arapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ChooseActivity extends AppCompatActivity {

    ListView dataListView;
    Button showModel;

    String idEmpresa;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference dbRef;

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();

    StorageConnection storageConn = new StorageConnection();

    ArrayAdapter<String> adapter;
    ArrayList<String> listItems = new ArrayList<String>();
    ArrayList<String> listKeys = new ArrayList<String>();

    public ProgressDialog mProgressDialog;

    private EditText itemText;
    private Boolean itemSelected = false;
    private int selectedPosition = 0;

    File SFB, ZIP, Directory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);

        this.setTitle("Seleccione un modelo");

        dataListView = findViewById(R.id.dataListView);
        showModel = findViewById(R.id.showModel);

        Bundle loginExtras = this.getIntent().getExtras();

        dbRef = database.getReference("models/");
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, listItems);
        dataListView.setAdapter(adapter);
        dataListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        showModel.setEnabled(false);

        showLoadDataDialog();

        addChildEventListener();

        hideProgressDialog();

        dataListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                selectedPosition = position;
                itemSelected = true;
                showModel.setEnabled(true);

                showModel.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        String model = dataListView.getItemAtPosition(selectedPosition).toString().toLowerCase().replaceAll("\\s","");
                        try {
                            downloadFiles(model);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        });
    }

    private void addChildEventListener() {
        ChildEventListener childListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                adapter.add(
                        (String) dataSnapshot.child("modelName").getValue());
                listKeys.add((String) dataSnapshot.child("modelName").getValue());

            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        };
        dbRef.addChildEventListener(childListener);
    }

    public void addItem(View view) {

        String item = itemText.getText().toString();
        String key = dbRef.push().getKey();

        itemText.setText("");
        dbRef.child(key).child("modelName").setValue(item);

        adapter.notifyDataSetChanged();
    }

    ValueEventListener queryValueListener = new ValueEventListener() {

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
            Iterator<DataSnapshot> iterator = snapshotIterator.iterator();

            adapter.clear();
            listKeys.clear();

            while (iterator.hasNext()) {
                DataSnapshot next = iterator.next();

                String match = (String) next.child("modelName").getValue();
                String key = next.getKey();
                listKeys.add(key);
                adapter.add(match);
            }

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    public void downloadFiles(String modelName) throws IOException {

        ZIP = new File("/data/data/com.example.arapp/cache/" + modelName + ".zip");
        SFB = new File("/data/data/com.example.arapp/cache/" + modelName + ".sfb");
        Directory = new File("/data/data/com.example.arapp/cache/");

        showProgressDialog();

        if (!SFB.exists()) {
            StorageReference gsZIPRef = storageRef.child("models/" + modelName + "/" + modelName + ".zip");

            gsZIPRef.getFile(ZIP).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    try {
                        unzip(ZIP, Directory);

                        ZIP.delete();

                        hideProgressDialog();

                        Intent launchIntent = new Intent(ChooseActivity.this, ARActivity.class);
                        launchIntent.putExtra("modelName", modelName);
                        startActivityForResult(launchIntent, 0);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        } else {

            hideProgressDialog();

            Intent launchIntent = new Intent(ChooseActivity.this, ARActivity.class);
            launchIntent.putExtra("modelName", modelName);
            startActivityForResult(launchIntent, 0);
        }

        /*File f = new File(Environment.getDataDirectory() + "/data/com.example.arapp/cache/");
        File[] files = f.listFiles();

        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            file.delete();
        }*/

    }

    public void showLoadDataDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.obtain_model_list));
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.getting_model_files));
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
    }

    public void unzip(File zipFile, File targetDirectory) throws IOException {
        ZipInputStream zip = new ZipInputStream(
                new BufferedInputStream(new FileInputStream(zipFile)));

        try {

            ZipEntry ze;
            int count;
            byte[] buffer = new byte[8192];

            while ((ze = zip.getNextEntry()) != null) {

                File file = new File(targetDirectory, ze.getName());
                File dir = ze.isDirectory() ? file : file.getParentFile();

                if (!dir.isDirectory() && !dir.mkdirs())
                    throw new FileNotFoundException("Failed" + dir.getAbsolutePath());
                if (ze.isDirectory())
                    continue;

                FileOutputStream fout = new FileOutputStream(file);

                try {
                    while ((count = zip.read(buffer)) != -1)
                        fout.write(buffer, 0, count);
                } finally {
                    fout.close();
                }
            }
        } finally {
            zip.close();
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder exitDialog = new AlertDialog.Builder(this);
        exitDialog.setTitle("Cerrar aplicación");
        exitDialog.setIcon(R.mipmap.ic_launcher);
        exitDialog.setMessage("¿Seguro de cerrar la aplicación?")
                .setCancelable(false)
                .setPositiveButton("Cerrar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        moveTaskToBack(true);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    }
                });
        AlertDialog alert = exitDialog.create();
        alert.show();
    }

}