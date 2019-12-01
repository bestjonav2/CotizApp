package com.example.myapplication.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.Models.Cotization;
import com.example.myapplication.Models.MeasurePoint;
import com.example.myapplication.R;
import com.example.myapplication.RecyclerAdapter;
import com.github.florent37.camerafragment.CameraFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity{
    BottomNavigationView nav;
    private CameraFragment cameraFragment;
    Intent inArFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inArFragment = new Intent(this, ArActivity.class);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv);
        RecyclerAdapter recyclerAdapter = new RecyclerAdapter(this, getData());
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        recyclerView.addItemDecoration(new LeftSpacing(150));
        recyclerView.setAdapter(recyclerAdapter);

        /*hasPermissionAndOpenCamera();
        startActivity(inArFragment);*/
    }

    private void hasPermissionAndOpenCamera() {
        if (!(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)) {
            requestPermission();
        }
    }

    private void requestPermission() {
        String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

        ActivityCompat.requestPermissions(this, permissions, PackageManager.PERMISSION_GRANTED);
    }

    private List<Cotization> getData() {
        List<Cotization> data = new ArrayList<>();

        data.add(new Cotization("22", "Pieza para jalarse el pito", 22.5, new ArrayList<MeasurePoint>(), new ArrayList<MeasurePoint>()));
        data.add(new Cotization("33", "Pieza para una pata de un wey", 78.2, new ArrayList<MeasurePoint>(), new ArrayList<MeasurePoint>()));
        data.add(new Cotization("36", "Pieza para un carro de RC", 6969.69, new ArrayList<MeasurePoint>(), new ArrayList<MeasurePoint>()));
        data.add(new Cotization("40", "Pieza para algo", 12.34, new ArrayList<MeasurePoint>(), new ArrayList<MeasurePoint>()));
        data.add(new Cotization("69", "Pieza para jalarse el pito v2", 420.69, new ArrayList<MeasurePoint>(), new ArrayList<MeasurePoint>()));
        //add information each of cardview
        //load imageView by picasso
        return data;
    }

    public void openCamera(View view) {
        hasPermissionAndOpenCamera();
        startActivity(inArFragment);
    }
}

class LeftSpacing extends RecyclerView.ItemDecoration {

    private final int paddingLeft1stItem;

    public LeftSpacing(int paddingLeft1stItem) {
        this.paddingLeft1stItem = paddingLeft1stItem;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if(parent.getChildAdapterPosition(view) == 0) {
            outRect.left = paddingLeft1stItem;
        }
    }
}