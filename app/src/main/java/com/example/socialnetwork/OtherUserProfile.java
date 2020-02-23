package com.example.socialnetwork;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.socialnetwork.CircularImage.CircleTransformation;
import com.example.socialnetwork.adapter.UserProfileAdapter;
import com.example.socialnetwork.model.Post;
import com.example.socialnetwork.model.Profile;
import com.example.socialnetwork.model.UserLoginModel;
import com.example.socialnetwork.view.RevealBackgroundView;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class OtherUserProfile extends AppCompatActivity implements RevealBackgroundView.OnStateChangeListener {

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
    Context thisCtx;


    String otherUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_user_profile);


        Intent intent = getIntent();
        otherUserName = intent.getStringExtra("userName");
        this.avatarSize = getResources().getDimensionPixelSize(R.dimen.user_profile_avatar_size);

        thisCtx= this;


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
        photosPost = new ArrayList<>();

        postCount = findViewById(R.id.tvPostCount);
        followersCount = findViewById(R.id.tvFollowersCount);
        followingCount = findViewById(R.id.tvFollowingCount);

        rvUserProfile.setVisibility(View.VISIBLE);
        //  tlUserProfileTabs.setVisibility(View.VISIBLE);
        vUserProfileRoot.setVisibility(View.VISIBLE);

        btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFollowUnfollow();

            }
        });


        userPhotosAdapter = new UserProfileAdapter(thisCtx, photosPost);
        animateUserProfileHeader();

        getOtherUserDataModel();
        getCurrentUserDataModel();



        mFirebaseStorage = FirebaseStorage.getInstance();
        mChatPhotoStorageRef = mFirebaseStorage.getReference().child("image_photos");


        setupUserProfileGrid();
        setupRevealBackground(savedInstanceState);

    }

    Profile otherUserProfileModel;
    Profile currentUserModel;

    public void getCurrentUserDataModel()
    {

        final DatabaseReference profileRef = FirebaseDatabase.getInstance().getReference("Profile");

        Query query;
        query = profileRef.orderByChild("id").equalTo(FirebaseAuth.getInstance().getUid());

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Profile profile = snapshot.getValue(Profile.class);


                    if (profile.getId().equals(FirebaseAuth.getInstance().getUid())) {

                        currentUserModel = profile;

                    }


                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public void getOtherUserDataModel() {


        final DatabaseReference otherUserProfile = FirebaseDatabase.getInstance().getReference("Profile");

        Query query = otherUserProfile.orderByChild("userName").equalTo(otherUserName);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Profile profile = snapshot.getValue(Profile.class);


                    if (profile.getUserName().equals(otherUserName)) {

                        otherUserProfileModel = profile;
                        fillPhotoPostList();
                        getData();



                    }

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void startFollowUnfollow() {


        DatabaseReference otherUserProfile = FirebaseDatabase.getInstance().getReference("Profile");


        Query query = otherUserProfile.orderByChild("userName").equalTo(otherUserProfileModel.getUserName());

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
                        btnFollow.setText("FOLLOWING");

                    }

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    public void fillPhotoPostList() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Post");
        Query query = reference.orderByChild("id").equalTo(otherUserProfileModel.getId());

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Post post = snapshot.getValue(Post.class);


                    if (post.getId().equals(otherUserProfileModel.getId())) {

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


    public void updatedDp() {

        Picasso.get()
                .load(profilePhoto)
                .placeholder(R.drawable.img_circle_placeholder)
                .resize(avatarSize, avatarSize)
                .centerCrop()
                .transform(new CircleTransformation())
                .into(ivUserProfilePhoto);
    }


    GestureDetectorCompat gestureDetector;

    Context ctx;
    ScrollView scrv;


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


            userPhotosAdapter = new UserProfileAdapter(thisCtx, photosPost);
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
        DatabaseReference followersRef = FirebaseDatabase.getInstance().getReference("Followers").child(otherUserProfileModel.getId());
        DatabaseReference followingRef = FirebaseDatabase.getInstance().getReference("Following").child(otherUserProfileModel.getId());


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


        Query query = userRef.orderByChild("id").equalTo(otherUserProfileModel.getId());

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    UserLoginModel userLoginModel = snapshot.getValue(UserLoginModel.class);


                    if (userLoginModel.getId().equals(otherUserProfileModel.getId())) {


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

        query = profileRef.orderByChild("id").equalTo(otherUserProfileModel.getId());

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Profile profile = snapshot.getValue(Profile.class);


                    if (profile.getId().equals(otherUserProfileModel.getId())) {


                        // If already present
                        String name = profile.getUserName();
                        String occupation = profile.getOccupation();
                        profilePhoto = profile.getDp();
                        tvInstaUserName.setText(name);
                        tvOccupation.setText(occupation);
                        updatedDp();



                    }


                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


}
