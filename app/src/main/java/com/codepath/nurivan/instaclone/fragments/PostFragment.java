package com.codepath.nurivan.instaclone.fragments;

import static com.parse.Parse.getApplicationContext;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.codepath.nurivan.instaclone.Post;
import com.codepath.nurivan.instaclone.R;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;

public class PostFragment extends Fragment {
    private static final String TAG = "PostFragment";


    Button bPicture;
    Button bSubmit;
    EditText etDescription;
    ImageView ivPost;
    ProgressBar pbUploading;

    private File photoFile;
    private Uri photoFileUri;
    private ActivityResultLauncher<Uri> resultLauncher;

    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        photoFile = getPhotoFileUri("photo.jpg");
        photoFileUri = FileProvider.getUriForFile(getActivity(), "com.codepath.fileprovider", photoFile);
        resultLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                new ActivityResultCallback<Boolean>() {
                    @Override
                    public void onActivityResult(Boolean result) {
                        Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                        // RESIZE BITMAP, see section below
                        // Load the taken image into a preview
                        ImageView ivPreview = (ImageView) getView().findViewById(R.id.ivPost);
                        ivPreview.setImageBitmap(takenImage);
                        bSubmit.setVisibility(View.VISIBLE);
                    }
                });

        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_post, parent, false);
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        bPicture = view.findViewById(R.id.bPicture);
        bSubmit = view.findViewById(R.id.bSubmit);
        etDescription = view.findViewById(R.id.etDescription);
        ivPost = view.findViewById(R.id.ivPost);
        pbUploading = view.findViewById(R.id.pbUploading);

        bSubmit.setOnClickListener(v -> {
            String description = etDescription.getText().toString();
            if(description.isEmpty()) {
                Toast.makeText(getActivity(), "Description cannot be empty.", Toast.LENGTH_SHORT).show();
                return;
            }

            if(photoFile == null || ivPost.getDrawable() == null) {
                Toast.makeText(getActivity(), "There is no image!", Toast.LENGTH_SHORT).show();
                return;
            }

            bSubmit.setVisibility(View.GONE);
            pbUploading.setVisibility(View.VISIBLE);

            ParseUser currentUser = ParseUser.getCurrentUser();
            savePost(description, currentUser, photoFile);
        });
        bPicture.setOnClickListener(v -> {
            launchCamera();
        });
    }

    private void savePost(String description, ParseUser currentUser, File photoFile) {
        Post post = new Post();
        post.setDescription(description);
        post.setUser(currentUser);
        post.setImage(new ParseFile(photoFile));
        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e != null) {
                    Log.e(TAG, "Error while saving", e);
                    Toast.makeText(getActivity(), "Error while saving", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.i(TAG, "Post saved successfully");
                etDescription.setText("");
                ivPost.setImageResource(0);
                pbUploading.setVisibility(View.GONE);
            }
        });
    }

    private void launchCamera() {
        resultLauncher.launch(photoFileUri);
    }

    // Returns the File for a photo stored on disk given the fileName
    private File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        return new File(mediaStorageDir.getPath() + File.separator + fileName);
    }
}