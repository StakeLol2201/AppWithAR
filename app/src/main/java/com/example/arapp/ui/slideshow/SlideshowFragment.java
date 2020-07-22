package com.example.arapp.ui.slideshow;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.arapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

public class SlideshowFragment extends Fragment {

    TextView enterpriseName, rutEmpresa, representanteLegal, region, comuna, direccion;
    ImageView logoEmpresa;

    File enterpriseLogo;

    Bitmap logo;

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_enterprise_data, container, false);

        enterpriseName = root.findViewById(R.id.enterpriseName);
        rutEmpresa = root.findViewById(R.id.rutEmpresa);
        representanteLegal = root.findViewById(R.id.representanteLegal);
        direccion = root.findViewById(R.id.direccion);

        logoEmpresa = root.findViewById(R.id.enterpriseLogo);

        Bitmap logoEnterprise = DownloadLogo();

        logoEmpresa.setImageBitmap(logoEnterprise);

        FirebaseDatabase dataBase = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = dataBase.getReference("empresas/engelyvolkers/");

        dbRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                enterpriseName.setText(getString(R.string.enterpriseName) + " " + snapshot.child("nombre").getValue(String.class));
                rutEmpresa.setText(getString(R.string.enterpriseRut) + " " + snapshot.child("rut empresa").getValue(String.class));
                representanteLegal.setText(getString(R.string.legalRepresentant) + " " + snapshot.child("representante legal").getValue(String.class));
                direccion.setText(getString(R.string.address) + " " + snapshot.child("direccion").getValue(String.class)
                    + ", " + snapshot.child("comuna").getValue(String.class)
                    + ", " + snapshot.child("region").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return root;
    }

    public Bitmap DownloadLogo() {
        enterpriseLogo = new File("/data/data/com.example.arapp/files/enterpriselogo.jpg");

        ProgressDialog dialog = ProgressDialog.show(getActivity(), "Descargando información", "Estamos obteniendo la información de la empresa, por favor espere.", true);

        if (!enterpriseLogo.exists()) {

            StorageReference logoDownload = storageRef.child("logo/E&V_Logo_CMYK.jpg");

            logoDownload.getFile(enterpriseLogo).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                    logo = BitmapFactory.decodeFile(enterpriseLogo.getPath());

                }
            });

        } else {
            logo = BitmapFactory.decodeFile(enterpriseLogo.getPath());
        }

        dialog.dismiss();

        return logo;

    }
}