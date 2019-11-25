package com.example.myapplication.Activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.Fragments.MyArFragment;
import com.google.ar.core.Anchor;
import com.google.ar.core.AugmentedImage;
import com.google.ar.core.AugmentedImageDatabase;
import com.google.ar.core.Config;
import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.Pose;
import com.google.ar.core.Session;
import com.google.ar.core.TrackingState;
import com.google.ar.core.exceptions.UnavailableApkTooOldException;
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException;
import com.google.ar.core.exceptions.UnavailableDeviceNotCompatibleException;
import com.google.ar.core.exceptions.UnavailableSdkTooOldException;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import com.example.myapplication.R;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import com.google.ar.core.Session;

public class ArActivity  extends AppCompatActivity {
    ArFragment arFragment;
    boolean shouldAddModel = true;
    private AnchorNode currentAnchorNode;
    ModelRenderable cubeRenderable;
    Anchor startAnchor;
    private Anchor currentAnchor = null;
    HitResult hitResult;
    TextView tvDistance;
    boolean addPoint = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar);
        tvDistance = findViewById(R.id.tvDistance);


        arFragment = (MyArFragment) getSupportFragmentManager().findFragmentById(R.id.my_ar_fragment);
        arFragment.getPlaneDiscoveryController().hide();
        arFragment.getArSceneView().getScene().addOnUpdateListener(this::onUpdateFrame);
        arFragment.setOnTapArPlaneListener((hitResult,plane,motionEvent)->{
            Anchor anchor = hitResult.createAnchor();
            AnchorNode anchorNode = new AnchorNode(anchor);
            anchorNode.setParent(arFragment.getArSceneView().getScene());

            currentAnchor = anchor;
            currentAnchorNode = anchorNode;


            TransformableNode node = new TransformableNode(arFragment.getTransformationSystem());
            node.setRenderable(cubeRenderable);
            node.setParent(anchorNode);
            arFragment.getArSceneView().getScene().addOnUpdateListener(this.arFragment);
            arFragment.getArSceneView().getScene().addChild(anchorNode);
            node.select();

            addPoint = true;
        });

    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void onUpdateFrame(FrameTime frameTime) {
        Frame frame = arFragment.getArSceneView().getArFrame();

        if (currentAnchorNode != null && addPoint) {
            Pose objectPose = currentAnchor.getPose();
            Pose cameraPose = frame.getCamera().getPose();

            float dx = objectPose.tx() - cameraPose.tx();
            float dy = objectPose.ty() - cameraPose.ty();
            float dz = objectPose.tz() - cameraPose.tz();

            ///Compute the straight-line distance.
            float distanceMeters = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
            tvDistance.setText("Distance from camera: " + distanceMeters + " metres");

            addPoint = false;
            /*float[] distance_vector = currentAnchor.getPose().inverse()
                    .compose(cameraPose).getTranslation();
            float totalDistanceSquared = 0;
            for (int i = 0; i < 3; ++i)
                totalDistanceSquared += distance_vector[i] * distance_vector[i];*/
        }
//        for (AugmentedImage augmentedImage : augmentedImages) {
//            if (augmentedImage.getTrackingState() == TrackingState.TRACKING) {
//                if (augmentedImage.getName().equals("fox") && shouldAddModel) {
//                    placeObject(arFragment, augmentedImage.createAnchor(augmentedImage.getCenterPose()), Uri.parse("ArcticFox_Posed.sfb"));
//                    shouldAddModel = false;
//                }
//            }
//        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void placeObject(ArFragment arFragment, Anchor anchor, Uri parse) {
        Toast.makeText(getApplicationContext(),"Place Model",Toast.LENGTH_SHORT).show();
        ModelRenderable.builder()
                .setSource(arFragment.getContext(), parse)
                .build()
                .thenAccept(modelRenderable -> addNodeToScene(arFragment, anchor, modelRenderable))
                .exceptionally(throwable -> {
                    Toast.makeText(arFragment.getContext(), "Error:" + throwable.getMessage(), Toast.LENGTH_LONG).show();
                    return null;
                });
    }

    public boolean setupAugmentedImagesDb(Config config, Session session) {
        AugmentedImageDatabase augmentedImageDatabase;
        Bitmap bitmap = loadAugmentedImage();
        if (bitmap == null) {
            return false;
        }        augmentedImageDatabase = new AugmentedImageDatabase(session);
        augmentedImageDatabase.addImage("fox", bitmap);
        config.setAugmentedImageDatabase(augmentedImageDatabase);
        return true;
    }

    private Bitmap loadAugmentedImage() {
        try (InputStream is = getAssets().open("earth.jpg")) {
            Log.wtf("wtf","We're here");
            return BitmapFactory.decodeStream(is);
        } catch (IOException e) {
            Log.wtf("wtf", "IO Exception", e);
        }        return null;
    }

    private void addNodeToScene(ArFragment arFragment, Anchor anchor, Renderable renderable) {
        AnchorNode anchorNode = new AnchorNode(anchor);
        TransformableNode node = new TransformableNode(arFragment.getTransformationSystem());
        node.setRenderable(renderable);
        node.setParent(anchorNode);
        arFragment.getArSceneView().getScene().addChild(anchorNode);
        node.select();
    }
}
