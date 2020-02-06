package com.example.socialnetwork;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GestureDetectorCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialnetwork.ChatAllEntities.ChatActivity;
import com.example.socialnetwork.adapter.FeedAdapter;
import com.example.socialnetwork.adapter.StoryAdapter;
import com.example.socialnetwork.model.Post;
import com.example.socialnetwork.model.Story;
import com.example.socialnetwork.swipe_listener.OnSwipeListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements FeedAdapter.OnFeedItemClickListener,
        FeedContextMenu.OnFeedContextMenuItemClickListener, View.OnTouchListener {

    private FeedAdapter feedAdapter;
    Toolbar toolbar;
    RecyclerView rvFeed;

    TextView ivLogo;
    List<Post> postList;
    private DatabaseReference mMessageDatabaseReference;
    private ChildEventListener childEventListener;
    public static String storyId = "";
    boolean backPressToExit = false;
    // Up down gestures
    GestureDetectorCompat gestureDetector;


    @Override
    public void onBackPressed() {


        if (backPressToExit) {

            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(a);
        }

        Snackbar.make(ivLogo, "Back Again to Exit", Snackbar.LENGTH_SHORT).show();

        this.backPressToExit = true;
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                backPressToExit = false;
            }
        }, 2000);


    }


    // Must use for recycle view

    private void setupFeed() {

        //Increase the amount of extra space that should be laid out by LayoutManager.

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this) {
            @Override
            protected int getExtraLayoutSpace(RecyclerView.State state) {
                return 300;
            }

        };
        rvFeed.setLayoutManager(linearLayoutManager);
        rvFeed.setItemAnimator(new DefaultItemAnimator());

        //feedAdapter = new FeedAdapter(this);
        feedAdapter = new FeedAdapter(postList, this);
        rvFeed.setAdapter(feedAdapter);
        feedAdapter.setOnFeedItemClickListener(this);

    }

    private void setupStory() {
        storyLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        storyRecyclerView.setLayoutManager(storyLayoutManager);
        storyRecyclerView.setItemAnimator(new DefaultItemAnimator());

        storyAdapter = new StoryAdapter(storyData, this);
        storyRecyclerView.setAdapter(storyAdapter);

    }


//Animation

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    private static final int ANIM_DURATION_TOOLBAR = 300;

    private void startIntroAnimation() {

        int actionbarSize = dpToPx(56);
        toolbar.setTranslationY(-actionbarSize);
        ivLogo.setTranslationY(-actionbarSize);
        inboxMenuItem.setTranslationY(-actionbarSize);
        //     inboxMenuItem.getActionView().setTranslationY(-actionbarSize);

        toolbar.animate()
                .translationY(0)
                .setDuration(ANIM_DURATION_TOOLBAR)
                .setStartDelay(200);
        ivLogo.animate()
                .translationY(0)
                .setDuration(300)
                .setStartDelay(400);
        inboxMenuItem.animate()
                .translationY(0)
                .setDuration(500)
                .setStartDelay(700)
                .start();

        feedAdapter.updateItems();

    }

    //FAB animation
    private static final int ANIM_DURATION_FAB = 400;
    ImageButton inboxMenuItem;

    // Bottom Story
    private static RecyclerView.Adapter storyAdapter;
    private RecyclerView.LayoutManager storyLayoutManager;
    private static RecyclerView storyRecyclerView;
    private static ArrayList<Story> storyData;

    // RecycleerView Hide and Show

    View storyFragment;

    private void startStoryFragmentAnimUp() {

        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                storyFragment.getHeight(),  // fromYDelta
                0);                // toYDelta
        animate.setDuration(500);
        storyFragment.startAnimation(animate);
    }

    private void startStoryFragmentAnimDown() {
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                0,                 // fromYDelta
                storyFragment.getHeight() + 500); // toYDelta
        animate.setDuration(500);
        storyFragment.startAnimation(animate);

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        rvFeed = findViewById(R.id.rvFeed);

        ivLogo = findViewById(R.id.ivLogo);
        inboxMenuItem = findViewById(R.id.msz);
        rvFeed.setHasFixedSize(true);
        storyFragment = findViewById(R.id.storyViewModeFragment);
        storyRecyclerView = findViewById(R.id.rvStory);
        storyRecyclerView.setHasFixedSize(true);
        storyData = new ArrayList<>();

        Story s1 = new Story(FirebaseAuth.getInstance().getUid(), "Itachi Uchiha", "https://firebasestorage.googleapis.com/v0/b/connectionsmine.appspot.com/o/image_photos%2Fimage%3A206953?alt=media&token=5fd0444c-b758-45af-98ba-f49586a94239");
        Story s2 = new Story(FirebaseAuth.getInstance().getUid(), "Madara Uchiha", "https://firebasestorage.googleapis.com/v0/b/connectionsmine.appspot.com/o/image_photos%2Fimage%3A207044?alt=media&token=d65709b3-2ee6-4d52-a59b-e159baf659b1");
        Story s3 = new Story(FirebaseAuth.getInstance().getUid(), "Sasuke Uchiha", "https://firebasestorage.googleapis.com/v0/b/connectionsmine.appspot.com/o/image_photos%2F51267?alt=media&token=84ad3eaf-6ed8-4b74-8a85-3ae877f41bd5");
        Story s4 = new Story(FirebaseAuth.getInstance().getUid(), "Hatake Kakashi", "https://firebasestorage.googleapis.com/v0/b/connectionsmine.appspot.com/o/image_photos%2F84037?alt=media&token=5e210192-1f31-41e9-b822-7ab44e672a34");
        Story s5 = new Story(FirebaseAuth.getInstance().getUid(), "Riyuzaki El", "https://firebasestorage.googleapis.com/v0/b/connectionsmine.appspot.com/o/image_photos%2F196029?alt=media&token=d28a2906-e0cb-4932-b183-d837c0a4d38b");

        Story s10 = new Story(FirebaseAuth.getInstance().getUid(), "Itachi Uchiha", "https://firebasestorage.googleapis.com/v0/b/connectionsmine.appspot.com/o/image_photos%2Fimage%3A206953?alt=media&token=5fd0444c-b758-45af-98ba-f49586a94239");
        Story s9 = new Story(FirebaseAuth.getInstance().getUid(), "Madara Uchiha", "https://firebasestorage.googleapis.com/v0/b/connectionsmine.appspot.com/o/image_photos%2Fimage%3A207044?alt=media&token=d65709b3-2ee6-4d52-a59b-e159baf659b1");
        Story s8 = new Story(FirebaseAuth.getInstance().getUid(), "Sasuke Uchiha", "https://firebasestorage.googleapis.com/v0/b/connectionsmine.appspot.com/o/image_photos%2F51267?alt=media&token=84ad3eaf-6ed8-4b74-8a85-3ae877f41bd5");
        Story s7 = new Story(FirebaseAuth.getInstance().getUid(), "Hatake Kakashi", "https://firebasestorage.googleapis.com/v0/b/connectionsmine.appspot.com/o/image_photos%2F84037?alt=media&token=5e210192-1f31-41e9-b822-7ab44e672a34");
        Story s6 = new Story(FirebaseAuth.getInstance().getUid(), "Riyuzaki El", "https://firebasestorage.googleapis.com/v0/b/connectionsmine.appspot.com/o/image_photos%2F196029?alt=media&token=d28a2906-e0cb-4932-b183-d837c0a4d38b");


        storyData.add(s1);
        storyData.add(s2);
        storyData.add(s3);
        storyData.add(s4);
        storyData.add(s5);
        storyData.add(s6);
        storyData.add(s7);
        storyData.add(s8);
        storyData.add(s9);
        storyData.add(s10);

        storyFragment.setVisibility(View.GONE);


        // Swipe for story
        OnSwipeListener onSwipeListenerUpDown = generateSwipeListenerForStory();
        View view = findViewById(R.id.vStory);
        gestureDetector = new GestureDetectorCompat(this, onSwipeListenerUpDown);
        view.setOnTouchListener(this);

        /*
        // Swipe for camera And Profile
        OnSwipeListener onSwipeListenerLeftRight = generateSwipeListenerForCamera();
        View leftRight = findViewById(R.id.idLeftRightNav);
        gestureDetectorCamera = new GestureDetectorCompat(this, onSwipeListenerLeftRight);
        leftRight.setOnTouchListener(this);


         */


        new Thread(new Runnable() {
            @Override
            public void run() {

                setupStory();

            }
        }).start();


        inboxMenuItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ChatActivity.class));

            }
        });

        ivLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseAuth.getInstance().signOut();
            }
        });

        postList = new ArrayList<>();

        mMessageDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Post");

        setupFeed();
        startIntroAnimation();


        rvFeed.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                FeedContextMenuManager.getInstance().onScrolled(recyclerView, dx, dy);
            }
        });


        new Thread(new Runnable() {
            @Override
            public void run() {
                attachDbReadListener();

            }
        }).start();
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

                if (direction == Direction.up) {

                    storyFragment.setVisibility(View.VISIBLE);
                    startStoryFragmentAnimUp();
                    return true;
                } else if (direction == OnSwipeListener.Direction.down) {

                    startStoryFragmentAnimDown();
                    storyFragment.setVisibility(View.GONE);
                    return true;

                } else if (direction == OnSwipeListener.Direction.left) {

                    Intent intent = new Intent(getApplicationContext(), UserProfileActivity.class);
                    Bundle bndlAnimation = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.slide_left_anim, R.anim.slide_right_anim).toBundle();
                    startActivity(intent, bndlAnimation);
                    return true;
                } else if (direction == OnSwipeListener.Direction.right) {
                    Intent intent = new Intent(getApplicationContext(), CameraXNew.class);
                    Bundle bndlAnimation = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.left_to_rigth_for_lr, R.anim.right_to_left_for_lr).toBundle();
                    startActivity(intent, bndlAnimation);
                    return true;

                }


                return super.onSwipe(direction);
            }
        };

        return onSwipeListener;

    }

    String id;
    String photo;
    String caption;
    int likes;
    String dp;
    String userName;


    private void attachDbReadListener() {

        if (childEventListener == null) {
            childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    Post p = (Post) dataSnapshot.getValue(Post.class);

                    id = p.getId();
                    photo = p.getPhoto();
                    caption = p.getCaption();
                    likes = p.getLikes();
                    dp = p.getDp();
                    userName = p.getUserName();

                    postList.add(new Post(id, photo, caption, likes, dp, userName));

                    feedAdapter.notifyDataSetChanged();


                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }

            };
            mMessageDatabaseReference.addChildEventListener(childEventListener);

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        detachDbReadListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        attachDbReadListener();
        postList.clear();
        feedAdapter.notifyDataSetChanged();
    }

    private void detachDbReadListener() {

        if (childEventListener != null) {
            mMessageDatabaseReference.removeEventListener(childEventListener);
            childEventListener = null;
        }
    }


    // comment feed


    @Override
    public void onCommentsClick(View v, int position) {
// Static transition to comment activity

        final Intent intent = new Intent(this, CommentsActivity.class);

        //Get location on screen for tapped view where we tapped on mainactivity for comment section
        int[] startingLocation = new int[2];
        v.getLocationOnScreen(startingLocation);
        intent.putExtra(CommentsActivity.ARG_DRAWING_START_LOCATION, startingLocation[1]);

        startActivity(intent);
        //Disable enter transition for new Acitvity
        overridePendingTransition(0, 0);


    }

    @Override
    public void onMoreClick(View v, int itemPosition) {

        FeedContextMenuManager.getInstance().toggleContextMenuFromView(v, itemPosition, this);
    }

    // circular anim for profile clicking

    @Override
    public void onProfileClick(View v) {

        int[] startingLocation = new int[2];
        v.getLocationOnScreen(startingLocation);
        startingLocation[0] += v.getWidth() / 2;
        //  UserProfileActivity.startUserProfileFromLocation(startingLocation, this);
        overridePendingTransition(0, 0);

    }


    @Override
    public void onReportClick(int feedItem) {

        FeedContextMenuManager.getInstance().hideContextMenu();
    }

    @Override
    public void onSharePhotoClick(int feedItem) {

        FeedContextMenuManager.getInstance().hideContextMenu();
    }

    @Override
    public void onCopyShareUrlClick(int feexdItem) {
        FeedContextMenuManager.getInstance().hideContextMenu();

    }


    @Override
    public void onCancelClick(int feedItem) {
        FeedContextMenuManager.getInstance().hideContextMenu();

    }

    public void openCamera(View v) {

        // startActivity(new Intent(getApplicationContext(), UserProfileActivity.class ));

        startActivity(new Intent(getApplicationContext(), CameraXNew.class));
        //  overridePendingTransition(0, 0);


    }


}
