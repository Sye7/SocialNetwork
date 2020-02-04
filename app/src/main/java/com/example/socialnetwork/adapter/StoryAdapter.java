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
import com.example.socialnetwork.MainActivity;
import com.example.socialnetwork.R;
import com.example.socialnetwork.model.Story;

import java.util.List;

public class StoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {


    List<Story> storyList;
    private Context context;


    public StoryAdapter(List<Story> storyList, Context context)
    {
        this.storyList = storyList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.storyc_content, parent,false);
        return new StoryAdapter.StoryFeedViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        final StoryAdapter.StoryFeedViewHolder holderStory = (StoryAdapter.StoryFeedViewHolder) holder;

        final Story story = storyList.get(position);

        holderStory.tvName.setText(story.getName());
        Glide.with(context)
                .load(story.getDp())
                .placeholder(R.drawable.img_circle_placeholder)
                .into(((StoryFeedViewHolder) holder).ivStoryPic);

     /*   Picasso.get()
                .load(story.getDp())
                .placeholder(R.drawable.img_circle_placeholder)
                .transform(new CircleTransformation())
                .into(holderStory.ivStoryPic);


      */

        holderStory.ivStoryPic.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {

               MainActivity.storyId = story.getId();

                return false;
            }


        });

       /* Picasso.get()
                .load(story.getDp())
                .placeholder(R.drawable.img_circle_placeholder)
                .resize(80, 80)
                .centerCrop()
                .transform(new CircleTransformation())
                .into(holderStory.ivStoryPic);


        */



    }

    @Override
    public int getItemCount() {
        return storyList.size();
    }

    public static class StoryFeedViewHolder extends RecyclerView.ViewHolder
    {

        ImageView ivStoryPic;
        TextView tvName;


        public StoryFeedViewHolder(@NonNull View itemView) {
            super(itemView);

            ivStoryPic = itemView.findViewById(R.id.ivStoryContent);
            tvName = itemView.findViewById(R.id.tvStoryHolderName);
        }
    }
}
