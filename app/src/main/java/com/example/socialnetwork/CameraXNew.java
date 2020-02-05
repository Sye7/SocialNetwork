package com.example.socialnetwork;


import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Rational;
import android.util.Size;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraInfoUnavailableException;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureConfig;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GestureDetectorCompat;
import androidx.lifecycle.LifecycleOwner;

import com.example.socialnetwork.swipe_listener.OnSwipeListener;

import java.io.File;
import java.io.IOException;

public class CameraXNew extends AppCompatActivity implements  View.OnTouchListener {

    private int REQUEST_CODE_PERMISSIONS = 101;
    private String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA",
            "android.permission.WRITE_EXTERNAL_STORAGE"};
    TextureView textureView;
    ConstraintLayout layout;
    final int RC_PHOTO_PICKER = 1;
    Uri selectedImageUri;

    static Bitmap bitmap;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {


        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {

            selectedImageUri = data.getData();

            try {
                 bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Intent inte = new Intent(getApplicationContext(), PublishActivity.class);
            inte.putExtra("path",  "bitmap");
            startActivity(inte);


        }


    }

    public void EditDp(View view)
    {
        // TODO: Fire an intent to show an image picker
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/jpeg");
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY,true);
        startActivityForResult(Intent.createChooser(intent,"Complete Action Using"),RC_PHOTO_PICKER);

    }



    private OnSwipeListener generateSwipeListenerForStory() {

        OnSwipeListener onSwipeListener = new OnSwipeListener() {

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

                // Grab two events located on the plane at e1=(x1, y1) and e2=(x2, y2)
                // Let e1 be the initial event
                // e2 can be located at 4 different positions, consider the following diagram
                // (Assume that lines are separated by 90 degrees.)
                //
                //
                //         \ A  /
                //          \  /
                //       D   e1   B
                //          /  \
                //         / C  \
                //
                // So if (x2,y2) falls in region:
                //  A => it's an UP swipe
                //  B => it's a RIGHT swipe
                //  C => it's a DOWN swipe
                //  D => it's a LEFT swipe
                //

                float x1 = e1.getX();
                float y1 = e1.getY();

                float x2 = e2.getX();
                float y2 = e2.getY();

                Direction direction = getDirection(x1,y1,x2,y2);
                return onSwipe(direction);
            }



            @Override
            public boolean onSwipe(Direction direction) {

                // Possible implementation


                 if(direction == OnSwipeListener.Direction.left ) {

                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    Bundle bndlAnimation = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.slide_left_anim, R.anim.slide_right_anim).toBundle();
                    startActivity(intent, bndlAnimation);
                    finish();
                    return true;
                }

               else if(direction == OnSwipeListener.Direction.up ) {
                     switchCamera();
                    return true;
                }




                return super.onSwipe(direction);
            }
        };

        return onSwipeListener;

    }



    GestureDetectorCompat  gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        setContentView(R.layout.activity_camera_xnew);


        textureView = findViewById(R.id.view_finder);
        layout = findViewById(R.id.cameraBg);

        OnSwipeListener onSwipeListenerUpDown = generateSwipeListenerForStory();
        gestureDetector = new GestureDetectorCompat(this, onSwipeListenerUpDown);
        layout.setOnTouchListener(this);



        if(allPermissionGranted()){

            startCamera();
        }
        else{

            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }
    }

    public void recordVideo(){

    }
    @SuppressLint("RestrictedApi")
    public void switchCamera(){

        lensFacing = lensFacing == CameraX.LensFacing.FRONT ? CameraX.LensFacing.BACK : CameraX.LensFacing.FRONT;
        try {
            // Only bind use cases if we can query a camera with this orientation
            CameraX.getCameraWithLensFacing(lensFacing);
            startCamera();
        } catch (CameraInfoUnavailableException e) {
            // Do nothing
        }
    }
    private CameraX.LensFacing lensFacing = CameraX.LensFacing.BACK;


    private void startCamera() {


        CameraX.unbindAll();


        Rational aspectRatio = new Rational(textureView.getWidth(), textureView.getHeight());
        Size screen = new Size(textureView.getWidth(), textureView.getHeight());

        PreviewConfig pConfig = new PreviewConfig.Builder().setLensFacing(lensFacing).setTargetAspectRatio(aspectRatio).setTargetResolution(screen).build();
        Preview preview = new Preview(pConfig);

        preview.setOnPreviewOutputUpdateListener(
                new Preview.OnPreviewOutputUpdateListener() {
                    @Override
                    public void onUpdated(Preview.PreviewOutput output) {
                        ViewGroup parent = (ViewGroup) textureView.getParent();
                        parent.removeView(textureView);
                        parent.addView(textureView, 0);

                        textureView.setSurfaceTexture(output.getSurfaceTexture());
                        updateTransform();
                    }
                });

        ImageCaptureConfig imageCaptureConfig = new ImageCaptureConfig.Builder().setCaptureMode(ImageCapture.CaptureMode.MIN_LATENCY).setLensFacing(lensFacing)
                .setTargetRotation(getWindowManager().getDefaultDisplay().getRotation()).build();
        final ImageCapture imgCap = new ImageCapture(imageCaptureConfig);



        findViewById(R.id.imgCapture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                File file = new File( Environment.getExternalStorageDirectory().getPath()+"/DCIM",/* "/sdcard/photos/DCIM/Camera/CameraX_",*/ System.currentTimeMillis() + ".jpg"   );
                imgCap.takePicture(file, new ImageCapture.OnImageSavedListener() {
                    @Override
                    public void onImageSaved(@NonNull File file) {
                        String path = file.getAbsolutePath();

                        Intent intent = new Intent(getApplicationContext(), PublishActivity.class);
                        intent.putExtra("path",path);
                        startActivity(intent);

                        Toast.makeText(getBaseContext(), path,Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(@NonNull ImageCapture.UseCaseError useCaseError, @NonNull String message, @Nullable Throwable cause) {
                        String msg = "Pic capture failed : " + message;
                        Toast.makeText(getBaseContext(), msg,Toast.LENGTH_LONG).show();
                        if(cause != null){
                            cause.printStackTrace();
                        }
                    }
                });
            }
        });

        CameraX.bindToLifecycle((LifecycleOwner)this, preview, imgCap);
    }



    private void updateTransform(){

        Matrix mx = new Matrix();
        float w = textureView.getMeasuredWidth();
        float h = textureView.getMeasuredHeight();

        float cX = w / 2f;
        float cY = h / 2f;

        int rotationDgr;
        int rotation = (int)textureView.getRotation();

        switch(rotation){
            case Surface.ROTATION_0:
                rotationDgr = 0;
                break;
            case Surface.ROTATION_90:
                rotationDgr = 90;
                break;
            case Surface.ROTATION_180:
                rotationDgr = 180;
                break;
            case Surface.ROTATION_270:
                rotationDgr = 270;
                break;
            default:
                return;
        }

        mx.postRotate((float)rotationDgr, cX, cY);
        textureView.setTransform(mx);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == REQUEST_CODE_PERMISSIONS){
            if(allPermissionGranted()){
                startCamera();
            } else{
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private boolean allPermissionGranted() {

        for(String permission : REQUIRED_PERMISSIONS){

            if(ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED){

                return false;
            }
        }
        return true;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return true;
    }
}