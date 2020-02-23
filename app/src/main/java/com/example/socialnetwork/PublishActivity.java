package com.example.socialnetwork;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.socialnetwork.fragment.CameraActivityFragment;
import com.example.socialnetwork.model.Post;
import com.example.socialnetwork.model.Profile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
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

    String id;
    String photo;
    String caption;
    int likes;
    String dp;
    String userName;

    private void uploadImage(Bitmap bitmap) {


        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, baos);

        byte[] data = baos.toByteArray();
        final UploadTask uploadTask = (UploadTask) mChatPhotoStorageRef.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Task<Uri> downloadUri =taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {

                        String generatedFilePath = task.getResult().toString();
                        System.out.println("yasir "+generatedFilePath);
                        photo = generatedFilePath;

                        uploadPost();

                    }
                });
            }
        });

    }

    public void uploadPost(){

        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Profile");

        //  /Get a reference to store file at image_photos/<FileName>
        final StorageReference photoRef = mChatPhotoStorageRef;

        // Setting Changes while editing profile
        final Query query = ref.orderByChild("id").equalTo(FirebaseAuth.getInstance().getUid());

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Profile profile = snapshot.getValue(Profile.class);


                    if (profile.getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {


                        // If already present
                        id = (profile.getId());
                        dp = profile.getDp();
                        userName = profile.getUserName();

                        Post post = new Post(id, photo, caption, likes, dp, userName);

                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Post");
                        databaseReference.push().setValue(post).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                startActivity(new Intent(getApplicationContext(), MainTabActivity.class));
                                finish();
                            }
                        });




                    }

                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }
    public void publish (View v)
    {
        likes =0;
        caption = status.getText().toString();

        uploadImage(pictureBitmap);

        btn_publish.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_out_send));
        btn_publish.setVisibility(View.GONE);


        // Save image locally

      /*  try {
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


       */

    }

    String path;
    static int counter=0;

    private StorageReference mChatPhotoStorageRef;
    private FirebaseStorage mFirebaseStorage;

    public Bitmap fixOrientation( Bitmap mBitmap) {
        if (mBitmap.getWidth() > mBitmap.getHeight()) {
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            mBitmap = Bitmap.createBitmap(mBitmap , 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), matrix, true);
        }
        return mBitmap;
    }

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
        status = findViewById(R.id.etDescription);

        mFirebaseStorage = FirebaseStorage.getInstance();
        mChatPhotoStorageRef = mFirebaseStorage.getReference().child("post/").child(Math.random()+"");



        // path = Environment.getExternalStorageDirectory().toString();
         //fOut = null;
         //file = new File(path, "Insta"+counter+".jpg"); // the File to save , append increasing numeric counter to prevent files from getting overwritten.
         //counter++;

       // pictureBitmap = Bitmap.createBitmap(BitmapFactory.decodeByteArray(CameraXImpl.pic,0,CameraXImpl.pic.length));
       if(path.equals("bitmap"))
            pictureBitmap = CameraActivityFragment.bitmap;
        else
            pictureBitmap = BitmapFactory.decodeFile(path);

        new Thread(new Runnable() {
            @Override
            public void run() {
                pictureBitmap =  fixOrientation(pictureBitmap);

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
