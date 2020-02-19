package com.example.socialnetwork;


import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.socialnetwork.CircularImage.CircleTransformation;
import com.example.socialnetwork.adapter.UserProfileAdapter;
import com.example.socialnetwork.model.Post;
import com.example.socialnetwork.model.Profile;
import com.example.socialnetwork.model.UserLoginModel;
import com.example.socialnetwork.swipe_listener.OnSwipeListener;
import com.example.socialnetwork.view.RevealBackgroundView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


// Circular Reveal

public class UserProfileActivity extends AppCompatActivity implements RevealBackgroundView.OnStateChangeListener,
        View.OnTouchListener {
    public static final String ARG_REVEAL_START_LOCATION = "reveal_start_location";

    private static final int USER_OPTIONS_ANIMATION_DELAY = 300;
    private static final Interpolator INTERPOLATOR = new DecelerateInterpolator();
    final int RC_PHOTO_PICKER = 1;
    static Uri downloadUri;

    private StorageReference mChatPhotoStorageRef;
    private FirebaseStorage mFirebaseStorage;
    Uri selectedImageUri;

    RevealBackgroundView vRevealBackground;

    RecyclerView rvUserProfile;
    ArrayList<String> photosPost;
    // private ActivityMainBinding binding;;


    ImageView ivUserProfilePhoto;
    View vUserDetails;
    Button btnFollow;
    View vUserStats;
    View vUserProfileRoot;
    TextView tvUserName;
    TextView tvInstaUserName;
    TextView tvOccupation;

    TextView postCount;
    TextView followersCount;
    TextView followingCount;


    private int avatarSize;
    private String profilePhoto;
    private UserProfileAdapter userPhotosAdapter;

    public void showStory(View view) {

        startActivity(new Intent(getApplicationContext(), StoryStatus.class));
    }


    public static void startUserProfileFromLocation(int[] startingLocation, Activity startingActivity) {
        Intent intent = new Intent(startingActivity, UserProfileActivity.class);
        intent.putExtra(ARG_REVEAL_START_LOCATION, startingLocation);
        startingActivity.startActivity(intent);
    }


    public void EditDp(View view) {
        // TODO: Fire an intent to show an image picker
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/jpeg");
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        startActivityForResult(Intent.createChooser(intent, "Complete Action Using"), RC_PHOTO_PICKER);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {


        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {

            selectedImageUri = data.getData();

        }


    }

    Profile updateProfile = new Profile();

    public void setting(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Profile");
        View viewInflated = LayoutInflater.from(this).inflate(R.layout.edit_profile_dialog, (ViewGroup) findViewById(android.R.id.content), false);
        final EditText userName = (EditText) viewInflated.findViewById(R.id.et_username);
        final EditText interest = (EditText) viewInflated.findViewById(R.id.et_interest);


        builder.setView(viewInflated);

// Set up the buttons
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Profile");


                //uploadImage();


                //  /Get a reference to store file at image_photos/<FileName>
                if (selectedImageUri != null) {

                    final StorageReference photoRef = mChatPhotoStorageRef.child(selectedImageUri.getLastPathSegment());


                    //Upload file to firebase storage
                    photoRef.putFile(selectedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Toast.makeText(UserProfileActivity.this, "Image Uploaded Successfully", Toast.LENGTH_SHORT).show();
                            Task<Uri> downloadUri = taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {

                                    String generatedFilePath = task.getResult().toString();
                                    System.out.println("yasir " + generatedFilePath);
                                    profilePhoto = generatedFilePath;
                                    updatedDp();


                                    // Setting Changes while editing profile
                                    Query query = ref.orderByChild("id").equalTo(FirebaseAuth.getInstance().getUid());

                                    query.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                Profile profile = snapshot.getValue(Profile.class);


                                                if (profile.getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {


                                                    // If already present
                                                    updateProfile.setId(profile.getId());

                                                    if (profile.getFollowing() == null)
                                                        updateProfile.setFollowing(null);
                                                    else
                                                        updateProfile.setFollowing(profile.getFollowing());

                                                    if (profile.getFollowers() == null)
                                                        updateProfile.setFollowers(null);
                                                    else
                                                        updateProfile.setFollowers(profile.getFollowers());


                                                    updateProfile.setPosts(profile.getPosts());

                                                    String name = userName.getText().toString();
                                                    String intr = interest.getText().toString();

                                                    if (name.length() < 2)
                                                        name = profile.getUserName();


                                                    if (intr.length() < 3)
                                                        intr = profile.getOccupation();


                                                    updateProfile.setDp(profilePhoto);

                                                    updateProfile.setUserName(name);
                                                    updateProfile.setOccupation(intr);

                                                    snapshot.getRef().setValue(updateProfile);


                                                }

                                            }


                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });


                                }
                            });


                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                }
                            });
                } else {
                    Query query = ref.orderByChild("id").equalTo(FirebaseAuth.getInstance().getUid());

                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Profile profile = snapshot.getValue(Profile.class);


                                if (profile.getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {


                                    // If already present
                                    updateProfile.setId(profile.getId());

                                    updateProfile.setFollowers(profile.getFollowers());
                                    updateProfile.setFollowing(profile.getFollowing());
                                    updateProfile.setPosts(profile.getPosts());

                                    String name = userName.getText().toString();
                                    String intr = interest.getText().toString();

                                    if (name.length() < 2)
                                        name = profile.getUserName();


                                    if (intr.length() < 3)
                                        intr = profile.getOccupation();

                                    // changes

                                    updateProfile.setDp(profile.getDp());

                                    updateProfile.setUserName(name);
                                    updateProfile.setOccupation(intr);

                                    snapshot.getRef().setValue(updateProfile);


                                }

                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                }


            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public void updatedDp() {

        Picasso.get()
                .load(profilePhoto)
                .placeholder(R.drawable.img_circle_placeholder)
                .resize(avatarSize, avatarSize)
                .centerCrop()
                .transform(new CircleTransformation())
                .into(ivUserProfilePhoto);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return true;
    }

    GestureDetectorCompat gestureDetector;

    Context ctx;
    ScrollView scrv;
    Profile currentUserModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);


        this.avatarSize = getResources().getDimensionPixelSize(R.dimen.user_profile_avatar_size);


        vRevealBackground = (RevealBackgroundView) findViewById(R.id.vRevealBackground);
        rvUserProfile = findViewById(R.id.rvUserProfile);
        ivUserProfilePhoto = findViewById(R.id.ivUserProfilePhoto);
        vUserDetails = findViewById(R.id.vUserDetails);
        btnFollow = findViewById(R.id.btnFollow);
        vUserStats = findViewById(R.id.vUserStats);
        vUserProfileRoot = findViewById(R.id.vUserProfileRoot);
        tvUserName = findViewById(R.id.tvUserName);
        tvInstaUserName = findViewById(R.id.tv_insta_UserName);
        tvOccupation = findViewById(R.id.tvOccupation);
        ctx = this;
        photosPost = new ArrayList<>();

        postCount = findViewById(R.id.tvPostCount);
        followersCount = findViewById(R.id.tvFollowersCount);
        followingCount = findViewById(R.id.tvFollowingCount);

        rvUserProfile.setVisibility(View.VISIBLE);
        //  tlUserProfileTabs.setVisibility(View.VISIBLE);
        vUserProfileRoot.setVisibility(View.VISIBLE);


        userPhotosAdapter = new UserProfileAdapter(this, photosPost);
        animateUserProfileHeader();
        scrv = findViewById(R.id.scRv);


        fillPhotoPostList();


        mFirebaseStorage = FirebaseStorage.getInstance();
        mChatPhotoStorageRef = mFirebaseStorage.getReference().child("image_photos");

        OnSwipeListener onSwipeListenerUpDown = generateSwipeListenerForStory();
        gestureDetector = new GestureDetectorCompat(this, onSwipeListenerUpDown);
        scrv.setOnTouchListener(this);


        setupUserProfileGrid();
        setupRevealBackground(savedInstanceState);
        getData();

    }

    public void startFollowUnfollow(View view) {



        DatabaseReference otherUserProfile = FirebaseDatabase.getInstance().getReference("Profile");


        Query query = otherUserProfile.orderByChild("userName").equalTo(tvInstaUserName.getText().toString());

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Profile profile = snapshot.getValue(Profile.class);

                    if (profile.getUserName().equals(tvInstaUserName.getText().toString())) {

                        // If already present
                        DatabaseReference followingRef = FirebaseDatabase.getInstance().getReference("Followers").child(profile.getId());
                        followingRef.push().setValue(currentUserModel);

                        DatabaseReference otherUserProfile = FirebaseDatabase.getInstance().getReference("Following").child(currentUserModel.getId());
                        otherUserProfile.push().setValue(profile);

                    }

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onBackPressed() {

    }

    public void fillPhotoPostList() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Post");
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Query query = reference.orderByChild("id").equalTo(firebaseUser.getUid());

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Post post = snapshot.getValue(Post.class);


                    if (post.getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {

                        // If already present
                        String photoUrl = post.getPhoto();
                        photosPost.add(photoUrl);


                        rvUserProfile.setVisibility(View.VISIBLE);
                        //  tlUserProfileTabs.setVisibility(View.VISIBLE);
                        vUserProfileRoot.setVisibility(View.VISIBLE);

                        userPhotosAdapter.notifyDataSetChanged();
                        postCount.setText(photosPost.size() + "");

                    }

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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

                Direction direction = getDirection(x1, y1, x2, y2);
                return onSwipe(direction);
            }


            @Override
            public boolean onSwipe(Direction direction) {

                // Possible implementation


                if (direction == OnSwipeListener.Direction.right) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    Bundle bndlAnimation = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.left_to_rigth_for_lr, R.anim.right_to_left_for_lr).toBundle();
                    startActivity(intent, bndlAnimation);
                    finish();
                    return true;
                }


                return super.onSwipe(direction);

            }
        };

        return onSwipeListener;

    }


    private void setupRevealBackground(Bundle savedInstanceState) {
        vRevealBackground.setOnStateChangeListener(this);
        if (savedInstanceState == null) {

            vRevealBackground.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {

                    // Change starting point of reveal

                    int[] startingLocation = new int[2];
                    vUserDetails.getLocationOnScreen(startingLocation);
                    startingLocation[0] += vUserDetails.getWidth() / 2;

                    vRevealBackground.getViewTreeObserver().removeOnPreDrawListener(this);
                    vRevealBackground.startFromLocation(startingLocation);
                    return false;
                }
            });
        }
    }


    private void setupUserProfileGrid() {

        final StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        rvUserProfile.setLayoutManager(layoutManager);
        rvUserProfile.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                userPhotosAdapter.setLockedAnimations(true);
            }
        });
    }


    @Override
    public void onStateChange(int state) {
        if (RevealBackgroundView.STATE_FINISHED == state) {
            rvUserProfile.setVisibility(View.VISIBLE);
            //  tlUserProfileTabs.setVisibility(View.VISIBLE);
            vUserProfileRoot.setVisibility(View.VISIBLE);


            userPhotosAdapter = new UserProfileAdapter(this, photosPost);
            rvUserProfile.setAdapter(userPhotosAdapter);
            animateUserProfileHeader();
        } else {
            //  tlUserProfileTabs.setVisibility(View.INVISIBLE);
            rvUserProfile.setVisibility(View.INVISIBLE);
            vUserProfileRoot.setVisibility(View.INVISIBLE);
        }
    }


    private void animateUserProfileHeader() {
        vUserProfileRoot.setTranslationY(-vUserProfileRoot.getHeight());
        ivUserProfilePhoto.setTranslationY(-ivUserProfilePhoto.getHeight());
        vUserDetails.setTranslationY(-vUserDetails.getHeight());
        vUserStats.setAlpha(0);

        vUserProfileRoot.animate().translationY(0).setDuration(300).setInterpolator(INTERPOLATOR);
        ivUserProfilePhoto.animate().translationY(0).setDuration(300).setStartDelay(100).setInterpolator(INTERPOLATOR);
        vUserDetails.animate().translationY(0).setDuration(300).setStartDelay(200).setInterpolator(INTERPOLATOR);
        vUserStats.animate().alpha(1).setDuration(200).setStartDelay(400).setInterpolator(INTERPOLATOR).start();
    }

    public void getData() {

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("UserLoginModel");
        DatabaseReference profileRef = FirebaseDatabase.getInstance().getReference("Profile");
        DatabaseReference followersRef = FirebaseDatabase.getInstance().getReference("Followers").child(FirebaseAuth.getInstance().getUid());
        DatabaseReference followingRef = FirebaseDatabase.getInstance().getReference("Following").child(FirebaseAuth.getInstance().getUid());


        followersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                followersCount.setText(dataSnapshot.getChildrenCount() + "");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        followingRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                followingCount.setText(dataSnapshot.getChildrenCount() + "");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        Query query = userRef.orderByChild("id").equalTo(firebaseUser.getUid());

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    UserLoginModel userLoginModel = snapshot.getValue(UserLoginModel.class);


                    if (userLoginModel.getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {


                        // If already present
                        String name = userLoginModel.getName();
                        tvUserName.setText(name);


                    }

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        query = profileRef.orderByChild("id").equalTo(firebaseUser.getUid());

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Profile profile = snapshot.getValue(Profile.class);


                    if (profile.getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {


                        // If already present
                        String name = profile.getUserName();
                        String occupation = profile.getOccupation();
                        profilePhoto = profile.getDp();
                        tvInstaUserName.setText(name);
                        tvOccupation.setText(occupation);
                        updatedDp();

                        currentUserModel = new Profile(firebaseUser.getUid(), name, occupation, profile.getPosts(), profile.getFollowers(), profile.getFollowing(), profilePhoto);


                    }


                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


}