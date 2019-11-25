package com.example.myapplication.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.example.myapplication.Activities.ArActivity;

import com.example.myapplication.Fragments.MyArFragment;
import com.example.myapplication.R;
import com.github.florent37.camerafragment.configuration.Configuration;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.ar.core.Anchor;
import com.google.ar.core.AugmentedImage;
import com.google.ar.core.AugmentedImageDatabase;
import com.google.ar.core.Config;
import com.google.ar.core.Frame;
import com.google.ar.core.Session;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.github.florent37.camerafragment.CameraFragment;



public class MainActivity extends AppCompatActivity{
    BottomNavigationView nav;
    private CameraFragment cameraFragment;
    Intent inArFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inArFragment = new Intent(this, ArActivity.class);

        hasPermissionAndOpenCamera();
        startActivity(inArFragment);
        //getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MyArFragment()).commit();
    }

    private void hasPermissionAndOpenCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startActivityCameraFragment();
        } else {
            requestPermission();
        }
    }

    private void startActivityCameraFragment() {
        //cameraFragment = CameraFragment.newInstance(new Configuration.Builder().build());
    }

    private void requestPermission() {
        String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

        ActivityCompat.requestPermissions(this, permissions, PackageManager.PERMISSION_GRANTED);
    }

}