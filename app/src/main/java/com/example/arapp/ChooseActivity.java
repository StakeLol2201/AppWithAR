package com.example.arapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class ChooseActivity extends AppCompatActivity {

    ListView dataListView;
    Button showModel;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference dbRef = database.getReference("models");

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

    File MTL,OBJ,PNG,SFA,SFB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);

        dataListView = findViewById(R.id.dataListView);
        showModel = findViewById(R.id.modelButton);

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, listItems);

        dataListView.setAdapter(adapter);
        dataListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        showModel.setEnabled(false);

        dataListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedPosition = position;
                itemSelected = true;
                showModel.setEnabled(true);

                showModel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String model = dataListView.getItemAtPosition(selectedPosition).toString();

                        try {
                            downloadFiles(model);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                });

            }
        });

        addChildEventListener();

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
                DataSnapshot next = (DataSnapshot) iterator.next();

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

        MTL = new File("/data/data/com.example.arapp/cache/model.mtl");
        PNG = new File("/data/data/com.example.arapp/cache/model.png");
        OBJ = new File("/data/data/com.example.arapp/cache/model.obj");
        SFA = new File("/data/data/com.example.arapp/cache/model.sfa");
        SFB = new File("/data/data/com.example.arapp/cache/model.sfb");

        showProgressDialog();

        File f = new File(Environment.getDataDirectory() + "/data/com.example.arapp/cache/");
        File[] files = f.listFiles();

        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            file.delete();
        }

        StorageReference gsPNGRef = storageRef.child("models/" + modelName + "/" + modelName + ".png");

        gsPNGRef.getFile(PNG).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                StorageReference gsMTLRef = storageRef.child("models/" + modelName + "/" + modelName + ".mtl");

                gsMTLRef.getFile(MTL).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                        StorageReference gsOBJRef = storageRef.child("models/" + modelName + "/" + modelName + ".obj");

                        gsOBJRef.getFile(OBJ).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                                StorageReference gsSFARef = storageRef.child("models/" + modelName + "/" + modelName + ".sfa");

                                gsSFARef.getFile(SFA).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                                        StorageReference gsSFBRef = storageRef.child("models/" + modelName + "/" + modelName + ".sfb");

                                        gsSFBRef.getFile(SFB).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                                                hideProgressDialog();

                                                Intent launchIntent = new Intent(getApplicationContext(), ARActivity.class);
                                                startActivityForResult(launchIntent, 0);

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {

                                            }
                                        });

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public void hideKeyboard(View view) {
        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
    }

}