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
import com.example.socialnetwork.model.Story;

import java.util.List;

public class StoryAdapter extends RecyclerView.Adapter  {

    public interface OnItemClickListener {
        void onItemClick(Story story);
    }

    private final OnItemClickListener listener;


    List<Story> storyList;
    private Context context;




    public StoryAdapter(List<Story> storyList, Context context, OnItemClickListener listener)
    {
        this.storyList = storyList;
        this.context = context;
        this.listener = listener;
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

        holderStory.bind(storyList.get(position), listener, context);


    }

    @Override
    public int getItemCount() {
        return storyList.size();
    }

     static class StoryFeedViewHolder extends RecyclerView.ViewHolder
    {

        ImageView ivStoryPic;
        TextView tvName;

        public void bind(final Story story, final OnItemClickListener listener, Context context) {


            tvName.setText(story.getName());
            Glide.with(context)
                    .load(story.getDp())
                    .placeholder(R.drawable.img_circle_placeholder)
                    .into(ivStoryPic);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(story);
                }
            });
        }


        public StoryFeedViewHolder(@NonNull View itemView) {
            super(itemView);

            ivStoryPic = itemView.findViewById(R.id.ivStoryContent);
            tvName = itemView.findViewById(R.id.tvStoryHolderName);
        }


    }
}
