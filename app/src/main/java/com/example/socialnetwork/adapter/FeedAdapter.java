package com.example.socialnetwork.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextSwitcher;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialnetwork.CircularImage.CircleTransformation;
import com.example.socialnetwork.DoubleClickListener;
import com.example.socialnetwork.R;
import com.example.socialnetwork.model.Post;
import com.squareup.picasso.Picasso;
import com.theophrast.ui.widget.SquareImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class FeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // red like button
    private static final AccelerateInterpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();
    private static final OvershootInterpolator OVERSHOOT_INTERPOLATOR = new OvershootInterpolator(4);

    private static final DecelerateInterpolator  DECCELERATE_INTERPOLATOR = new DecelerateInterpolator();
    private final Map<RecyclerView.ViewHolder, AnimatorSet> likeAnimations = new HashMap<>();
    private final ArrayList<Integer> likedPositions = new ArrayList<>();



    // like anim
    private final Map<Integer, Integer> likesCount = new HashMap<>();

    List<Post> postList;

    private static final int ANIMATED_ITEMS_COUNTS = 2;
    private Context context;
    private int lastAnimatedPosition = -1;
    private int itemsCount = 0;

    public FeedAdapter(List<Post> postList, Context context)
    {
        this.postList = postList;
        this.context = context;
    }

    public FeedAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.item_feed, parent,false);
        return new CellFeedViewHolder(view);
    }

    // Red Like Button

    private void updateHeartButton(final CellFeedViewHolder holder, boolean animated) {
        if (animated) {
            if (!likeAnimations.containsKey(holder)) {
                AnimatorSet animatorSet = new AnimatorSet();
                likeAnimations.put(holder, animatorSet);

                ObjectAnimator rotationAnim = ObjectAnimator.ofFloat(holder.btnLike, "rotation", 0f, 360f);
                rotationAnim.setDuration(500);
                rotationAnim.setInterpolator(ACCELERATE_INTERPOLATOR);

                ObjectAnimator bounceAnimX = ObjectAnimator.ofFloat(holder.btnLike, "scaleX", 0.2f, 1f);
                bounceAnimX.setDuration(600);
                bounceAnimX.setInterpolator(OVERSHOOT_INTERPOLATOR);

                ObjectAnimator bounceAnimY = ObjectAnimator.ofFloat(holder.btnLike, "scaleY", 0.2f, 1f);
                bounceAnimY.setDuration(600);
                bounceAnimY.setInterpolator(OVERSHOOT_INTERPOLATOR);
                bounceAnimY.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        holder.btnLike.setImageResource(R.drawable.ic_heart_red);
                    }
                });


                // Play animation

                animatorSet.play(rotationAnim);
                animatorSet.play(bounceAnimX).with(bounceAnimY).after(rotationAnim);

                animatorSet.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        resetLikeAnimationState(holder);
                    }
                });

                animatorSet.start();
            }
        } else {
            if (likedPositions.contains(holder.getPosition())) {
                holder.btnLike.setImageResource(R.drawable.ic_heart_red);
            } else {
                holder.btnLike.setImageResource(R.drawable.ic_heart_outline_grey);
            }
        }
    }



// center grey like by double click

    private void animatePhotoLike(final CellFeedViewHolder holder) {
        if (!likeAnimations.containsKey(holder)) {
            holder.vBgLike.setVisibility(View.VISIBLE);
            holder.ivLike.setVisibility(View.VISIBLE);


            holder.vBgLike.setScaleY(0.1f);
            holder.vBgLike.setScaleX(0.1f);
            holder.vBgLike.setAlpha(1f);
            holder.ivLike.setScaleY(0.1f);
            holder.ivLike.setScaleX(0.1f);

            AnimatorSet animatorSet = new AnimatorSet();
            likeAnimations.put(holder, animatorSet);

            ObjectAnimator bgScaleYAnim = ObjectAnimator.ofFloat(holder.vBgLike, "scaleY", 0.1f, 1f);
            bgScaleYAnim.setDuration(400);
            bgScaleYAnim.setInterpolator(DECCELERATE_INTERPOLATOR);
            ObjectAnimator bgScaleXAnim = ObjectAnimator.ofFloat(holder.vBgLike, "scaleX", 0.1f, 1f);
            bgScaleXAnim.setDuration(400);
            bgScaleXAnim.setInterpolator(DECCELERATE_INTERPOLATOR);
            ObjectAnimator bgAlphaAnim = ObjectAnimator.ofFloat(holder.vBgLike, "alpha", 1f, 0f);
            bgAlphaAnim.setDuration(400);
            bgAlphaAnim.setStartDelay(400);
            bgAlphaAnim.setInterpolator(DECCELERATE_INTERPOLATOR);

            ObjectAnimator imgScaleUpYAnim = ObjectAnimator.ofFloat(holder.ivLike, "scaleY", 0.1f, 1f);
            imgScaleUpYAnim.setDuration(400);
            imgScaleUpYAnim.setInterpolator(DECCELERATE_INTERPOLATOR);
            ObjectAnimator imgScaleUpXAnim = ObjectAnimator.ofFloat(holder.ivLike, "scaleX", 0.1f, 1f);
            imgScaleUpXAnim.setDuration(400);
            imgScaleUpXAnim.setInterpolator(DECCELERATE_INTERPOLATOR);

            ObjectAnimator imgScaleDownYAnim = ObjectAnimator.ofFloat(holder.ivLike, "scaleY", 1f, 0f);
            imgScaleDownYAnim.setDuration(400);
            imgScaleDownYAnim.setInterpolator(ACCELERATE_INTERPOLATOR);
            ObjectAnimator imgScaleDownXAnim = ObjectAnimator.ofFloat(holder.ivLike, "scaleX", 1f, 0f);
            imgScaleDownXAnim.setDuration(400);
            imgScaleDownXAnim.setInterpolator(ACCELERATE_INTERPOLATOR);

            animatorSet.playTogether(bgScaleYAnim, bgScaleXAnim, bgAlphaAnim, imgScaleUpYAnim, imgScaleUpXAnim);
            animatorSet.play(imgScaleDownYAnim).with(imgScaleDownXAnim).after(imgScaleUpYAnim);

            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    resetLikeAnimationState(holder);
                }
            });
            animatorSet.start();
        }
    }


    private void updateLikesCounter(CellFeedViewHolder holder, boolean animated) {
        int currentLikesCount = likesCount.get(holder.getPosition()) + 1;
        String likesCountText = context.getResources().getQuantityString(
                R.plurals.likes_count, currentLikesCount, currentLikesCount
        );

        if (animated) {
            holder.tsLikesCounter.setText(likesCountText);
        } else {
            holder.tsLikesCounter.setCurrentText(likesCountText);
        }

        likesCount.put(holder.getPosition(), currentLikesCount);
    }



    private void runEnterAnimation(View view, int position)
    {
        if(position >= ANIMATED_ITEMS_COUNTS-1)
                return;

        if(position > lastAnimatedPosition)
        {
            lastAnimatedPosition = position;

            view.setTranslationY((float)Resources.getSystem().getDisplayMetrics().heightPixels);

            view.animate()
                    .translationY(0)
                    .setInterpolator(new DecelerateInterpolator(3.f))
                    .setDuration(800)
                    .start();
        }
    }


    public void updatedDp(String pic, ImageView view){


        Picasso.get()
                .load(pic)
                .placeholder(R.drawable.img_circle_placeholder)
                .resize(88, 88)
                .centerCrop()
                .transform(new CircleTransformation())
                .into(view);
    }

    public void updatedFeedPhoto(String pic, ImageView view){


        Picasso.get()
                .load(pic)
                .resize(1080, 810)
                .centerInside()
                .into(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {


        runEnterAnimation(viewHolder.itemView,position);
        final CellFeedViewHolder holder = (CellFeedViewHolder) viewHolder;

        Post post = postList.get(position);
        holder.tsLikesCounter.setText(post.getLikes()+"");
        holder.tvFeedBottom.setText(post.getCaption());
        holder.tvnameFeed.setText(post.getUserName());
        updatedDp(post.getDp(), holder.ivDpFeed);
        updatedFeedPhoto(post.getPhoto(), holder.ivFeedCenter);

/*
        if (position % 2 == 0) {
            holder.ivFeedCenter.setImageResource(R.mipmap.img_feed_center_1);
            holder.ivFeedBottom.setImageResource(R.mipmap.img_feed_bottom_1);
        } else {
            holder.ivFeedCenter.setImageResource(R.mipmap.img_feed_center_2);
            holder.ivFeedBottom.setImageResource(R.mipmap.img_feed_bottom_2);
        }

 */


        // like anim
       // updateLikesCounter(holder, false);

        // red like button anim
        updateHeartButton(holder, false);

        // Profile Screen

        holder.ivUserProfile.setTag(holder);
        holder.ivUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (onFeedItemClickListener != null) {
                    onFeedItemClickListener.onProfileClick(v);
                }
            }
        });




        holder.ivFeedCenter.setTag(holder);
        holder.ivFeedCenter.setOnClickListener(new DoubleClickListener() {
            @Override
            public void onSingleClick(View v) {

            }

            @Override
            public void onDoubleClick(View v) {

                CellFeedViewHolder holder = (CellFeedViewHolder) v.getTag();
                // center like btn grey
                animatePhotoLike(holder);
                updateLikesCounter(holder, true);
                holder.btnLike.setImageResource(R.drawable.ic_heart_red);


            }
        });


        holder.btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CellFeedViewHolder holder = (CellFeedViewHolder) v.getTag();
                if (!likedPositions.contains(holder.getPosition())) {
                    likedPositions.add(holder.getPosition());
                 //   updateLikesCounter(holder, true);
                    updateHeartButton(holder, true);
                    updateLikesCounter(holder, true);

                }
            }
        });

        holder.btnLike.setTag(holder);

        if (likeAnimations.containsKey(holder)) {
            likeAnimations.get(holder).cancel();
        }
        resetLikeAnimationState(holder);

        // red like btn anim end

        holder.btnComment.setTag(position);

        holder.btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (v.getId() == R.id.btnComments) {
                    if (onFeedItemClickListener != null) {
                        onFeedItemClickListener.onCommentsClick(v, (Integer) v.getTag());
                    }
                }

            }
        });


        // Feed menu
        holder.btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (v.getId() == R.id.btnMore) {
                    if (onFeedItemClickListener != null) {
                        onFeedItemClickListener.onMoreClick(v, (Integer) v.getTag());
                    }
                }

            }
        });
        holder.btnMore.setTag(position);

    }

    // red like button anim

    private void resetLikeAnimationState(CellFeedViewHolder holder) {
        likeAnimations.remove(holder);
        holder.vBgLike.setVisibility(View.GONE);
        holder.ivLike.setVisibility(View.GONE);
    }

    // Feed Menu in comment section

    private OnFeedItemClickListener onFeedItemClickListener;

    public void setOnFeedItemClickListener(OnFeedItemClickListener onFeedItemClickListener) {

        this.onFeedItemClickListener = onFeedItemClickListener;
    }

    public interface OnFeedItemClickListener {
        public void onCommentsClick(View v, int position);

        public void onMoreClick(View v, int position);

        public void onProfileClick(View v);
    }



    @Override
    public int getItemCount() {
        return postList.size();
    }


    // class

    public static class CellFeedViewHolder extends RecyclerView.ViewHolder{



        TextView tvFeedBottom;
        SquareImageView ivFeedCenter;
        ImageButton btnMore;
        ImageButton btnComment;
        ImageButton btnLike;
        ImageView ivDpFeed;
        TextView tvnameFeed;


        View vBgLike;
        ImageView ivLike;
        TextSwitcher tsLikesCounter;
        ImageView ivUserProfile;



        public CellFeedViewHolder(@NonNull View itemView) {
            super(itemView);

            tvFeedBottom = itemView.findViewById(R.id.ivFeedBottom);

            ivFeedCenter = itemView.findViewById(R.id.ivFeedCenter);

            btnMore = itemView.findViewById(R.id.btnMore);
            btnComment = itemView.findViewById(R.id.btnComments);
            btnLike = itemView.findViewById(R.id.btnLike);

            vBgLike = itemView.findViewById(R.id.vBgLike);
            tsLikesCounter = itemView.findViewById(R.id.tsLikesCounter);
            ivLike = itemView.findViewById(R.id.ivLike);
            ivDpFeed = itemView.findViewById(R.id.ivDpFeed);
            tvnameFeed = itemView.findViewById(R.id.tvUSerNameFeed);
            ivUserProfile = itemView.findViewById(R.id.profileHeart);


        }
    }

    public void updateItems(){
        itemsCount = 10;
        notifyDataSetChanged();

        // like anim
        fillLikesWithRandomValues();
    }
    private void fillLikesWithRandomValues() {
        for (int i = 0; i < getItemCount(); i++) {
            likesCount.put(i, new Random().nextInt(100));
        }
    }

}
