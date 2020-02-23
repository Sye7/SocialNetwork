package com.example.socialnetwork;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialnetwork.model.Profile;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class SearchUsers extends AppCompatActivity {


    private EditText mSearchField;
    private ImageButton mSearchBtn;

    private RecyclerView mResultList;

    private DatabaseReference mUserDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_users);

        mUserDatabase = FirebaseDatabase.getInstance().getReference("Profile");


        mSearchField = (EditText) findViewById(R.id.search_field);
        mSearchBtn = (ImageButton) findViewById(R.id.search_btn);

        mResultList = (RecyclerView) findViewById(R.id.result_list);
        mResultList.setHasFixedSize(true);
        mResultList.setLayoutManager(new LinearLayoutManager(this));

        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String searchText = mSearchField.getText().toString();

                firebaseUserSearch(searchText);

            }
        });

    }

    private void firebaseUserSearch(String searchText) {


        Query firebaseSearchQuery = mUserDatabase.orderByChild("userName").startAt(searchText).endAt(searchText + "\uf8ff");

        FirebaseRecyclerAdapter<Profile, UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Profile, UsersViewHolder>(

                Profile.class,
                R.layout.search_users_content,
                UsersViewHolder.class,
                firebaseSearchQuery

        ) {
            @Override
            protected void populateViewHolder(UsersViewHolder usersViewHolder, Profile profile, int i) {
                if(profile.getId().equals(FirebaseAuth.getInstance().getUid()))
                {
                    usersViewHolder.setDetails(getApplicationContext(), profile.getUserName()+" (You)", profile.getOccupation(), profile.getDp());
                    return;
                }

                usersViewHolder.setDetails(getApplicationContext(), profile.getUserName(), profile.getOccupation(), profile.getDp());

            }

        };

        mResultList.setAdapter(firebaseRecyclerAdapter);

    }


    // View Holder Class

    public static class UsersViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public UsersViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

        }

        public void setDetails(final Context ctx, final String userName, String userStatus, final String userImage){

            final TextView user_name = (TextView) mView.findViewById(R.id.name_text);
            TextView user_status = (TextView) mView.findViewById(R.id.status_text);
            ImageView user_image = (ImageView) mView.findViewById(R.id.profile_image);
            RelativeLayout rlSearch = mView.findViewById(R.id.rlSearch);



            user_name.setText(userName);
            user_status.setText(userStatus);

            Glide.with(ctx).load(userImage).into(user_image);

            rlSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(ctx, OtherUserProfile.class);
                    intent.putExtra("userName",userName);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    ctx.startActivity(intent);
                }
            });



        }




    }

}
