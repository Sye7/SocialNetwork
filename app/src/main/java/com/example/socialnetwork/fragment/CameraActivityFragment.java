package com.example.socialnetwork.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
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
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;

import com.example.socialnetwork.PublishActivity;
import com.example.socialnetwork.R;
import com.example.socialnetwork.swipe_listener.OnSwipeListener;

import java.io.File;
import java.io.IOException;

public class CameraActivityFragment extends Fragment implements View.OnTouchListener {

    private int REQUEST_CODE_PERMISSIONS = 101;
    private String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA",
            "android.permission.WRITE_EXTERNAL_STORAGE"};
    TextureView textureView;
    ConstraintLayout layout;
    final int RC_PHOTO_PICKER = 1;
    Uri selectedImageUri;

    public static Bitmap bitmap;
    Context thisContext;
    View fragView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_camera_xnew, container, false);
        fragView = view;

        thisContext = inflater.getContext();
        textureView = view.findViewById(R.id.view_finder);
        layout = view.findViewById(R.id.cameraBg);

        OnSwipeListener onSwipeListenerUpDown = generateSwipeListenerForStory();
        gestureDetector = new GestureDetectorCompat(thisContext, onSwipeListenerUpDown);
        layout.setOnTouchListener(this);


        if (allPermissionGranted()) {

            startCamera();
        } else {

            ActivityCompat.requestPermissions(getActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }


        return view;
    }


    private OnSwipeListener generateSwipeListenerForStory() {

        OnSwipeListener onSwipeListener = new OnSwipeListener() {

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

                float x1 = e1.getX();
                float y1 = e1.getY();

                float x2 = e2.getX();
                float y2 = e2.getY();

                Direction direction = getDirection(x1, y1, x2, y2);
                return onSwipe(direction);
            }


            @Override
            public boolean onSwipe(Direction direction) {

                // Possible implementation


                if (direction == OnSwipeListener.Direction.up) {
                    switchCamera();
                    return true;
                }


                return super.onSwipe(direction);
            }
        };

        return onSwipeListener;

    }


    GestureDetectorCompat gestureDetector;


    @SuppressLint("RestrictedApi")
    public void switchCamera() {

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
                .setTargetRotation(getActivity().getWindowManager().getDefaultDisplay().getRotation()).build();
        final ImageCapture imgCap = new ImageCapture(imageCaptureConfig);


        fragView.findViewById(R.id.imgCapture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                File file = new File(Environment.getExternalStorageDirectory().getPath() + "/DCIM",/* "/sdcard/photos/DCIM/Camera/CameraX_",*/ System.currentTimeMillis() + ".jpg");
                imgCap.takePicture(file, new ImageCapture.OnImageSavedListener() {
                    @Override
                    public void onImageSaved(@NonNull File file) {
                        String path = file.getAbsolutePath();

                        Intent intent = new Intent(thisContext, PublishActivity.class);
                        intent.putExtra("path", path);
                        startActivity(intent);

                        Toast.makeText(getActivity().getBaseContext(), path, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(@NonNull ImageCapture.UseCaseError useCaseError, @NonNull String message, @Nullable Throwable cause) {
                        String msg = "Pic capture failed : " + message;
                        Toast.makeText(getActivity().getBaseContext(), msg, Toast.LENGTH_LONG).show();
                        if (cause != null) {
                            cause.printStackTrace();
                        }
                    }
                });
            }
        });

        CameraX.bindToLifecycle((LifecycleOwner) this, preview, imgCap);
    }


    private void updateTransform() {

        Matrix mx = new Matrix();
        float w = textureView.getMeasuredWidth();
        float h = textureView.getMeasuredHeight();

        float cX = w / 2f;
        float cY = h / 2f;

        int rotationDgr;
        int rotation = (int) textureView.getRotation();

        switch (rotation) {
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

        mx.postRotate((float) rotationDgr, cX, cY);
        textureView.setTransform(mx);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionGranted()) {
                startCamera();
            } else {
                Toast.makeText(thisContext, "Please Grant Permission.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean allPermissionGranted() {

        for (String permission : REQUIRED_PERMISSIONS) {

            if (ContextCompat.checkSelfPermission(thisContext, permission) != PackageManager.PERMISSION_GRANTED) {

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


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {


        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_PHOTO_PICKER && resultCode == getActivity().RESULT_OK) {

            selectedImageUri = data.getData();

            try {
                bitmap = MediaStore.Images.Media.getBitmap(thisContext.getContentResolver(), selectedImageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Intent inte = new Intent(thisContext, PublishActivity.class);
            inte.putExtra("path", "bitmap");
            startActivity(inte);


        }
    }

    public void EditDp(View view) {
        // TODO: Fire an intent to show an image picker
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/jpeg");
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        startActivityForResult(Intent.createChooser(intent, "Complete Action Using"), RC_PHOTO_PICKER);

    }


}
