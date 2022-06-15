package com.codepath.nurivan.instaclone.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.MenuItem;

import com.codepath.nurivan.instaclone.R;
import com.codepath.nurivan.instaclone.fragments.FeedFragment;
import com.codepath.nurivan.instaclone.fragments.PostFragment;
import com.codepath.nurivan.instaclone.fragments.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final FragmentManager fragmentManager = getSupportFragmentManager();

        // define your fragments here
        final Fragment postFragment = new PostFragment();
        final Fragment feedFragment = new FeedFragment();
        final Fragment profileFragment = new ProfileFragment();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;

                switch (item.getItemId()) {
                    case R.id.action_feed:
                        fragment = feedFragment;
                        break;
                    case R.id.action_post:
                        fragment = postFragment;
                        break;
                    case R.id.action_profile:
                        fragment = profileFragment;
                        break;
                    default: return true;
                }

                fragmentManager.beginTransaction().replace(R.id.fragment_holder, fragment).commit();
                return true;
            }
        });

        bottomNavigationView.setSelectedItemId(R.id.action_post);
    }
}