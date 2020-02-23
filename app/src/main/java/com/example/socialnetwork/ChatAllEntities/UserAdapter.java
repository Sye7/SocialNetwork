package com.example.socialnetwork.ChatAllEntities;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialnetwork.R;
import com.example.socialnetwork.model.Profile;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;


public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context mContext;
    private List<Profile> mUsers;

    public UserAdapter(Context mContext, List<Profile> mUsers){
        this.mUsers = mUsers;
        this.mContext = mContext;

    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item, viewGroup, false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {

        final Profile user = mUsers.get(i);
        viewHolder.username.setText(user.getUserName());
        if(user.getDp().equals("default")){
            viewHolder.profile_image.setImageResource(R.drawable.app_icon);

        }
        else
        {
            Picasso.get()
                    .load(user.getDp())
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
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MessageActivity.class);
                intent.putExtra("userid",user.getId());
                mContext.startActivity(intent);
            }
        });


    }
    public class ViewHolder extends RecyclerView.ViewHolder{

        public  TextView username;
        public  ImageView profile_image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username =itemView.findViewById(R.id.usernamet);
            profile_image = itemView.findViewById(R.id.profile_imaget);

        }

    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }



}