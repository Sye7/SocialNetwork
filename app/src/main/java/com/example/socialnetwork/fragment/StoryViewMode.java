package com.example.socialnetwork.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.socialnetwork.R;

import jp.shts.android.storiesprogressview.StoriesProgressView;

public class StoryViewMode extends Fragment implements StoriesProgressView.StoriesListener {


    private static final int PROGRESS_COUNT = 5;

    private StoriesProgressView storiesProgressView;
    private ImageView image;

    private int counter = 0;
    private final int[] resources = new int[]{
            R.drawable.misa,
            R.drawable.itachi,
            R.drawable.madara,
            R.drawable.itachi2,
            R.drawable.sample1,
    };

    private final long[] durations = new long[]{
            500L, 1000L, 1500L, 4000L, 5000L
    };

    long pressTime = 0L;
    long limit = 500L;

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    pressTime = System.currentTimeMillis();
                    storiesProgressView.pause();
                    return false;
                case MotionEvent.ACTION_UP:
                    long now = System.currentTimeMillis();
                    storiesProgressView.resume();
                    return limit < now - pressTime;
            }
            return false;
        }
    };




    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        storiesProgressView = (StoriesProgressView) view.findViewById(R.id.frstories);
        storiesProgressView.setStoriesCount(PROGRESS_COUNT);
        storiesProgressView.setStoryDuration(3000L);
        // or
        // storiesProgressView.setStoriesCountWithDurations(durations);
        storiesProgressView.setStoriesListener(this);
//        storiesProgressView.startStories();
        counter = 0;
        storiesProgressView.startStories(counter);

        image = (ImageView) view.findViewById(R.id.frimage);
        image.setImageResource(resources[counter]);

        // bind reverse view
        View reverse = view.findViewById(R.id.frreverse);
        reverse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storiesProgressView.reverse();
            }
        });
        reverse.setOnTouchListener(onTouchListener);

        // bind skip view
        View skip = view.findViewById(R.id.frskip);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storiesProgressView.skip();
            }
        });
        skip.setOnTouchListener(onTouchListener);



    }

    @Nullable
    @Override
    public View onCreateView( LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_story_view_mode, container, false);

    }

    @Override
    public void onNext() {
        image.setImageResource(resources[++counter]);

    }

    @Override
    public void onPrev() {

        if ((counter - 1) < 0) return;
        image.setImageResource(resources[--counter]);

    }

    @Override
    public void onComplete() {

    }

    @Override
    public void onDestroy() {
        // Very important !
        storiesProgressView.destroy();
        super.onDestroy();
    }
}
