package com.example.myapplication.Activities;

import android.graphics.Bitmap;
import android.net.Uri;
import android.opengl.GLES20;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.PixelCopy;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.ExampleDialog;
import com.example.myapplication.Fragments.MyArFragment;
import com.example.myapplication.Models.Cotization;
import com.example.myapplication.Models.MeasurePoint;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.ar.core.Anchor;
import com.google.ar.core.Frame;
import com.google.ar.core.Plane;
import com.google.ar.core.Pose;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.MaterialFactory;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ShapeFactory;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.IntBuffer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;


public class ArActivity  extends AppCompatActivity implements ExampleDialog.ExampleDialogListener{
    private ArFragment arFragment;
    private AnchorNode currentAnchorNode;
    private ModelRenderable cubeRenderable;
    private Anchor currentAnchor = null;
    private TextView tvDistance;
    private float areaBase;
    private float volumen;
    private Button btnCambio;
    private boolean addPoint = false;
    private int mWidth;
    private int mHeight;
    private ArrayList<MeasurePoint> points;
    private final String[] uris = new String[1];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar);
        tvDistance = findViewById(R.id.tvDistance);
        btnCambio = findViewById(R.id.btnCambio);
        points = new ArrayList<>();
        btnCambio.setEnabled(false);
        btnCambio.setOnClickListener(new View.OnClickListener() {//listener del boton
            @Override
            public void onClick(View view) {
                if(points.size()>=5) {//si hay un cubo hecho reiniciamos la actividad
                    Log.d("FIREBASE","PUS SI");

                    DisplayMetrics d = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(d);
                    mWidth = d.widthPixels;
                    mHeight = d.heightPixels;
                    GLES20.glViewport(0, 0, mWidth, mHeight);
                    try {
                        SavePicture();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //recreate();
                }else{
                    clearAnchor();
                }

            }
        });


        arFragment = (MyArFragment) getSupportFragmentManager().findFragmentById(R.id.my_ar_fragment);
        arFragment.getPlaneDiscoveryController().hide();
        arFragment.getPlaneDiscoveryController().setInstructionView(null);
        arFragment.getArSceneView().getScene().addOnUpdateListener(this::onUpdateFrame);// añadimos el listener para las escenas

        arFragment.setOnTapArPlaneListener((hitResult,plane,motionEvent)->{// añadimos el listener para el touch en la pantalla
            //Un anchor es lo que dijo el jona y ya no me acuerdo
            Anchor anchor = hitResult.createAnchor();// guardamos el anchor del punto qeu se toco
            AnchorNode anchorNode = new AnchorNode(anchor);
            anchorNode.setParent(arFragment.getArSceneView().getScene());

            currentAnchor = anchor;
            currentAnchorNode = anchorNode;

            points.add(new MeasurePoint(currentAnchorNode, currentAnchor, 0f));//añadimos a las lista de puntos

            TransformableNode node = new TransformableNode(arFragment.getTransformationSystem());
            node.setRenderable(cubeRenderable);
            node.setParent(anchorNode);
            arFragment.getArSceneView().getScene().addOnUpdateListener(this.arFragment);
            arFragment.getArSceneView().getScene().addChild(anchorNode);
            node.select();

            addPoint = true;
            if(points.size() == 1)// si solo tenemos solo un punto
                tvDistance.setText("Seleccione la siguiente arista.");//cambiamos la intrucción
            if(points.size() == 2)// si tenemos dos puntos
                tvDistance.setText("Seleccione la tercera arista.");//cambiamos la instrucción
            if(points.size() >= 2 && points.size() < 4) {// si tenemos entre do o 3 puntos
                Anchor a1 = points.get(points.size() - 1).getAnchor();
                Anchor a2 = points.get(points.size() - 2).getAnchor();
                addLineBetweenHits(a1, a2, plane, motionEvent);//dibujamos la lines
            }
            if(points.size() == 3){// si tenemos tres puntos
                tvDistance.setText("Volte el objeto para medir su altura");//cambiamos la instrucción
                btnCambio.setEnabled(true);//
            }
        });
//        FirebaseAuth mAuth = FirebaseAuth.getInstance();
//        mAuth.signInAnonymously()
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            // Sign in success, update UI with the signed-in user's information
//                            Log.d("Firebase Auth", "signInAnonymously:success");
//                            FirebaseUser user = mAuth.getCurrentUser();
//                        } else {
//                            // If sign in fails, display a message to the user.
//                            Log.w("Firebase Auth", "signInAnonymously:failure", task.getException());
//                            Toast.makeText(ArActivity.this, "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
//                        }
//
//                        // ...
//                    }
//                });

    }

    /**
     * limpia la pantalla
     */
    private void clearAnchor() {
        for(int i = 0;i<points.size();i++) {//recorremos la lista de points y nulleamos todo
            Anchor currentAncho = points.get(i).getAnchor();
            AnchorNode currentAnchorNod = points.get(i).getAnchorNode();
            currentAncho = null;
            if (currentAnchorNod != null) {
                arFragment.getArSceneView().getScene().removeChild(currentAnchorNod);
                currentAnchorNod.getAnchor().detach();
                currentAnchorNod.setParent(null);
                currentAnchorNod = null;
            }
        }

    }

    /**
     * Método que dibuja la figura
     */
    private void createCube(){
            MaterialFactory.makeTransparentWithColor(getApplicationContext(), new Color(0f, 157f, 164f,0.8f))
                .thenAccept(
                        material -> {

                            Vector3 vector3 = new Vector3((float)points.get(1).getDistanceToLastPoint(),
                                    (float)points.get(4).getDistanceToLastPoint(),
                                    (float)points.get(2).getDistanceToLastPoint());
                            ModelRenderable model = ShapeFactory.makeCube(vector3,
                                    Vector3.zero(), material);
                            model.setShadowCaster(false);
                            model.setShadowReceiver(false);

                            TransformableNode transformableNode = new TransformableNode(arFragment.getTransformationSystem());
                            transformableNode.setParent(currentAnchorNode);
                            transformableNode.setRenderable(model);
                            transformableNode.select();
                        });
    }

    //metodo llamada en cada actualizacion del frame
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void onUpdateFrame(FrameTime frameTime) {//
        Frame frame = arFragment.getArSceneView().getArFrame();//Tomamos la escena
        //entramos solo si tenemos un anchor seleccionado YY si  ya hay dos puntosen nuetra lista de puntos
        if (currentAnchorNode != null && addPoint && points.size() >= 2) {
            Anchor a1 = points.get(points.size() - 1).getAnchor();//Agarmmos los ultimos dos puntos
            Anchor a2 = points.get(points.size() - 2).getAnchor();
            Pose objectPose = a1.getPose();
            Pose objectPose2 = a2.getPose();
            //tomamos sus coordenadas
            float dx = objectPose.tx() - objectPose2.tx();//calculamos las coorednadas en tre os puntos
            float dy = objectPose.ty() - objectPose2.ty();
            float dz = objectPose.tz() - objectPose2.tz();
            //Calculamos la distancia en metros
            float distanceMeters = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
            points.get(points.size() - 1).setDistanceToLastPoint(distanceMeters);// la guardamos en nuestra lista
            if(points.size()==2){// si ya tenemos dos puntos guardamos el primer lado de la base
                areaBase = distanceMeters;
            }
            if(points.size()==3){// si ya tenemos tres calculamos el area de la base
                areaBase = areaBase*distanceMeters;
            }
            if(points.size()==5){// si ya tenemos cinco calulamos el volumen
                volumen = areaBase*distanceMeters;//tenemos el volumen en metreos cubicos
                double parteEntera, resultado;
                String medida = "l";
                resultado = volumen*1000; //conventimos a litros
                if(resultado<0.4){//si el resultado es muy peque;a convertimos a mililitros
                    resultado = resultado*1000;
                    medida = "ml";
                }
                parteEntera = Math.floor(resultado);//redondeamos a 4 puntos decimales
                resultado=(resultado-parteEntera)*Math.pow(10, 4);
                resultado=Math.round(resultado);
                resultado=(resultado/Math.pow(10, 4))+parteEntera;
                //String res = new BigDecimal(resultado).toPlainString();
                tvDistance.setText("Volumen de la figura: "+ resultado+" "+medida);//imprimimos el volumen calculado
                createCube();//llamamos a pintar la figura
                btnCambio.setText("Guardar");
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
    public void SavePicture() throws IOException {
        // Create a bitmap.
        ArSceneView view = arFragment.getArSceneView();
        Bitmap bmp = Bitmap.createBitmap(view.getWidth(),view.getHeight(), Bitmap.Config.ARGB_8888);

        final HandlerThread ht = new HandlerThread("PixelCopier");
        ht.start();
        PixelCopy.request(view, bmp, (res) -> {
            if(res == PixelCopy.SUCCESS){
                // Write it to disk.

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();


                FirebaseStorage storage = FirebaseStorage.getInstance();
                // Create a storage reference from our app
                StorageReference storageRef = storage.getReference();

                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                LocalDateTime now = LocalDateTime.now();
                String date = dtf.format(now);
                // Create a reference to "mountains.jpg"
                StorageReference mountainsRef = storageRef.child("image-"+date+".jpg");

                // Create a reference to 'images/mountains.jpg'
                StorageReference mountainImagesRef = storageRef.child("images/image-"+date+".jpg");

                // While the file names are the same, the references point to different files
                mountainsRef.getName().equals(mountainImagesRef.getName());    // true
                mountainsRef.getPath().equals(mountainImagesRef.getPath());    // false

                UploadTask uploadTask = mountainsRef.putBytes(data);
                uploadTask.addOnFailureListener(new OnFailureListener(){
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        Log.d("FIREBASEURL=========================>","PUS FALLO");
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                        mountainsRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                uris[0] = uri.toString();
                                Log.d("FIREBASEURL=========================>","" + uris[0]);
                                openDialog();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("FIREBASEURL=========================>",e.getMessage());
                            }
                        });
                    }
                });
            }else {
                Log.d("DrawAr", "Failed to copy pixels");
            }
            ht.quitSafely();
        }, new Handler(ht.getLooper()));
    }
    public void openDialog() {
        ExampleDialog exampleDialog = new ExampleDialog();
        exampleDialog.show(getSupportFragmentManager(), "example dialog");
    }

    @Override
    public void applyTexts(String username, String password) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Cotization objData = new Cotization("",username,
                Double.parseDouble(password)*volumen,""+volumen,uris[0]);
        db.collection("cotization").add(objData).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                recreate();
            }
        });

    }

    /**
     * Método que nos dibuja las lineas entre puntos.
     * @param anchor1 primer punto
     * @param anchor2 segundo punto
     * @param plane
     * @param motionEvent
     */
    private void addLineBetweenHits(Anchor anchor1, Anchor anchor2, Plane plane, MotionEvent motionEvent) {

        int val = motionEvent.getActionMasked();
        float axisVal = motionEvent.getAxisValue(MotionEvent.AXIS_X, motionEvent.getPointerId(motionEvent.getPointerCount() - 1));
        Log.e("Values:", String.valueOf(val) + axisVal);
        AnchorNode anchorNode = new AnchorNode(anchor1);
        AnchorNode anchorNode2 = new AnchorNode(anchor2);

        anchorNode.setParent(arFragment.getArSceneView().getScene());
        // optenemos las coordenadas de los puntos
        Vector3 point1, point2;
        point1 = anchorNode.getWorldPosition();
        point2 = anchorNode2.getWorldPosition();

        final Vector3 difference = Vector3.subtract(point1, point2);
        final Vector3 directionFromTopToBottom = difference.normalized();
        final Quaternion rotationFromAToB =
                Quaternion.lookRotation(directionFromTopToBottom, Vector3.up());
        MaterialFactory.makeOpaqueWithColor(getApplicationContext(), new Color(0, 255, 244))// método para generar las lineas
                .thenAccept(
                        material -> {
                            ModelRenderable model = ShapeFactory.makeCube(
                                    new Vector3(.01f, .01f, difference.length()),
                                    Vector3.zero(), material);
                            Node node = new Node();
                            node.setParent(anchorNode);
                            node.setRenderable(model);
                            node.setWorldPosition(Vector3.add(point1, point2).scaled(.5f));
                            node.setWorldRotation(rotationFromAToB);
                        }
                );
    }

}
