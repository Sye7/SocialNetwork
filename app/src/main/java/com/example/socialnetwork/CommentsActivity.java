package com.example.socialnetwork;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.socialnetwork.adapter.CommentsAdapter;
import com.example.socialnetwork.adapter.FeedAdapter;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;

import static java.security.AccessController.getContext;


public class CommentsActivity extends AppCompatActivity  implements SendCommentButton.OnSendClickListener {



    public static final String ARG_DRAWING_START_LOCATION = "arg_drawing_start_location";

    Toolbar toolbar;
    LinearLayout contentRoot;
    RecyclerView rvComments;
    LinearLayout llAddComment;
    EditText et_comment;
    private int drawingStartLocation;
    private CommentsAdapter commentsAdapter;

    Button sendButton;



    @BindView(R.id.btnSendComment)
    SendCommentButton btnSendComment;


    // send button anim
    boolean flag = true;


    @Override
    public void onSendClickListener(View v) {
        if (validateComment()) {
            commentsAdapter.addItem();
            commentsAdapter.setAnimationsLocked(false);
            commentsAdapter.setDelayEnterAnimation(false);
            rvComments.smoothScrollBy(0, rvComments.getChildAt(0).getHeight() * commentsAdapter.getItemCount());

            et_comment.setText("");

            sendButton.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_out_send));
            sendButton.setText("✓");

            final Context context = this;

            new Handler().postDelayed(
                    new Runnable() {
                        @Override
                        public void run() {
                            sendButton.startAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_in_send));

                            sendButton.setText("SEND");

                        }
                    }, 300);
        }


    }

    public void setupSendCommentButton() {

        btnSendComment.setOnSendClickListener(this);


    }

    public boolean validateComment() {
        if (TextUtils.isEmpty(et_comment.getText())) {
            sendButton.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake_error));
            return false;
        }

        return true;
    }





    @Override
    public void onBackPressed() {


        contentRoot.animate()
                .translationY((float) Resources.getSystem().getDisplayMetrics().heightPixels)
                .setDuration(200)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        CommentsActivity.super.onBackPressed();
                        overridePendingTransition(0, 0);
                    }
                })
                .start();
    }



    private void setupComments() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvComments.setLayoutManager(linearLayoutManager);
        rvComments.setHasFixedSize(true);

        commentsAdapter = new CommentsAdapter(this);
        rvComments.setAdapter(commentsAdapter);
        rvComments.setOverScrollMode(View.OVER_SCROLL_NEVER);
        rvComments.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    commentsAdapter.setAnimationsLocked(true);
                }
            }
        });
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        toolbar = findViewById(R.id.toolbar);
        contentRoot = findViewById(R.id.contentRoot);
        rvComments = findViewById(R.id.rvComments);
        llAddComment = findViewById(R.id.llAddComment);
        et_comment = findViewById(R.id.et_comment);
        btnSendComment = new SendCommentButton(this);
        sendButton = findViewById(R.id.btnSendComment);


        ButterKnife.bind(this);

        setupComments();
        setupSendCommentButton();







        drawingStartLocation = getIntent().getIntExtra(ARG_DRAWING_START_LOCATION, 0);
        if (savedInstanceState == null) {
            contentRoot.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    contentRoot.getViewTreeObserver().removeOnPreDrawListener(this);
                    startIntroAnimation();
                    return true;
                }
            });
        }

    }



    private void startIntroAnimation() {
        contentRoot.setScaleY(0.1f);
        contentRoot.setPivotY(drawingStartLocation);
        llAddComment.setTranslationY(100);

        contentRoot.animate()
                .scaleY(1)
                .setDuration(200)
                .setInterpolator(new AccelerateInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        animateContent();
                    }
                })
                .start();
    }

    private void animateContent() {
        commentsAdapter.updateItems();
        llAddComment.animate().translationY(0)
                .setInterpolator(new DecelerateInterpolator())
                .setDuration(200)
                .start();
    }



}
