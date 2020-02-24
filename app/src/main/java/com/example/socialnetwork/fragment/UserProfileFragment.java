package com.example.socialnetwork.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GestureDetectorCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.socialnetwork.CircularImage.CircleTransformation;
import com.example.socialnetwork.FollowerAndFollowingActivity;
import com.example.socialnetwork.R;
import com.example.socialnetwork.adapter.UserProfileAdapter;
import com.example.socialnetwork.model.Post;
import com.example.socialnetwork.model.Profile;
import com.example.socialnetwork.model.UserLoginModel;
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

public class UserProfileFragment extends Fragment implements RevealBackgroundView.OnStateChangeListener {


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
    Context thisCtx;

    LinearLayout following;
    LinearLayout follower;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_user_profile, container, false);

        this.avatarSize = getResources().getDimensionPixelSize(R.dimen.user_profile_avatar_size);
        thisCtx = inflater.getContext();


        vRevealBackground = (RevealBackgroundView) view.findViewById(R.id.vRevealBackground);
        rvUserProfile = view.findViewById(R.id.rvUserProfile);
        ivUserProfilePhoto = view.findViewById(R.id.ivUserProfilePhoto);
        vUserDetails = view.findViewById(R.id.vUserDetails);
        btnFollow = view.findViewById(R.id.btnFollow);
        vUserStats = view.findViewById(R.id.vUserStats);
        vUserProfileRoot = view.findViewById(R.id.vUserProfileRoot);
        tvUserName = view.findViewById(R.id.tvUserName);
        tvInstaUserName = view.findViewById(R.id.tv_insta_UserName);
        tvOccupation = view.findViewById(R.id.tvOccupation);
        ctx = thisCtx;
        photosPost = new ArrayList<>();

        follower = view.findViewById(R.id.followerLayout);
        following = view.findViewById(R.id.followingLayout);

        postCount = view.findViewById(R.id.tvPostCount);
        followersCount = view.findViewById(R.id.tvFollowersCount);
        followingCount = view.findViewById(R.id.tvFollowingCount);

        rvUserProfile.setVisibility(View.VISIBLE);
        //  tlUserProfileTabs.setVisibility(View.VISIBLE);
        vUserProfileRoot.setVisibility(View.VISIBLE);

        btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setting();

            }
        });


        userPhotosAdapter = new UserProfileAdapter(thisCtx, photosPost);
        animateUserProfileHeader();
        scrv = view.findViewById(R.id.scRv);


        fillPhotoPostList();


        mFirebaseStorage = FirebaseStorage.getInstance();
        mChatPhotoStorageRef = mFirebaseStorage.getReference().child("image_photos");


        setupUserProfileGrid();
        setupRevealBackground(savedInstanceState);
        getData();

        follower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getContext(), FollowerAndFollowingActivity.class);
                intent.putExtra("followOrFollowing", "follower");
                startActivity(intent);

            }
        });

        following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getContext(), FollowerAndFollowingActivity.class);
                intent.putExtra("followOrFollowing", "following");
                startActivity(intent);
            }
        });

        return view;
    }


    public void startFollowUnfollow() {

        if (currentUserModel.getUserName().equals(tvInstaUserName.getText())) {
            Toast.makeText(thisCtx, "Not Allowed", Toast.LENGTH_SHORT).show();
            return;
        }

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


    public void EditDp(View view) {
        // TODO: Fire an intent to show an image picker
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/jpeg");
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        startActivityForResult(Intent.createChooser(intent, "Complete Action Using"), RC_PHOTO_PICKER);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {


        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_PHOTO_PICKER && resultCode == getActivity().RESULT_OK) {

            selectedImageUri = data.getData();

        }


    }

    Profile updateProfile = new Profile();

    public void setting() {
        AlertDialog.Builder builder = new AlertDialog.Builder(thisCtx);
        builder.setTitle("Edit Profile");
        View viewInflated = LayoutInflater.from(thisCtx).inflate(R.layout.edit_profile_dialog, (ViewGroup) getView().findViewById(android.R.id.content), false);
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

                            Toast.makeText(thisCtx, "Image Uploaded Successfully", Toast.LENGTH_SHORT).show();
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


    GestureDetectorCompat gestureDetector;

    Context ctx;
    ScrollView scrv;
    Profile currentUserModel;


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
