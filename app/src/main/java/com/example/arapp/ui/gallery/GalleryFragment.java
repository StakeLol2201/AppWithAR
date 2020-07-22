package com.example.arapp.ui.gallery;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.arapp.ARActivity;
import com.example.arapp.R;
import com.example.arapp.StorageConnection;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class GalleryFragment extends Fragment {

    //Models Fragment

    ListView dataListView;
    Button showModel;

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
    Boolean isConencted;

    //End Models Fragment

    private GalleryViewModel galleryViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                ViewModelProviders.of(this).get(GalleryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_models_houses, container, false);
        //MODEL FRAGMENT

        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        dataListView = root.findViewById(R.id.dataListView);
        showModel = root.findViewById(R.id.showModel);
        dbRef = database.getReference("models/");
        adapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_single_choice, listItems);
        dataListView.setAdapter(adapter);
        dataListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        addChildEventListener();
        dataListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedPosition = position;
                itemSelected = true;
                showModel.setEnabled(true);
                showModel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (networkInfo != null && networkInfo.isConnected()) {
                            String model = dataListView.getItemAtPosition(selectedPosition).toString().toLowerCase().replaceAll("\\s", "");
                            try {
                                downloadFiles(model);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        } else {

                        }

                    }
                });
            }
        });

        showModel.setEnabled(false);

        //END MODEL FRAGMENT

        return root;
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
    public void downloadFiles(String modelName) throws IOException {

        ZIP = new File("/data/data/com.example.arapp/cache/" + modelName + ".zip");
        SFB = new File("/data/data/com.example.arapp/cache/" + modelName + ".sfb");
        Directory = new File("/data/data/com.example.arapp/cache/");

        ProgressDialog dialog = ProgressDialog.show(getActivity(), "Loading...", "Please Wait", true);

        if (!SFB.exists()) {

            StorageReference gsZIPRef = storageRef.child("models/" + modelName + "/" + modelName + ".zip");

            gsZIPRef.getFile(ZIP).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    try {
                        unzip(ZIP, Directory);

                        ZIP.delete();

                        dialog.dismiss();

                        Intent launchIntent = new Intent(getActivity(), ARActivity.class);
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

            dialog.dismiss();

            Intent launchIntent = new Intent(getActivity(), ARActivity.class);
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
    private void addChildEventListener() {
        ChildEventListener childListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                if (dataSnapshot.child("modelType").getValue(String.class).equals("casas")) {
                    adapter.add(
                            (String) dataSnapshot.child("modelName").getValue());
                    listKeys.add((String) dataSnapshot.child("modelName").getValue());
                }
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

}