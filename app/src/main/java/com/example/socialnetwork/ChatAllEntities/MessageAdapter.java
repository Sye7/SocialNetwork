package com.example.socialnetwork.ChatAllEntities;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialnetwork.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private Context mContext;

    public static final int  MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    private List<Chat> mChat;
    private String imageUrl;

    FirebaseUser firebaseUser;



    public MessageAdapter(Context mContext, List<Chat> mChat, String imageUrl){
        this.mChat = mChat;
        this.mContext = mContext;
        this.imageUrl = imageUrl;


    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        if(viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, viewGroup, false);
            return new MessageAdapter.ViewHolder(view);
        }
        else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, viewGroup, false);
            return new MessageAdapter.ViewHolder(view);

        }
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {

        Chat chat = mChat.get(i);
        viewHolder.show_message.setText(chat.getMessage());
        if(imageUrl.equals("default")){
            viewHolder.profile_image.setImageResource(R.drawable.app_icon);

        }
        else {
            Picasso.get()
                    .load(imageUrl)
                    .centerCrop()
                    .resize(88, 88)
                    .into(viewHolder.profile_image, new Callback() {
                        @Override
                        public void onSuccess() {
                            viewHolder.profile_image.animate()
                                    .scaleX(1.f).scaleY(1.f)
                                    .setInterpolator(new OvershootInterpolator())
                                    .setDuration(400)
                                    .setStartDelay(200)
                                    .start();
                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });

        }


    }
    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView show_message;
        public ImageView profile_image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            show_message =itemView.findViewById(R.id.show_message);
            profile_image = itemView.findViewById(R.id.profile_image);

        }

    }



    @Override
    public int getItemCount() {
        return mChat.size();
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if(mChat.get(position).getSender().equals(firebaseUser.getUid())){
            return MSG_TYPE_RIGHT;
        }
        else {
            return MSG_TYPE_LEFT;
        }
    }
}