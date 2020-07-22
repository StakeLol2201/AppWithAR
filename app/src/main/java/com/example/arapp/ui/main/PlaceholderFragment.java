package com.example.arapp.ui.main;

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
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
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

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private PageViewModel pageViewModel;

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

    public static PlaceholderFragment newInstance(int index) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        pageViewModel.setIndex(index);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_choose_model, container, false);

        pageViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

                //MODEL FRAGMENT

                ConnectivityManager connectivityManager = (ConnectivityManager) getContext()
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                dataListView = root.findViewById(R.id.dataListView);
                showModel = root.findViewById(R.id.showModel);
                dbRef = database.getReference("models/");
                adapter = new ArrayAdapter<String>(getActivity().getApplicationContext(),
                        android.R.layout.simple_list_item_single_choice, listItems);
                dataListView.setAdapter(adapter);
                dataListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                addChildEventListener(s);
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
                                    String model = dataListView.getItemAtPosition(selectedPosition)
                                            .toString().toLowerCase().replaceAll("\\s", "");
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

            }
        });
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

    }

    private void addChildEventListener(String modelType) {
        ChildEventListener childListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                String model = dataSnapshot.child("modelType").getValue(String.class);

                if (model.equals(modelType)) {

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