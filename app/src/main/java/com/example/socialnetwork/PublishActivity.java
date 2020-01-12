package com.example.socialnetwork;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);

        ivPhoto = findViewById(R.id.ivPhoto);
        imageShareMock = findViewById(R.id.iv_preview);
        //status = findViewById(R.id.etStatus);
        photoSize = getResources().getDimensionPixelSize(R.dimen.publish_photo_thumbnail_size);



        String path = Environment.getExternalStorageDirectory().toString();
         fOut = null;
        Integer counter = 0;
         file = new File(path, "Insta"+counter+".jpg"); // the File to save , append increasing numeric counter to prevent files from getting overwritten.
        try {
            fOut = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

         pictureBitmap = Bitmap.createBitmap(BitmapFactory.decodeByteArray(CameraXImpl.pic,0,CameraXImpl.pic.length));
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

    boolean flag_preview = false;

// Must

    private void loadThumbnailPhoto() {
        ivPhoto.setScaleX(0);
        ivPhoto.setScaleY(0);
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
    }
}
