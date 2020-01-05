package com.example.socialnetwork;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;


public class FeedContextMenu extends LinearLayout {
    private  final int CONTEXT_MENU_WIDTH = dpToPx(240);

    private int feedItem = -1;

    private OnFeedContextMenuItemClickListener onItemClickListener;

    public FeedContextMenu(Context context) {
        super(context);
        init();
    }


    public  int dpToPx(int dp) {
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }


    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_context_menu, this, true);
        setOrientation(VERTICAL);
        setLayoutParams(new LayoutParams(CONTEXT_MENU_WIDTH, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    public void bindToItem(int feedItem) {
        this.feedItem = feedItem;
    }

    Button btnReport;
    Button btnSharePhoto;
    Button btnCopyShareUrl;
    Button btnCancel;


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();


        btnReport = findViewById(R.id.btnReport);
        btnSharePhoto = findViewById(R.id.btnSharePhoto);
        btnCopyShareUrl = findViewById(R.id.btnCopyShareUrl);
        btnCancel = findViewById(R.id.btnCancel);

        btnReport.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (onItemClickListener != null) {
                    onItemClickListener.onReportClick(feedItem);
                }
            }
        });


        btnSharePhoto.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (onItemClickListener != null) {
                    onItemClickListener.onSharePhotoClick(feedItem);
                }
            }
        });

        btnCopyShareUrl.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (onItemClickListener != null) {
                    onItemClickListener.onCopyShareUrlClick(feedItem);
                }
            }
        });


        btnCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (onItemClickListener != null) {
                    onItemClickListener.onCancelClick(feedItem);
                }
            }
        });



    }

    public void dismiss() {
        ((ViewGroup) getParent()).removeView(FeedContextMenu.this);
    }


    public void setOnFeedMenuItemClickListener(OnFeedContextMenuItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnFeedContextMenuItemClickListener {
        public void onReportClick(int feedItem);

        public void onSharePhotoClick(int feedItem);

        public void onCopyShareUrlClick(int feedItem);

        public void onCancelClick(int feedItem);
    }
}