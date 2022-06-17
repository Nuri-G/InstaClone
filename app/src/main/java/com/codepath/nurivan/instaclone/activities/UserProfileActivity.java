package com.codepath.nurivan.instaclone.activities;

import static android.view.View.GONE;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.nurivan.instaclone.Post;
import com.codepath.nurivan.instaclone.PostAdapter;
import com.codepath.nurivan.instaclone.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class UserProfileActivity extends AppCompatActivity {
    private Button bLogout;
    private Button bUpdatePicture;
    private TextView tvProfileUsername;
    private ImageView ivProfilePicture;
    private RecyclerView rvPostList;
    private PostAdapter adapter;
    private List<Post> postList;

    private ParseUser currentUser;

    public static final String TAG = "UserProfileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_profile);

        Post p = getIntent().getParcelableExtra(Post.class.getSimpleName());
        
        currentUser = p.getUser();

        bLogout = findViewById(R.id.bLogout);
        tvProfileUsername = findViewById(R.id.tvProfileUsername);
        tvProfileUsername.setText(currentUser.getUsername());
        ivProfilePicture = findViewById(R.id.ivProfilePicture);
        bUpdatePicture = findViewById(R.id.bUpdatePicture);
        rvPostList = findViewById(R.id.rvPostList);

        bLogout.setVisibility(GONE);
        bUpdatePicture.setVisibility(GONE);

        postList = new ArrayList<>();
        adapter = new PostAdapter(this, postList, true);

        rvPostList.setLayoutManager(new GridLayoutManager(this, 3));
        rvPostList.setAdapter(adapter);

        getUserPosts();

        Glide.with(this)
                .load(currentUser.getParseFile("profilePicture").getUrl())
                .circleCrop()
                .into(ivProfilePicture);
    }

    private void getUserPosts() {
        postList.clear();
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.whereEqualTo(Post.KEY_USER, currentUser);
        query.include(Post.KEY_USER);
        query.orderByDescending(Post.KEY_CREATED_AT);
        query.setLimit(20);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if(e != null) {
                    Log.e(TAG, "Error getting posts", e);
                    return;
                }
                postList.addAll(posts);
                adapter.notifyDataSetChanged();
            }
        });
    }
}