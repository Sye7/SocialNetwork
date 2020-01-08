package com.example.socialnetwork;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.socialnetwork.adapter.FeedAdapter;



public class MainActivity extends AppCompatActivity implements FeedAdapter.OnFeedItemClickListener,
        FeedContextMenu.OnFeedContextMenuItemClickListener   {

    private FeedAdapter feedAdapter;
    Toolbar toolbar;
    RecyclerView rvFeed;

    ImageView ivLogo;
    ImageButton btnCreate;


    // Must use for recycle view

    private void setupFeed() {

        //Increase the amount of extra space that should be laid out by LayoutManager.

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this){
            @Override
            protected int getExtraLayoutSpace(RecyclerView.State state) {
                return 300;
            }

        };
        rvFeed.setLayoutManager(linearLayoutManager);

        feedAdapter = new FeedAdapter(this);
        rvFeed.setAdapter(feedAdapter);
        feedAdapter.setOnFeedItemClickListener(this);

    }


//Animation

    public  int dpToPx(int dp) {
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    private static final int ANIM_DURATION_TOOLBAR = 300;

    private void startIntroAnimation() {
        btnCreate.setTranslationY(2 * getResources().getDimensionPixelOffset(R.dimen.btn_fab_size));

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

        startContentAnimation();
    }

    //FAB animation
    private static final int ANIM_DURATION_FAB = 400;
    ImageButton inboxMenuItem;

    private void startContentAnimation() {
        btnCreate.animate()
                .translationY(0)
                .setInterpolator(new OvershootInterpolator(1.f))
                .setStartDelay(1000)
                .setDuration(ANIM_DURATION_FAB)
                .start();
        feedAdapter.updateItems();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        rvFeed = findViewById(R.id.rvFeed);

        ivLogo = findViewById(R.id.ivLogo);
        btnCreate = findViewById(R.id.btnCreate);
        inboxMenuItem =  findViewById(R.id.msz);

        setupFeed();
        startIntroAnimation();

        rvFeed.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                FeedContextMenuManager.getInstance().onScrolled(recyclerView, dx, dy);
            }
        });
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

        /*
        By calling overridePendingTransition(0, 0);
        we disable exit animation (for MainActivity) and enter animation (for CommentsActivity).

         */

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
        UserProfileActivity.startUserProfileFromLocation(startingLocation, this);
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
}
