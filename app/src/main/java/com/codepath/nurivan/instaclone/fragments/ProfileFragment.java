package com.codepath.nurivan.instaclone.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

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
import com.codepath.nurivan.instaclone.activities.LoginActivity;
import com.codepath.nurivan.instaclone.R;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;


public class ProfileFragment extends Fragment {
    public static final String TAG = "ProfileFragment";
    Button bLogout;
    Button bUpdatePicture;
    TextView tvProfileUsername;
    ImageView ivProfilePicture;

    private ActivityResultLauncher resultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == getActivity().RESULT_OK) {
                        // compare the resultCode with the
                        // SELECT_PICTURE constant
                        // Get the url of the image from data
                        Uri selectedImageUri = result.getData().getData();
                        if (null != selectedImageUri) {
                            // update the preview image in the layout
                            Glide.with(getContext())
                                    .load(selectedImageUri)
                                    .into(ivProfilePicture);

                            Bitmap selectedImageBitmap;
                            try {
                                ImageDecoder.Source source = ImageDecoder.createSource(getContext().getContentResolver(), selectedImageUri);
                                selectedImageBitmap = ImageDecoder.decodeBitmap(source);
                            }
                            catch (IOException e) {
                                e.printStackTrace();
                                return;
                            }

                            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                            selectedImageBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                            byte[] image = outputStream.toByteArray();

                            ParseFile profileImage = new ParseFile("profilePicture.png", image);

                            ParseUser.getCurrentUser().put("profilePicture", profileImage);
                            ParseUser.getCurrentUser().saveInBackground(e -> {
                                if(e == null) {
                                    Log.i(TAG, "Updated profile.");
                                } else {
                                    Log.e(TAG, "Failed to update profile.", e);
                                }
                            });
                        }

                    } else { // Result was a failure
                        Toast.makeText(getActivity(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
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

        bUpdatePicture.setOnClickListener(v -> {
            imageChooser();
        });

        Glide.with(view)
                .load(ParseUser.getCurrentUser().getParseFile("profilePicture").getUrl())
                .circleCrop()
                .into(ivProfilePicture);
    }

    private void logOut() {
        ParseUser.logOut();

        Intent i = new Intent(getActivity(), LoginActivity.class);
        startActivity(i);
        getActivity().finish();

    }

    void imageChooser() {

        // create an instance of the
        // intent of the type image
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);

        // pass the constant to compare it
        // with the returned requestCode
        resultLauncher.launch(i);
    }
}