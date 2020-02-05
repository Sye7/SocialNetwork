package com.example.socialnetwork.ChatAllEntities;

import android.os.Bundle;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.socialnetwork.R;
import com.example.socialnetwork.model.Profile;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class ChatActivity extends AppCompatActivity {

    ImageView profile_image;
    DatabaseReference reference;

    FirebaseUser  firebaseUser;
    TextView username;



    public void getData() {

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Profile");


        Query query = reference.orderByChild("id").equalTo(firebaseUser.getUid());

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Profile user = snapshot.getValue(Profile.class);


                    if (user.getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {


                        // If already present
                        username.setText(user.getUserName());

                        if(user.getDp().equals("default")){
                            profile_image.setImageResource(R.drawable.app_icon);

                        }
                        else {

                            Picasso.get()
                                    .load(user.getDp())
                                    .centerCrop()
                                    .resize(88, 88)
                                    .into(profile_image, new Callback() {
                                        @Override
                                        public void onSuccess() {
                                            profile_image.animate()
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

                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Toolbar toolbar = findViewById(R.id.toolbar_chat);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("DM's");

        profile_image = findViewById(R.id.profile_image_toolbar);
        username = findViewById(R.id.username_toolbars);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference().child("Profile");
        getData();


        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager viewPager =  findViewById(R.id.viewPager);

        ViewPagerAdapter viewPagerAdapter  = new ViewPagerAdapter(getSupportFragmentManager());

        viewPagerAdapter.addFragment(new ChatsFragment(),"Chats");
        viewPagerAdapter.addFragment(new UsersFragment(),"All Users");


        viewPager.setAdapter(viewPagerAdapter);

        tabLayout.setupWithViewPager(viewPager);
    }
    class ViewPagerAdapter extends FragmentPagerAdapter {

        public ArrayList<Fragment> fragments;
        public ArrayList<String> titles;

        ViewPagerAdapter(FragmentManager fm){
            super(fm);
            this.fragments= new ArrayList<>();
            this.titles = new ArrayList<>();

        }
        @Override
        public Fragment getItem(int i) {
            return fragments.get(i);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public void addFragment(Fragment fragment,String title){
            fragments.add(fragment);
            titles.add(title);

        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }

}

