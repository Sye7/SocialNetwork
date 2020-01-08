package com.example.socialnetwork.adapter;


import android.content.Context;
import android.content.res.Resources;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.socialnetwork.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.List;


public class UserProfileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int PHOTO_ANIMATION_DELAY = 600;
    private static final Interpolator INTERPOLATOR = new DecelerateInterpolator();


    private final Context context;
    private final int cellSize;

    private final List<String> photos;

    private boolean lockedAnimations = false;
    private int lastAnimatedItem = -1;

    public UserProfileAdapter(Context context) {
        this.context = context;
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int mWidthScreen = display.getWidth();

        int width  = Resources.getSystem().getDisplayMetrics().widthPixels;
        this.cellSize = mWidthScreen / 3;
        this.photos = Arrays.asList(context.getResources().getStringArray(R.array.user_photos));
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.item_photo, parent, false);
        StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
        layoutParams.height = cellSize;
        layoutParams.width = cellSize;
        layoutParams.setFullSpan(false);
        view.setLayoutParams(layoutParams);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        bindPhoto((PhotoViewHolder) holder, position);
    }

    private void bindPhoto(final PhotoViewHolder holder, int position) {
        Picasso.get()
                .load(photos.get(position))
                .resize(cellSize, cellSize)
                .centerCrop()
                .into(holder.ivPhoto, new Callback() {
                    @Override
                    public void onSuccess() {
                        animatePhoto(holder);
                    }

                    @Override
                    public void onError(Exception e) {

                    }

                });
        if (lastAnimatedItem < position) lastAnimatedItem = position;
    }

    private void animatePhoto(PhotoViewHolder viewHolder) {
        if (!lockedAnimations) {
            if (lastAnimatedItem == viewHolder.getPosition()) {
                setLockedAnimations(true);
            }

            long animationDelay = PHOTO_ANIMATION_DELAY + viewHolder.getPosition() * 30;

            viewHolder.flRoot.setScaleY(0);
            viewHolder.flRoot.setScaleX(0);

            viewHolder.flRoot.animate()
                    .scaleY(1)
                    .scaleX(1)
                    .setDuration(200)
                    .setInterpolator(INTERPOLATOR)
                    .setStartDelay(animationDelay)
                    .start();
        }
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    static class PhotoViewHolder extends RecyclerView.ViewHolder {
        FrameLayout flRoot;
        ImageView ivPhoto;

        public PhotoViewHolder(View view) {
            super(view);

            flRoot = view.findViewById(R.id.flRoot);
            ivPhoto = view.findViewById(R.id.ivPhoto);
        }
    }

    public void setLockedAnimations(boolean lockedAnimations) {
        this.lockedAnimations = lockedAnimations;
    }
}

/*import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.socialnetwork.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.List;

import butterknife.internal.Utils;

public class UserProfileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int PHOTO_ANIMATION_DELAY = 600;
    private static final Interpolator INTERPOLATOR = new DecelerateInterpolator();


    private final Context context;
    private final int cellSize;

    private final List<String> photos;

    private boolean lockedAnimations = false;
    private int lastAnimatedItem = -1;

    private static final int USER_OPTIONS_ANIMATION_DELAY = 300;
    private static final int MAX_PHOTO_ANIMATION_DELAY = 600;


    public static final int TYPE_PROFILE_HEADER = 0;
    public static final int TYPE_PROFILE_OPTIONS = 1;
    public static final int TYPE_PHOTO = 2;

    private long profileHeaderAnimationStartTime = 0;

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_PROFILE_HEADER;
        } else if (position == 1) {
            return TYPE_PROFILE_OPTIONS;
        } else {
            return TYPE_PHOTO;
        }
    }

    // anim of user profile every photo
    private void animateUserProfileHeader(ProfileHeaderViewHolder viewHolder) {
        if (!lockedAnimations) {
            profileHeaderAnimationStartTime = System.currentTimeMillis();

            viewHolder.vUserProfileRoot.setTranslationY(-viewHolder.vUserProfileRoot.getHeight());
            viewHolder.ivUserProfilePhoto.setTranslationY(-viewHolder.ivUserProfilePhoto.getHeight());
            viewHolder.vUserDetails.setTranslationY(-viewHolder.vUserDetails.getHeight());
            viewHolder.vUserStats.setAlpha(0);

            viewHolder.vUserProfileRoot.animate().translationY(0).setDuration(300).setInterpolator(INTERPOLATOR);
            viewHolder.ivUserProfilePhoto.animate().translationY(0).setDuration(300).setStartDelay(100).setInterpolator(INTERPOLATOR);
            viewHolder.vUserDetails.animate().translationY(0).setDuration(300).setStartDelay(200).setInterpolator(INTERPOLATOR);
            viewHolder.vUserStats.animate().alpha(1).setDuration(200).setStartDelay(400).setInterpolator(INTERPOLATOR).start();
        }
    }




    private void animateUserProfileOptions(ProfileOptionsViewHolder viewHolder) {
        if (!lockedAnimations) {
            viewHolder.vButtons.setTranslationY(-viewHolder.vButtons.getHeight());
            viewHolder.vUnderline.setScaleX(0);

            viewHolder.vButtons.animate().translationY(0).setDuration(300).setStartDelay(USER_OPTIONS_ANIMATION_DELAY).setInterpolator(INTERPOLATOR);
            viewHolder.vUnderline.animate().scaleX(1).setDuration(200).setStartDelay(USER_OPTIONS_ANIMATION_DELAY + 300).setInterpolator(INTERPOLATOR).start();
        }
    }


    public UserProfileAdapter(Context context) {
        this.context = context;
        int width  = Resources.getSystem().getDisplayMetrics().widthPixels;
        this.cellSize = width / 3;
        this.photos = Arrays.asList(context.getResources().getStringArray(R.array.user_photos));
    }



    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
      ////  int viewType = getItemViewType(position);
        //        if (TYPE_PROFILE_HEADER == viewType) {
        //            bindProfileHeader((ProfileHeaderViewHolder) holder);
        //        } else if (TYPE_PROFILE_OPTIONS == viewType) {
        //            bindProfileOptions((ProfileOptionsViewHolder) holder);
        //        } else
        //


            bindPhoto((PhotoViewHolder) holder, position);

    }



    private void bindPhoto(final PhotoViewHolder holder, int position) {
        Picasso.get()
                .load(photos.get(position))
                .resize(cellSize, cellSize)
                .centerCrop()
                .into(holder.ivPhoto, new Callback() {
                    @Override
                    public void onSuccess() {
                        animatePhoto(holder);
                    }

                    @Override
                    public void onError(Exception e) {

                    }

                });
        if (lastAnimatedItem < position) lastAnimatedItem = position;
    }

    private void animatePhoto(PhotoViewHolder viewHolder) {
        if (!lockedAnimations) {
            if (lastAnimatedItem == viewHolder.getPosition()) {
                setLockedAnimations(true);
            }

            long animationDelay = PHOTO_ANIMATION_DELAY + viewHolder.getPosition() * 30;

            viewHolder.flRoot.setScaleY(0);
            viewHolder.flRoot.setScaleX(0);

            viewHolder.flRoot.animate()
                    .scaleY(1)
                    .scaleX(1)
                    .setDuration(200)
                    .setInterpolator(INTERPOLATOR)
                    .setStartDelay(animationDelay)
                    .start();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.item_photo, parent, false);
        StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
        layoutParams.height = cellSize;
        layoutParams.width = cellSize;
        layoutParams.setFullSpan(false);
        view.setLayoutParams(layoutParams);
        return new PhotoViewHolder(view);

    }


    @Override
    public int getItemCount() {
        return photos.size();
    }



    static class PhotoViewHolder extends RecyclerView.ViewHolder {
        FrameLayout flRoot;
        ImageView ivPhoto;

        public PhotoViewHolder(View view) {
            super(view);

            flRoot = view.findViewById(R.id.flRoot);
            ivPhoto = view.findViewById(R.id.ivPhoto);
        }
    }

    public void setLockedAnimations(boolean lockedAnimations) {
        this.lockedAnimations = lockedAnimations;
    }

*/