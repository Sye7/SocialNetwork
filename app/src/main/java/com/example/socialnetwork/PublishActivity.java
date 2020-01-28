package com.example.socialnetwork;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class PublishActivity extends AppCompatActivity {

    ImageView ivPhoto;
    EditText status;
    public  Bitmap pictureBitmap;
    OutputStream fOut;
    File file;
    private int photoSize;
    ImageView imageShareMock;
    Button btn_publish;

    // Delete The Image
    @Override
    public void onBackPressed() {

        file.delete();
        getApplicationContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                Uri.fromFile(file)));

        System.out.println("yasir" +" "+file.getName() + " "+file.getAbsolutePath());
        counter--;
        finish();


    }

    public void publish (View v)
    {
        Toast.makeText(this, "Photo Uploaded Successfully", Toast.LENGTH_SHORT).show();
        // Save image locally

        try {
            fOut = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        new Thread(new Runnable() {
            @Override
            public void run() {
                pictureBitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut); // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
                try {
                    fOut.flush();
                    fOut.close(); // do not forget to close the stream
                    MediaStore.Images.Media.insertImage(getContentResolver(),file.getAbsolutePath(),file.getName(),file.getName());

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(PublishActivity.this, ""+ e.getMessage(), Toast.LENGTH_SHORT).show();
                }


            }
        }).start();

    }

    String path;
    static int counter=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);

        Intent intent = getIntent();
        path = intent.getStringExtra("path");

        ivPhoto = findViewById(R.id.ivPhoto);
        imageShareMock = findViewById(R.id.iv_preview);
        btn_publish = findViewById(R.id.btn_publish);
        photoSize = getResources().getDimensionPixelSize(R.dimen.publish_photo_thumbnail_size);



        // path = Environment.getExternalStorageDirectory().toString();
         //fOut = null;
         //file = new File(path, "Insta"+counter+".jpg"); // the File to save , append increasing numeric counter to prevent files from getting overwritten.
         //counter++;

       // pictureBitmap = Bitmap.createBitmap(BitmapFactory.decodeByteArray(CameraXImpl.pic,0,CameraXImpl.pic.length));
        pictureBitmap = BitmapFactory.decodeFile(path);

        loadThumbnailPhoto();



        ivPhoto.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                if(flag_preview){
                    imageShareMock.setImageBitmap(pictureBitmap);
                    flag_preview = false;
                }
                else
                    Toast.makeText(PublishActivity.this, "Tap on FourSquare", Toast.LENGTH_SHORT).show();

                return true;
            }
        });

        imageShareMock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!flag_preview)
                {
                    imageShareMock.setImageResource(R.drawable.img_share_mock);
                    flag_preview = true;
                }
            }
        });


    }

    boolean flag_preview = true;

// Must

    private void loadThumbnailPhoto() {
        ivPhoto.setScaleX(0);
        ivPhoto.setScaleY(0);

        ivPhoto.animate()
                .scaleX(1.f).scaleY(1.f)
                .setInterpolator(new OvershootInterpolator())
                .setDuration(500)
                .setStartDelay(300)
                .start();

        ivPhoto.setImageBitmap(pictureBitmap);

/*
       Picasso.get()
                .load(file)
                .centerCrop()
                .resize(photoSize, photoSize)
                .into(ivPhoto, new Callback() {
                    @Override
                    public void onSuccess() {
                        ivPhoto.animate()
                                .scaleX(1.f).scaleY(1.f)
                                .setInterpolator(new OvershootInterpolator())
                                .setDuration(400)
                                .setStartDelay(200)
                                .start();
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });



 */






    }
}
