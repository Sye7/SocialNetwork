package com.example.socialnetwork;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.socialnetwork.CircularImage.CircleTransformation;
import com.example.socialnetwork.adapter.UserProfileAdapter;
import com.example.socialnetwork.model.Profile;
import com.example.socialnetwork.model.User;
import com.example.socialnetwork.view.RevealBackgroundView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


public class UserProfileActivity extends AppCompatActivity implements RevealBackgroundView.OnStateChangeListener {
    public static final String ARG_REVEAL_START_LOCATION = "reveal_start_location";

    private static final int USER_OPTIONS_ANIMATION_DELAY = 300;
    private static final Interpolator INTERPOLATOR = new DecelerateInterpolator();

    RevealBackgroundView vRevealBackground;

    RecyclerView rvUserProfile;
   // private ActivityMainBinding binding;;


    ImageView ivUserProfilePhoto;
    View vUserDetails;
    Button btnFollow;
    View vUserStats;
    View vUserProfileRoot;
    TextView tvUserName;
    TextView tvInstaUserName;
    TextView tvOccupation;

    private int avatarSize;
    private String profilePhoto;
    private UserProfileAdapter userPhotosAdapter;

    public static void startUserProfileFromLocation(int[] startingLocation, Activity startingActivity) {
        Intent intent = new Intent(startingActivity, UserProfileActivity.class);
        intent.putExtra(ARG_REVEAL_START_LOCATION, startingLocation);
        startingActivity.startActivity(intent);
    }

    public void setting(View view)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Profile");
        View viewInflated = LayoutInflater.from(this).inflate(R.layout.edit_profile_dialog, (ViewGroup) findViewById(android.R.id.content), false);
        final EditText userName = (EditText) viewInflated.findViewById(R.id.et_username);
        final EditText interest = (EditText) viewInflated.findViewById(R.id.et_interest);
        final ImageView dp = (ImageView) viewInflated.findViewById(R.id.iv_editDp);

        final Profile updateProfile = new Profile();
        builder.setView(viewInflated);

// Set up the buttons
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Profile");
               Query query = ref.orderByChild("id").equalTo(FirebaseAuth.getInstance().getUid());

                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Profile profile = snapshot.getValue(Profile.class);


                            if (profile.getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {


                                // If already present
                                updateProfile.setId(profile.getId());
                                updateProfile.setDp(profile.getDp());
                                updateProfile.setFollowers(profile.getFollowers());
                                updateProfile.setFollowing(profile.getFollowing());
                                updateProfile.setPosts(profile.getPosts());

                                updateProfile.setUserName(userName.getText().toString());
                                updateProfile.setOccupation(interest.getText().toString());



                                snapshot.getRef().setValue(updateProfile).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        Toast.makeText(UserProfileActivity.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
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
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);


        this.avatarSize = getResources().getDimensionPixelSize(R.dimen.user_profile_avatar_size);
        this.profilePhoto = getString(R.string.user_profile_photo);

        vRevealBackground = (RevealBackgroundView) findViewById(R.id.vRevealBackground);
        rvUserProfile = findViewById(R.id.rvUserProfile);
        ivUserProfilePhoto = findViewById(R.id.ivUserProfilePhoto);
        vUserDetails  = findViewById(R.id.vUserDetails);
        btnFollow = findViewById(R.id.btnFollow);
        vUserStats = findViewById(R.id.vUserStats);
        vUserProfileRoot = findViewById(R.id.vUserProfileRoot);
        tvUserName = findViewById(R.id.tvUserName);
        tvInstaUserName = findViewById(R.id.tv_insta_UserName);
        tvOccupation = findViewById(R.id.tvOccupation);


        Picasso.get()
                .load(profilePhoto)
                .placeholder(R.drawable.img_circle_placeholder)
                .resize(avatarSize, avatarSize)
                .centerCrop()
                .transform(new CircleTransformation())
                .into(ivUserProfilePhoto);


      //  setupTabs();
        setupUserProfileGrid();
        setupRevealBackground(savedInstanceState);
        getData();
    }

   /* private void setupTabs() {
        tlUserProfileTabs.addTab(tlUserProfileTabs.newTab().setIcon(R.drawable.ic_grid_on_white));
        tlUserProfileTabs.addTab(tlUserProfileTabs.newTab().setIcon(R.drawable.ic_list_white));
        tlUserProfileTabs.addTab(tlUserProfileTabs.newTab().setIcon(R.drawable.ic_place_white));
        tlUserProfileTabs.addTab(tlUserProfileTabs.newTab().setIcon(R.drawable.ic_label_white));
    }
    */


    private void setupRevealBackground(Bundle savedInstanceState) {
        vRevealBackground.setOnStateChangeListener(this);
        if (savedInstanceState == null) {
            final int[] startingLocation = getIntent().getIntArrayExtra(ARG_REVEAL_START_LOCATION);
            vRevealBackground.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    vRevealBackground.getViewTreeObserver().removeOnPreDrawListener(this);
                    vRevealBackground.startFromLocation(startingLocation);
                    return false;
                }
            });
        } else {
            userPhotosAdapter.setLockedAnimations(true);
            vRevealBackground.setToFinishedFrame();
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
            userPhotosAdapter = new UserProfileAdapter(this);
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

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("User");
        DatabaseReference profileRef = FirebaseDatabase.getInstance().getReference("Profile");



        Query query = userRef.orderByChild("id").equalTo(firebaseUser.getUid());

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);


                    if (user.getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {


                        // If already present
                        String name = user.getName();
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
                        snapshot.getRef().setValue(profile);
                        String name = profile.getUserName();
                        String occupation =  profile.getOccupation();

                        tvInstaUserName.setText(name);
                        tvOccupation.setText(occupation);


                    }

                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

}