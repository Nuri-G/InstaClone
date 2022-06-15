package com.codepath.nurivan.instaclone;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.nurivan.instaclone.activities.PostDetailsActivity;
import com.parse.ParseException;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    Context context;
    List<Post> posts;

    public PostAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView ivFeedPost;
        TextView tvDescription;
        TextView tvUsername;
        TextView tvFeedTimestamp;
        ImageView ivProfileCircle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivFeedPost = itemView.findViewById(R.id.ivFeedPost);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvFeedTimestamp = itemView.findViewById(R.id.tvFeedTimestamp);
            ivProfileCircle = itemView.findViewById(R.id.ivProfileCircle);
            itemView.setOnClickListener(this);
        }

        public void bind(Post post) {
            tvDescription.setText(post.getDescription());
            tvUsername.setText(post.getUser().getUsername());
            tvFeedTimestamp.setText(post.getTimeAgo());

            try {
                Glide.with(context)
                        .load(post.getImage().getFile())
                        .into(ivFeedPost);
                Glide.with(context)
                        .load(post.getUser().getParseFile("profilePicture").getUrl())
                        .circleCrop()
                        .into(ivProfileCircle);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onClick(View v) {
            // gets item position
            int position = getAdapterPosition();
            // make sure the position is valid, i.e. actually exists in the view
            if (position != RecyclerView.NO_POSITION) {
                // get the movie at the position, this won't work if the class is static
                Post post = posts.get(position);
                // create intent for the new activity
                Intent intent = new Intent(context, PostDetailsActivity.class);
                // serialize the movie using parceler, use its short name as a key
                intent.putExtra(Post.class.getSimpleName(), post);
                // show the activity
                context.startActivity(intent);
            }
        }
    }
}
