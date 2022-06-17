package com.codepath.nurivan.instaclone.fragments;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.nurivan.instaclone.Post;
import com.codepath.nurivan.instaclone.PostAdapter;
import com.codepath.nurivan.instaclone.activities.LoginActivity;
import com.codepath.nurivan.instaclone.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";
    private Button bLogout;
    private Button bUpdatePicture;
    private TextView tvProfileUsername;
    private ImageView ivProfilePicture;

    @Override
    public boolean shouldShowRequestPermissionRationale(@NonNull String permission) {
        return super.shouldShowRequestPermissionRationale(permission);
    }

    private RecyclerView rvPostList;
    private PostAdapter adapter;
    private List<Post> postList;

    private ActivityResultLauncher<String> resultLauncher;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {

        resultLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {

                    @Override
                    public void onActivityResult(Uri result) {
                        if (result != null) {
                            // compare the resultCode with the
                            // SELECT_PICTURE constant
                            // Get the url of the image from data
                            // update the preview image in the layout
                            Glide.with(getContext())
                                    .load(result)
                                    .circleCrop()
                                    .into(ivProfilePicture);


                            Bitmap selectedImageBitmap;
                            try {
                                ImageDecoder.Source source = ImageDecoder.createSource(getContext().getContentResolver(), result);
                                selectedImageBitmap = ImageDecoder.decodeBitmap(source);
                            }
                            catch (IOException e) {
                                e.printStackTrace();
                                return;
                            }

                            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                            selectedImageBitmap.compress(Bitmap.CompressFormat.PNG, 20, outputStream);
                            byte[] image = outputStream.toByteArray();


                            ParseFile profileImage = new ParseFile("profilePicture.png", image);

                            ParseUser.getCurrentUser().put("profilePicture", profileImage);
                            ParseUser.getCurrentUser().saveInBackground(e -> {
                                if(e == null) {
                                    Log.i(TAG, "Updated profile.");
                                    Toast.makeText(getActivity(), "Updated profile picture.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Log.e(TAG, "Failed to update profile.", e);
                                }
                            });

                        } else { // Result was a failure
                            Toast.makeText(getActivity(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        return inflater.inflate(R.layout.fragment_profile, parent, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        bLogout = view.findViewById(R.id.bLogout);
        bLogout.setOnClickListener(v -> logOut());
        tvProfileUsername = view.findViewById(R.id.tvProfileUsername);
        tvProfileUsername.setText(ParseUser.getCurrentUser().getUsername());
        ivProfilePicture = view.findViewById(R.id.ivProfilePicture);
        bUpdatePicture = view.findViewById(R.id.bUpdatePicture);
        rvPostList = view.findViewById(R.id.rvPostList);

        postList = new ArrayList<>();
        adapter = new PostAdapter(getContext(), postList, true);

        rvPostList.setLayoutManager(new GridLayoutManager(getContext(), 3));
        rvPostList.setAdapter(adapter);

        getUserPosts();

        bUpdatePicture.setOnClickListener(v -> {
            imageChooser();
        });

        Glide.with(view)
                .load(ParseUser.getCurrentUser().getParseFile("profilePicture").getUrl())
                .circleCrop()
                .into(ivProfilePicture);
    }

    private void getUserPosts() {
        postList.clear();
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser());
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

    private void logOut() {
        ParseUser.logOut();

        Intent i = new Intent(getActivity(), LoginActivity.class);
        startActivity(i);
        getActivity().finish();

    }

    void imageChooser() {

        // pass the constant to compare it
        // with the returned requestCode
        resultLauncher.launch("image/*");
    }
}