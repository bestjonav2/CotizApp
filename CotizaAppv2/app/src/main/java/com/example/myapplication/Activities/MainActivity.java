package com.example.myapplication.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Firebase Auth", "signInAnonymously:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Firebase Auth", "signInAnonymously:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        List<Cotization> data = new ArrayList<>();
        Context ctx = this;
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv);

        db.collection("cotization")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("Firebase GetData", document.getId() + " => " + document.getData() + " => " + document.get("description").toString());
                                data.add(new Cotization(document.getId(), document.get("description").toString(), document.getDouble("averageCost"), document.get("figureVolume").toString(), document.get("url").toString()));
                            }

                            RecyclerAdapter recyclerAdapter = new RecyclerAdapter(ctx, data);
                            recyclerView.setLayoutManager(new LinearLayoutManager(ctx, RecyclerView.HORIZONTAL, false));
                            recyclerView.addItemDecoration(new LeftSpacing(150));
                            recyclerView.setAdapter(recyclerAdapter);
                        } else {
                            Log.w("Firebase GetData", "Error getting documents.", task.getException());
                        }
                    }
                });

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


        /*data.add(new Cotization("22", "Pieza para jalarse el pito", 22.5, new ArrayList<MeasurePoint>(), new ArrayList<MeasurePoint>()));
        data.add(new Cotization("33", "Pieza para una pata de un wey", 78.2, new ArrayList<MeasurePoint>(), new ArrayList<MeasurePoint>()));
        data.add(new Cotization("36", "Pieza para un carro de RC", 6969.69, new ArrayList<MeasurePoint>(), new ArrayList<MeasurePoint>()));
        data.add(new Cotization("40", "Pieza para algo", 12.34, new ArrayList<MeasurePoint>(), new ArrayList<MeasurePoint>()));
        data.add(new Cotization("69", "Pieza para jalarse el pito v2", 420.69, new ArrayList<MeasurePoint>(), new ArrayList<MeasurePoint>()));*/
        //add information each of cardview
        //load imageView by picasso
        return null;
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