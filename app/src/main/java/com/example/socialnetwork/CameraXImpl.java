package com.example.socialnetwork;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

import com.camerakit.CameraKit;
import com.camerakit.CameraKitView;
import com.camerakit.type.CameraSize;


public class CameraXImpl extends AppCompatActivity {


    private CameraKitView cameraView;

    private AppCompatTextView facingText;
    private AppCompatTextView flashText;
    private AppCompatTextView previewSizeText;
    private AppCompatTextView photoSizeText;

    private ImageButton flashOnButton;
    private ImageButton flashOffButton;
    ImageButton photoButton;


    private ImageButton facingFrontButton;
    private ImageButton facingBackButton;

    private ImageButton videoRecord;

    private Button permissionsButton;
    static  byte[] pic=null;
    public static final String ARG_REVEAL_START_LOCATION = "reveal_start_location";


    public static void startUserProfileFromLocation(int[] startingLocation, Activity startingActivity) {
        Intent intent = new Intent(startingActivity, CameraXImpl.class);
        intent.putExtra(ARG_REVEAL_START_LOCATION, startingLocation);
        startingActivity.startActivity(intent);
    }

    public void onPhotoClick(View view)
    {
        cameraView.captureImage(new CameraKitView.ImageCallback() {
            @Override
            public void onImage(CameraKitView view, final byte[] photo) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        // Store in external drive
                        Intent intent = new Intent(getApplicationContext(), PublishActivity.class);
                        pic = photo;

                            startActivity(intent);

                    }
                }).start();
            }
        });
    }

    boolean videoFlag=true;

    public void videoRecording(View v){


        if(videoFlag)
        {
            videoRecord.setImageResource(R.drawable.stop_video);
            videoFlag  = false;

        }
        else
        {
            videoRecord.setImageResource(R.drawable.ic_video);
            videoFlag  = true;
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_x);

        cameraView = findViewById(R.id.camera);


        facingText = findViewById(R.id.facingText);
        flashText = findViewById(R.id.flashText);
        previewSizeText = findViewById(R.id.previewSizeText);
        photoSizeText = findViewById(R.id.photoSizeText);

        photoButton = findViewById(R.id.photoButton);
      //  photoButton.setOnClickListener(photoOnClickListener);

        flashOnButton = findViewById(R.id.flashOnButton);
        flashOffButton = findViewById(R.id.flashOffButton);
        videoRecord = findViewById(R.id.ib_video);

        facingFrontButton = findViewById(R.id.facingFrontButton);
        facingBackButton = findViewById(R.id.facingBackButton);



        permissionsButton = findViewById(R.id.permissionsButton);
        permissionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraView.requestPermissions(CameraXImpl.this);
            }
        });

        // circular anim





        flashOnButton.setTranslationY(2 * getResources().getDimensionPixelOffset(R.dimen.btn_fab_size));
        flashOffButton.setTranslationY(2 * getResources().getDimensionPixelOffset(R.dimen.btn_fab_size));
        facingFrontButton.setTranslationY(2 * getResources().getDimensionPixelOffset(R.dimen.btn_fab_size));
        facingBackButton.setTranslationY(2 * getResources().getDimensionPixelOffset(R.dimen.btn_fab_size));
        photoButton.setTranslationY(2 * getResources().getDimensionPixelOffset(R.dimen.btn_fab_size));


        startContentAnimation1();
        startContentAnimation2();
        startContentAnimation3();
        startContentAnimation4();
        startContentAnimation5();

        cameraView.setPermissionsListener(new CameraKitView.PermissionsListener() {
            @Override
            public void onPermissionsSuccess() {
                permissionsButton.setVisibility(View.GONE);
            }

            @Override
            public void onPermissionsFailure() {
                permissionsButton.setVisibility(View.VISIBLE);
            }
        });

        cameraView.setCameraListener(new CameraKitView.CameraListener() {
            @Override
            public void onOpened() {
                Log.v("CameraKitView", "CameraListener: onOpened()");
            }

            @Override
            public void onClosed() {
                Log.v("CameraKitView", "CameraListener: onClosed()");
            }
        });

        cameraView.setPreviewListener(new CameraKitView.PreviewListener() {
            @Override
            public void onStart() {
                Log.v("CameraKitView", "PreviewListener: onStart()");
                updateInfoText();
            }

            @Override
            public void onStop() {
                Log.v("CameraKitView", "PreviewListener: onStop()");
            }
        });



    }


    private static final int ANIM_DURATION_FAB = 400;

    private void startContentAnimation1() {
        flashOffButton.animate()
                .translationY(1)
                .setInterpolator(new OvershootInterpolator(1.f))
                .setStartDelay(1000)
                .setDuration(ANIM_DURATION_FAB)
                .start();
    }

    private void startContentAnimation2() {
        flashOnButton.animate()
                .translationY(0)
                .setInterpolator(new OvershootInterpolator(1.f))
                .setStartDelay(1000)
                .setDuration(ANIM_DURATION_FAB)
                .start();
    }

    private void startContentAnimation3() {
        photoButton.animate()
                .translationY(0)
                .setInterpolator(new OvershootInterpolator(1.f))
                .setStartDelay(1000)
                .setDuration(ANIM_DURATION_FAB)
                .start();
    }

    private void startContentAnimation4() {
        facingBackButton.animate()
                .translationY(0)
                .setInterpolator(new OvershootInterpolator(1.f))
                .setStartDelay(1000)
                .setDuration(ANIM_DURATION_FAB)
                .start();
    }

    private void startContentAnimation5() {
        facingFrontButton.animate()
                .translationY(0)
                .setInterpolator(new OvershootInterpolator(1.f))
                .setStartDelay(1000)
                .setDuration(ANIM_DURATION_FAB)
                .start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        cameraView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        cameraView.onResume();
    }

    @Override
    public void onPause() {
        cameraView.onPause();
        super.onPause();
    }

    @Override
    protected void onStop() {
        cameraView.onStop();
        super.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        cameraView.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }



    private View.OnClickListener photoOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    public void flashOn(View v)
    {
        if (cameraView.getFlash() != CameraKit.FLASH_ON) {
            cameraView.setFlash(CameraKit.FLASH_ON);
            updateInfoText();
        }
    }


    public void flashOff(View v)
    {
        if (cameraView.getFlash() != CameraKit.FLASH_OFF) {
            cameraView.setFlash(CameraKit.FLASH_OFF);
            updateInfoText();
        }
    }

    public void frontCamera(View v)
    {
        cameraView.setFacing(CameraKit.FACING_FRONT);
    }

    public void backCamera(View v)
    {
        cameraView.setFacing(CameraKit.FACING_BACK);
    }



    private void updateInfoText() {
        String facingValue = cameraView.getFacing() == CameraKit.FACING_BACK ? "BACK" : "FRONT";
        facingText.setText(Html.fromHtml("<b>Facing:</b> " + facingValue));

        String flashValue = "OFF";
        switch (cameraView.getFlash()) {
            case CameraKit.FLASH_OFF: {
                flashValue = "OFF";
                break;
            }

            case CameraKit.FLASH_ON: {
                flashValue = "ON";
                break;
            }

            case CameraKit.FLASH_AUTO: {
                flashValue = "AUTO";
                break;
            }

            case CameraKit.FLASH_TORCH: {
                flashValue = "TORCH";
                break;
            }
        }
        flashText.setText(Html.fromHtml("<b>Flash:</b> " + flashValue));

        CameraSize previewSize = cameraView.getPreviewResolution();
        if (previewSize != null) {
            previewSizeText.setText(Html.fromHtml(String.format("<b>Preview Resolution:</b> %d x %d", previewSize.getWidth(), previewSize.getHeight())));
        }

        CameraSize photoSize = cameraView.getPhotoResolution();
        if (photoSize != null) {
            photoSizeText.setText(Html.fromHtml(String.format("<b>Photo Resolution:</b> %d x %d", photoSize.getWidth(), photoSize.getHeight())));
        }
    }

}

