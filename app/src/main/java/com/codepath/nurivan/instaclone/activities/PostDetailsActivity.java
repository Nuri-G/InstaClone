package com.codepath.nurivan.instaclone.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.nurivan.instaclone.Post;
import com.codepath.nurivan.instaclone.R;
import com.parse.ParseException;

public class PostDetailsActivity extends AppCompatActivity {
    Post post;
    Context context;

    TextView tvUser;
    TextView tvTimestamp;
    TextView tvDetails;
    ImageView ivBigImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        post = getIntent().getParcelableExtra(Post.class.getSimpleName());
        context = PostDetailsActivity.this;

        tvUser = findViewById(R.id.tvUser);
        tvTimestamp = findViewById(R.id.tvTimestamp);
        tvDetails = findViewById(R.id.tvDetails);
        ivBigImage = findViewById(R.id.ivBigImage);

        tvUser.setText(post.getUser().getUsername());
        tvTimestamp.setText(post.getTimeAgo());
        tvDetails.setText(post.getDescription());
        try {
            Glide.with(context)
                    .load(post.getImage().getFile())
                    .into(ivBigImage);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}