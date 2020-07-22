package com.example.arapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;

import com.google.ar.core.Anchor;

import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;
import com.google.firebase.analytics.FirebaseAnalytics;

public class ARActivity extends AppCompatActivity {

    private ArFragment arFragment;
    private ModelRenderable modelRenderable;

    private FirebaseAnalytics mFirebaseAnalytics;

    public ProgressDialog mProgressDialog;

    StorageConnection firebaseStorage = new StorageConnection();

    String modelName;

    @SuppressLint("SdCardPath")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arfragment);

        Bundle intentExtras = this.getIntent().getExtras();
        modelName = intentExtras.getString("modelName");

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "model_ar");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "INSERT_MODEL_NAME");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "model 3D");

        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.arFragment);

        ModelRenderable.builder()
                //.setSource(this, R.raw.arcticfoxposed)
                .setSource(this, Uri.parse("/data/data/com.example.arapp/cache/" + modelName + ".sfb"))
                .build()
                .thenAccept(renderable -> modelRenderable = renderable)
                .exceptionally(throwable -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ARActivity.this);
                    builder.setMessage(throwable.getMessage())
                            .show();
                    return null;
                });

        arFragment.setOnTapArPlaneListener((HitResult hitResult, Plane plane, MotionEvent motionEvent) -> {
            if (modelRenderable == null) {
                return;
            }
            Anchor anchor = hitResult.createAnchor();
            AnchorNode anchorNode = new AnchorNode(anchor);
            anchorNode.setParent(arFragment.getArSceneView().getScene());

            TransformableNode model = new TransformableNode(arFragment.getTransformationSystem());
            model.setParent(anchorNode);
            model.setRenderable(modelRenderable);
            model.select();
        });
    }



}
