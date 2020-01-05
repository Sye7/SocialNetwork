package com.example.socialnetwork.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialnetwork.R;
import com.facebook.rebound.ui.Util;
import com.theophrast.ui.widget.SquareImageView;



public class FeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ANIMATED_ITEMS_COUNTS = 2;
    private Context context;
    private int lastAnimatedPosition = -1;
    private int itemsCount = 0;

    public FeedAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.item_feed,parent,false);
        return new CellFeedViewHolder(view);
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


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {

        runEnterAnimation(viewHolder.itemView,position);
        CellFeedViewHolder holder = (CellFeedViewHolder) viewHolder;
        if (position % 2 == 0) {
            holder.ivFeedCenter.setImageResource(R.mipmap.img_feed_center_1);
            holder.ivFeedBottom.setImageResource(R.mipmap.img_feed_bottom_1);
        } else {
            holder.ivFeedCenter.setImageResource(R.mipmap.img_feed_center_2);
            holder.ivFeedBottom.setImageResource(R.mipmap.img_feed_bottom_2);
        }

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

    // Feed Menu in comment section

    private OnFeedItemClickListener onFeedItemClickListener;

    public void setOnFeedItemClickListener(OnFeedItemClickListener onFeedItemClickListener) {
        this.onFeedItemClickListener = onFeedItemClickListener;
    }

    public interface OnFeedItemClickListener {
        public void onCommentsClick(View v, int position);

        public void onMoreClick(View v, int position);
    }



    @Override
    public int getItemCount() {
        return itemsCount;
    }

    public static class CellFeedViewHolder extends RecyclerView.ViewHolder{


        ImageView ivFeedBottom;
        SquareImageView ivFeedCenter;
        ImageButton btnMore;
        ImageButton btnComment;


        public CellFeedViewHolder(@NonNull View itemView) {
            super(itemView);

            ivFeedBottom = itemView.findViewById(R.id.ivFeedBottom);
            ivFeedCenter = itemView.findViewById(R.id.ivFeedCenter);
            btnMore = itemView.findViewById(R.id.btnMore);
            btnComment = itemView.findViewById(R.id.btnComments);

        }
    }

    public void updateItems(){
        itemsCount = 10;
        notifyDataSetChanged();
    }
}
