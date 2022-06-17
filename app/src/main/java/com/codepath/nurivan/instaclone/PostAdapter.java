package com.codepath.nurivan.instaclone;

import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.nurivan.instaclone.activities.PostDetailsActivity;
import com.parse.ParseException;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    Context context;
    List<Post> posts;
    boolean pictureOnly;

    public PostAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
        this.pictureOnly = false;
    }

    public PostAdapter(Context context, List<Post> posts, boolean pictureOnly) {
        this.context = context;
        this.posts = posts;
        this.pictureOnly = pictureOnly;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view, pictureOnly);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivFeedPost;
        TextView tvDescription;
        TextView tvUsername;
        TextView tvFeedTimestamp;
        ImageView ivProfileCircle;
        ImageButton ibLike;
        TextView tvLikes;
        TextView tvLikeLabel;

        boolean pictureOnly;
        boolean liked;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivFeedPost = itemView.findViewById(R.id.ivFeedPost);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvFeedTimestamp = itemView.findViewById(R.id.tvFeedTimestamp);
            ivProfileCircle = itemView.findViewById(R.id.ivProfileCircle);
            ibLike = itemView.findViewById(R.id.ibLike);
            tvLikes = itemView.findViewById(R.id.tvLikes);
            tvLikeLabel = itemView.findViewById(R.id.tvLikeLabel);
            this.pictureOnly = false;
            this.liked = false;
        }

        public ViewHolder(@NonNull View itemView, boolean pictureOnly) {
            this(itemView);
            this.pictureOnly = pictureOnly;
        }

        public void bind(Post post) {
            if(!pictureOnly) {
                tvDescription.setText(post.getDescription());
                tvUsername.setText(post.getUser().getUsername());
                tvFeedTimestamp.setText(post.getTimeAgo());

            } else {
                tvDescription.setVisibility(View.GONE);
                tvUsername.setVisibility(View.GONE);
                tvFeedTimestamp.setVisibility(View.GONE);
                ivProfileCircle.setVisibility(View.GONE);
                ibLike.setVisibility(View.GONE);
                tvLikeLabel.setVisibility(View.GONE);

            }

            tvLikes.setText(post.getLikes().toString());

            ivFeedPost.setOnClickListener(e -> {
                // create intent for the new activity
                Intent intent = new Intent(context, PostDetailsActivity.class);
                // serialize the movie using parceler, use its short name as a key
                intent.putExtra(Post.class.getSimpleName(), post);
                // show the activity
                context.startActivity(intent);
            });

            JSONArray likedUsers = post.getLikedUsers();

            for(int i = 0; i < likedUsers.length(); i++) {
                String like = "";
                try {
                    like = likedUsers.getString(i);
                    if(like.equals(ParseUser.getCurrentUser().getObjectId())) {
                        liked = true;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            setLike();



            ibLike.setOnClickListener(e -> {
                liked = !liked;

                setLike();

                if(liked) {
                    post.addLike(ParseUser.getCurrentUser());
                } else {
                    post.decrementLikes(ParseUser.getCurrentUser());
                }

                tvLikes.setText(post.getLikes().toString());

                post.saveInBackground(err -> {
                    if(err != null) {
                        Log.e("", "Failed to save post",  err);
                    }
                    if(err == null)
                        Log.i("ViewHolder", "Saved like.");
                });

            });

            try {
                Glide.with(context)
                        .load(post.getImage().getFile())
                        .into(ivFeedPost);

                if(!pictureOnly) {
                    Glide.with(context)
                            .load(post.getUser().getParseFile("profilePicture").getUrl())
                            .circleCrop()
                            .into(ivProfileCircle);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        private void setLike() {
            if(liked) {
                ibLike.setImageResource(R.drawable.ufi_heart_active);
            } else {
                ibLike.setImageResource(R.drawable.ufi_heart);
            }
        }
    }
}
