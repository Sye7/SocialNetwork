package com.example.socialnetwork;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.socialnetwork.fragment.CameraActivityFragment;
import com.example.socialnetwork.fragment.MainActivityFragment;
import com.example.socialnetwork.fragment.UserProfileFragment;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class MainTabActivity extends AppCompatActivity {

    boolean backPressToExit = false;
    // Up down gestures


    @Override
    public void onBackPressed() {


        if (backPressToExit) {

            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(a);
        }

        Snackbar.make(viewPager, "Back Again to Exit", Snackbar.LENGTH_SHORT).show();

        this.backPressToExit = true;
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                backPressToExit = false;
            }
        }, 2000);


    }

    ViewPager viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_tab);




        TabLayout tabLayout = findViewById(R.id.tabLayout);
         viewPager =  findViewById(R.id.viewPager);

        ViewPagerAdapter viewPagerAdapter  = new ViewPagerAdapter(getSupportFragmentManager());

        viewPagerAdapter.addFragment(new CameraActivityFragment(),"Camera");
        viewPagerAdapter.addFragment(new MainActivityFragment(),"Main Activity");
        viewPagerAdapter.addFragment(new UserProfileFragment(),"User Profile");


        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setCurrentItem(1);

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
