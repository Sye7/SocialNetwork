package com.example.socialnetwork.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialnetwork.R;
import com.example.socialnetwork.model.Profile;

import java.util.List;

public class FollowerANdFollowingAdapter extends RecyclerView.Adapter {

    public interface OnItemClickListener {
        void onItemClick(Profile profile);
    }

    private FollowerANdFollowingAdapter.OnItemClickListener listener;


    public void setClickListener(OnItemClickListener itemClickListener) {
        this.listener = itemClickListener;
    }



    List<Profile> profile;
    private Context context;

    public FollowerANdFollowingAdapter(  List<Profile> profile, Context context, FollowerANdFollowingAdapter.OnItemClickListener listener)
    {
        this.profile = profile;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.search_users_content, parent,false);
        return new FollowerANdFollowingAdapter.StoryFeedViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {



        final FollowerANdFollowingAdapter.StoryFeedViewHolder holderStory = (FollowerANdFollowingAdapter.StoryFeedViewHolder) holder;

        holderStory.bind(profile.get(position), listener, context);


    }

    @Override
    public int getItemCount() {
        return profile.size();
    }

    // allows clicks events to be caught




    static class StoryFeedViewHolder extends RecyclerView.ViewHolder
    {

        ImageView ivStoryPic;
        TextView tvName;
        TextView occupatation;

        public void bind(final Profile profile, final FollowerANdFollowingAdapter.OnItemClickListener listener, Context context) {


            tvName.setText(profile.getUserName());
            Glide.with(context)
                    .load(profile.getDp())
                    .placeholder(R.drawable.img_circle_placeholder)
                    .into(ivStoryPic);
            occupatation.setText(profile.getOccupation());


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(profile);
                }
            });
        }


        public StoryFeedViewHolder(@NonNull View itemView) {
            super(itemView);

            ivStoryPic = itemView.findViewById(R.id.profile_image);
            tvName = itemView.findViewById(R.id.name_text);
            occupatation = itemView.findViewById(R.id.status_text);

        }


    }


}
