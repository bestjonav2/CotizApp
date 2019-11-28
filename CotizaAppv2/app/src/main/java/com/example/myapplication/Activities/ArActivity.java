package com.example.myapplication.Activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.Fragments.MyArFragment;
import com.example.myapplication.Models.MeasurePoint;
import com.google.ar.core.*;
import com.google.ar.core.exceptions.UnavailableApkTooOldException;
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException;
import com.google.ar.core.exceptions.UnavailableDeviceNotCompatibleException;
import com.google.ar.core.exceptions.UnavailableSdkTooOldException;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.*;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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
    private Anchor currentAnchor = null;
    TextView tvDistance;
    float areaBase;
    float volumen;
    Button btnCambio;
    boolean addPoint = false;

    ArrayList<MeasurePoint> points;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar);
        tvDistance = findViewById(R.id.tvDistance);
        btnCambio = findViewById(R.id.btnCambio);
        points = new ArrayList<>();
        btnCambio.setEnabled(false);
        btnCambio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        arFragment = (MyArFragment) getSupportFragmentManager().findFragmentById(R.id.my_ar_fragment);
        arFragment.getPlaneDiscoveryController().hide();
        arFragment.getArSceneView().getScene().addOnUpdateListener(this::onUpdateFrame);

        arFragment.setOnTapArPlaneListener((hitResult,plane,motionEvent)->{
            Anchor anchor = hitResult.createAnchor();
            AnchorNode anchorNode = new AnchorNode(anchor);
            anchorNode.setParent(arFragment.getArSceneView().getScene());

            currentAnchor = anchor;
            currentAnchorNode = anchorNode;

            points.add(new MeasurePoint(currentAnchorNode, currentAnchor, 0f));

            TransformableNode node = new TransformableNode(arFragment.getTransformationSystem());
            node.setRenderable(cubeRenderable);
            node.setParent(anchorNode);
            arFragment.getArSceneView().getScene().addOnUpdateListener(this.arFragment);
            arFragment.getArSceneView().getScene().addChild(anchorNode);
            node.select();

            addPoint = true;
            if(points.size() == 1){
                tvDistance.setText("Seleccione la siguiente arista.");
            }
            if(points.size() == 2){
                tvDistance.setText("Seleccione la tercera arista.");
            }
            if(points.size() >= 2) {
                Anchor a1 = points.get(points.size() - 1).getAnchor();
                Anchor a2 = points.get(points.size() - 2).getAnchor();
                addLineBetweenHits(a1, a2, plane, motionEvent);
            }
            if(points.size() == 3){
                tvDistance.setText("Volte el objeto para medir su altura");
                btnCambio.setEnabled(true);
            }
        });

    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void onUpdateFrame(FrameTime frameTime) {
        Frame frame = arFragment.getArSceneView().getArFrame();

        if (currentAnchorNode != null && addPoint && points.size() >= 2) {
            Anchor a1 = points.get(points.size() - 1).getAnchor();
            Anchor a2 = points.get(points.size() - 2).getAnchor();
            Pose objectPose = a1.getPose();
            Pose objectPose2 = a2.getPose();

            float dx = objectPose.tx() - objectPose2.tx();
            float dy = objectPose.ty() - objectPose2.ty();
            float dz = objectPose.tz() - objectPose2.tz();

            ///Compute the straight-line distance.
            float distanceMeters = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
            points.get(points.size() - 1).setDistanceToLastPoint(distanceMeters);
            if(points.size()==2){
                areaBase = distanceMeters;
            }
            if(points.size()==3){
                areaBase = areaBase*distanceMeters;
            }
            if(points.size()==5){
                volumen = areaBase*distanceMeters;
                tvDistance.setText("Volumen de la figura:"+ volumen+ "m3");
            }
            //tvDistance.setText("Distance from camera: " + distanceMeters + " metres");

            addPoint = false;
            /*float[] distance_vector = currentAnchor.getPose().inverse()
                    .compose(cameraPose).getTranslation();
            float totalDistanceSquared = 0;
            for (int i = 0; i < 3; ++i)
                totalDistanceSquared += distance_vector[i] * distance_vector[i];*/
        }
    }

    private void addLineBetweenHits(Anchor anchor1, Anchor anchor2, Plane plane, MotionEvent motionEvent) {

        int val = motionEvent.getActionMasked();
        float axisVal = motionEvent.getAxisValue(MotionEvent.AXIS_X, motionEvent.getPointerId(motionEvent.getPointerCount() - 1));
        Log.e("Values:", String.valueOf(val) + String.valueOf(axisVal));
        AnchorNode anchorNode = new AnchorNode(anchor1);
        AnchorNode anchorNode2 = new AnchorNode(anchor2);

        anchorNode.setParent(arFragment.getArSceneView().getScene());

        Vector3 point1, point2;
        point1 = anchorNode.getWorldPosition();
        point2 = anchorNode2.getWorldPosition();

        final Vector3 difference = Vector3.subtract(point1, point2);
        final Vector3 directionFromTopToBottom = difference.normalized();
        final Quaternion rotationFromAToB =
                Quaternion.lookRotation(directionFromTopToBottom, Vector3.up());
        MaterialFactory.makeOpaqueWithColor(getApplicationContext(), new Color(0, 255, 244))
                .thenAccept(
                        material -> {
                        /* Then, create a rectangular prism, using ShapeFactory.makeCube() and use the difference vector
                               to extend to the necessary length.  */
                            ModelRenderable model = ShapeFactory.makeCube(
                                    new Vector3(.01f, .01f, difference.length()),
                                    Vector3.zero(), material);
                        /* Last, set the world rotation of the node to the rotation calculated earlier and set the world position to
                               the midpoint between the given points . */
                            Node node = new Node();
                            node.setParent(anchorNode);
                            node.setRenderable(model);
                            node.setWorldPosition(Vector3.add(point1, point2).scaled(.5f));
                            node.setWorldRotation(rotationFromAToB);
                        }
                );
    }

}
