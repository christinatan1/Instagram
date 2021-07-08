//package com.example.instagram;
//
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.content.FileProvider;
//
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.Environment;
//import android.provider.MediaStore;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.Toast;
//
//import com.parse.FindCallback;
//import com.parse.Parse;
//import com.parse.ParseException;
//import com.parse.ParseFile;
//import com.parse.ParseQuery;
//import com.parse.ParseUser;
//import com.parse.SaveCallback;
//
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.util.List;
//
//public class MainActivity extends AppCompatActivity {
//
//    public static final String TAG = "MainActivity";
//    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;
//    private EditText etDescription;
//    private Button btnCaptureImage;
//    private ImageView ivPostImage;
//    private Button btnSubmit;
//    private File photoFile;
//    public String photoFileName = "photo.jpg";
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        etDescription = findViewById(R.id.etDescription);
//        btnCaptureImage = findViewById(R.id.btnCaptureImage);
//        ivPostImage = findViewById(R.id.ivPostImage);
//        btnSubmit = findViewById(R.id.btnSubmit);
//
//        btnCaptureImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                launchCamera();
//            }
//        });
//
////        queryPosts();
//
//        btnSubmit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String description = etDescription.getText().toString();
//                if (description.isEmpty()){
//                    Toast.makeText(MainActivity.this, "Description cannot be empty",
//                            Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                if (photoFile == null || ivPostImage.getDrawable() == null){
//                    Toast.makeText(MainActivity.this, "There is no image!", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                ParseUser currentUser = ParseUser.getCurrentUser();
//                savePost(description, currentUser, photoFile);
//            }
//        });
//    }
//
//    private void launchCamera() {
//        // create Intent to take a picture and return control to the calling application
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        // Create a File reference for future access
//        photoFile = getPhotoFileUri(photoFileName);
//
//        // wrap File object into a content provider, fileProvider wraps photo file
//        Uri fileProvider = FileProvider.getUriForFile(MainActivity.this, "com.codepath.fileprovider", photoFile);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);
//
//        // check if there is an application that can handle the intent
//        // as long as the result is not null, it's safe to use the intent.
//        if (intent.resolveActivity(getPackageManager()) != null) {
//            // start the image capture intent to take photo
//            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
//        }
//
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
//            if (resultCode == RESULT_OK) {
//                // by this point we have the camera photo on disk
//                Uri takenPhotoUri = Uri.fromFile(getPhotoFileUri(photoFileName));
//                Bitmap takenImage = BitmapFactory.decodeFile(takenPhotoUri.getPath());
//                Bitmap resizedBitmap = BitmapScaler.scaleToFitWidth(takenImage, 200);
//                ivPostImage.setImageBitmap(resizedBitmap);
//
//                // configure byte output stream
//                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//
//                // compress the image further
//                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
//
//                // create a new file for the resized bitmap
//                File resizedFile = getPhotoFileUri(photoFileName + "_resized");
//                try {
//                    resizedFile.createNewFile();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                FileOutputStream fos = null;
//                try {
//                    fos = new FileOutputStream(resizedFile);
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                }
//
//                // write the bytes of the bitmap to file
//                try {
//                    fos.write(bytes.toByteArray());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                try {
//                    fos.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            } else { // result was a failure
//                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//
//    // uri = uniform resource identifier, resource is the file in our case
//    // returns the File for a photo stored on disk given the fileName
//    public File getPhotoFileUri(String fileName) {
//        // Get safe storage directory for photos
//        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);
//
//        // Create the storage directory if it does not exist
//        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
//            Log.d(TAG, "failed to create directory");
//        }
//
//        // Return the file target for the photo based on filename
//        return new File(mediaStorageDir.getPath() + File.separator + fileName);
//    }
//
//
//    private void savePost(String description, ParseUser currentUser, File photoFile) {
//        Post post = new Post();
//        post.setDescription(description);
//        post.setImage(new ParseFile(photoFile));
//        post.setUser(currentUser);
//        post.saveInBackground(new SaveCallback() {
//            @Override
//            public void done(ParseException e) {
//                if (e != null){
//                    Log.e(TAG, "Error while saving", e);
//                    Toast.makeText(MainActivity.this, "Error while saving!", Toast.LENGTH_SHORT).show();
//                }
//                Log.i(TAG, "Post save was successful!");
//                etDescription.setText("");
//                ivPostImage.setImageResource(0);
//            }
//        });
//    }
//
////    private void queryPosts() {
////        // specify what type of data we want to query - Post.class
////        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
////        // include data referred by user key
////        query.include(Post.KEY_USER);
////        // limit query to latest 20 items
////        query.setLimit(20);
////        // order posts by creation date (newest first)
////        query.addDescendingOrder("createdAt");
////        // start an asynchronous call for posts
////        query.findInBackground(new FindCallback<Post>() {
////            @Override
////            public void done(List<Post> posts, ParseException e) {
////                // check for errors
////                if (e != null) {
////                    Log.e(TAG, "Issue with getting posts", e);
////                    return;
////                }
////
////                // for debugging purposes let's print every post description to logcat
////                for (Post post : posts) {
////                    Log.i(TAG, "Post: " + post.getDescription() + ", username: " + post.getUser().getUsername());
////                }
////
////                // save received posts to list and notify adapter of new data
////                allPosts.addAll(posts);
////                adapter.notifyDataSetChanged();
////            }
////        });
////    }
//
//    public void onLogoutButton(View view){
//        ParseUser.logOut();
//
//        Intent i = new Intent(this, LoginActivity.class);
//        startActivity(i);
//    }
//
//    public void onSubmitBtn(View view){
//        Intent i = new Intent(this, LoginActivity.class);
//        startActivity(i);
//    }
//}


package com.example.instagram;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvPosts;
    protected PostsAdapter adapter;
    protected List<Post> allPosts;
    public static final String TAG = "MainActivity";
    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvPosts = findViewById(R.id.rvPosts);

        // initialize the array that will hold posts and create a PostsAdapter
        allPosts = new ArrayList<>();
        adapter = new PostsAdapter(this, allPosts);

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                fetchTimelineAsync(0);
            }
        });

        // set the adapter on the recycler view
        rvPosts.setAdapter(adapter);
        // set the layout manager on the recycler view
        rvPosts.setLayoutManager(new LinearLayoutManager(this));
        // query posts from Parstagram
        queryPosts(0);
    }

    public void fetchTimelineAsync(int page) {
        // Send the network request to fetch the updated data
        queryPosts(1);
        // CLEAR OUT old items before appending in the new ones
        // Now we call setRefreshing(false) to signal refresh has finished
        swipeContainer.setRefreshing(false);
    }


    private void queryPosts(int i) {
        // specify what type of data we want to query - Post.class
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        // include data referred by user key
        query.include(Post.KEY_USER);
        // limit query to latest 20 items
        query.setLimit(20);
        // order posts by creation date (newest first)
        query.addDescendingOrder("createdAt");
        // start an asynchronous call for posts
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                // check for errors
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }
                for (Post post : posts) {
                    Log.i(TAG, "Post: " + post.getDescription() + ", username: " + post.getUser().getUsername());
                }
                if (i == 1){
                    adapter.clear();
                }
                // save received posts to list and notify adapter of new data, invalidates existing items and rebinds data
                allPosts.addAll(posts);
                adapter.notifyDataSetChanged();
            }
        });
    }
}

