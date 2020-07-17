package com.example.arapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.filament.Material;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class AdministrationActivity extends AppCompatActivity {

    //Models Fragment

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

    File MTL,OBJ,PNG,SFA,SFB, ZIP, Directory;

    //End Models Fragment

    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administration);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.administration, menu);

        //HOME FRAGMENT

        TextView adminEmail, adminName;
        ImageView adminPhoto;
        String email,name, photoURL;

        adminEmail = findViewById(R.id.textAdminEmail);
        adminName = findViewById(R.id.textAdminName);

        final CardView userData = findViewById(R.id.cardUserData);

        Bundle loginExtras = this.getIntent().getExtras();

        email = loginExtras.getString("adminEmail");
        name = loginExtras.getString("adminName");
        photoURL = loginExtras.getString("adminPhotoURL");

        adminEmail.setText(email);
        adminName.setText(name);

        new DownloadImageTask((ImageView) findViewById(R.id.imageAdminPhoto)).execute(photoURL);

        //END HOME FRAGMENT

        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            String URLDisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(URLDisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return mIcon11;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            bmImage.setImageBitmap(bitmap);
        }
    }

    //Fragment Models

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
        Directory = new File("/data/data/com.example.arapp/cache/");

        showProgressDialog();

        File f = new File(Environment.getDataDirectory() + "/data/com.example.arapp/cache/");
        File[] files = f.listFiles();

        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            file.delete();
        }

        StorageReference gsZIPRef = storageRef.child("models/" + modelName + "/" + modelName + ".zip");

        gsZIPRef.getFile(ZIP).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                try {
                    unzip(ZIP, Directory);

                    ZIP.delete();

                    hideProgressDialog();

                    Intent launchIntent = new Intent(getApplicationContext(), ARActivity.class);
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
    private void addChildEventListener() {
        ChildEventListener childListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                String modelIdEmrpesa = dataSnapshot.child("idEmpresa").getValue(String.class);

                if (modelIdEmrpesa.equals(idEmpresa)) {

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

    public void OnClick(View v) {
        int i = v.getId();
        if (i == R.id.log_out){
            MaterialAlertDialogBuilder logoutDialog = new MaterialAlertDialogBuilder(getApplicationContext())
                    .setTitle("Cerrar sesión")
                    .setMessage("¿Seguro que desea cerrar sesión?")
                    .setCancelable(true);

            logoutDialog
                    .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

            logoutDialog.create();
            logoutDialog.show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}