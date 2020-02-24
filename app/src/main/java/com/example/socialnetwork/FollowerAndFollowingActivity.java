package com.example.socialnetwork;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialnetwork.adapter.FollowerANdFollowingAdapter;
import com.example.socialnetwork.model.Profile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FollowerAndFollowingActivity extends AppCompatActivity implements FollowerANdFollowingAdapter.OnItemClickListener {

    DatabaseReference reference = null;
    String userId;
    ArrayList<Profile> statusList;
    RecyclerView recyclerView;
    String status;
    TextView headLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follower_and_following);

        Intent intent = getIntent();
        status = intent.getStringExtra("followOrFollowing");

        headLabel = findViewById(R.id.heading_label);

        if (status.equals("follower")) {
            headLabel.setText("Followers");
        } else if (status.equals("following")) {
            headLabel.setText("Following");

        }

        userId = FirebaseAuth.getInstance().getUid();

        statusList = new ArrayList<>();
        recyclerView = findViewById(R.id.result_list);
        setupFeed();


        getFollowersAndFollowing();

    }

    FollowerANdFollowingAdapter adapter;

    private void setupFeed() {

        //Increase the amount of extra space that should be laid out by LayoutManager.

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        //feedAdapter = new FeedAdapter(this);
        adapter = new FollowerANdFollowingAdapter(statusList, this, this);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
    }


    @Override
    public void onItemClick(Profile profile) {

        Intent intent = new Intent(this, OtherUserProfile.class);
        intent.putExtra("userName",profile.getUserName());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }

    public void getFollowersAndFollowing() {
        if (status.equals("follower")) {
            reference = FirebaseDatabase.getInstance().getReference("Followers").child(userId);
        } else if (status.equals("following")) {
            reference = FirebaseDatabase.getInstance().getReference("Following").child(userId);

        }
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot child : dataSnapshot.getChildren()) {

                    Profile profile = child.getValue(Profile.class);
                    String key = child.getKey();


                    statusList.add(profile);
                    System.out.println("yasir " + profile.getUserName());
                    System.out.println("yasir " + key);
                    adapter.notifyDataSetChanged();


                    //fun(key);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

}
