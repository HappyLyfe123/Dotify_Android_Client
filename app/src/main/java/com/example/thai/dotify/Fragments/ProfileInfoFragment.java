package com.example.thai.dotify.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thai.dotify.DotifyUser;
import com.example.thai.dotify.MainActivityContainer;
import com.example.thai.dotify.R;
import com.example.thai.dotify.Server.Dotify;
import com.example.thai.dotify.Server.DotifyHttpInterface;
import com.example.thai.dotify.StartUpContainer;
import com.example.thai.dotify.UserPermission;
import com.example.thai.dotify.Utilities.UserUtilities;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


import static android.app.Activity.RESULT_OK;

/**
 * the ProfileInfoFragment object displays data for the account
 */
public class ProfileInfoFragment extends Fragment implements View.OnClickListener{

    private String TAG = ProfileInfoFragment.class.getSimpleName();
    private Context activityContext;
    private LoginFragment loginFragment;
    private final int RESULT_LOAD_PROFILE_PIC = 1;
    private boolean readPermissionGranted;
    private int activityToStart;
    private final int REQUEST_CODE = 1052;
    private DotifyUser user;
    private CircleImageView profileImage;
    private UserImageUploadListener userImageUploadListener;

    /**
     * interface to upload some image for the profile
     */
    public interface UserImageUploadListener{
        void onUserImageUploaded(DotifyUser user);
    }

    /**
     *  A class that works on uploading an image to the server
     */
    private class UploadImage extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(final String... encodedImage) {
            // Create a Dotify object to begin sending requests
            final Dotify dotify = new Dotify(getString(R.string.base_URL));
            // Add an interceptor to log what is moving back and forth
            dotify.addLoggingInterceptor(HttpLoggingInterceptor.Level.BODY);
            // Create the interface to get the route methods
            DotifyHttpInterface dotifyHttpInterface = dotify.getHttpInterface();
            Call<ResponseBody> uploadImage = dotifyHttpInterface.saveUserProfileImage(
                    getString(R.string.appKey),
                    user.getUsername(),
                    encodedImage[0]
            );
            uploadImage.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            });
            return encodedImage[0];
        }

        /**
         * update DotifyUser object by adding the uploaded image
         * @param encodedImage
         */
        @Override
        protected void onPostExecute(String encodedImage) {
            super.onPostExecute(encodedImage);
            // Cache the encoded image
            user.setProfileImage(encodedImage);
            UserUtilities.cacheUser(activityContext, user);
            // Update the activitie's DotifyUser object
            userImageUploadListener.onUserImageUploaded(user);
        }
    }

    /**
     * creates the View object for the ProfileInfoFragment object
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_info, container, false);

        loginFragment = new LoginFragment();

        //TextViews
        TextView usernameEditText = view.findViewById(R.id.usernameDisplay);

        //Buttons
        profileImage = view.findViewById(R.id.profileImage);
        Button logoutButton = view.findViewById(R.id.logoutButton);

        //On Click Listener for Buttons
        profileImage.setOnClickListener(this);
        logoutButton.setOnClickListener(this);

        //Set the username to their username
        user = ((MainActivityContainer) this.getActivity()).getCurrentUser();
        usernameEditText.setText(user.getUsername());

        //Permission Granted
        readPermissionGranted = UserPermission.checkUserPermission(activityContext, UserPermission.Permission.READ_PERMISSION);

        // Check whether the user contains a user image
        String encodedUserImage = user.getProfileImage();
        if (encodedUserImage != null){
            if(!encodedUserImage.isEmpty()) {
                byte[] decodedImage = Base64.decode(encodedUserImage, Base64.DEFAULT);
                Bitmap userImage = BitmapFactory.decodeByteArray(decodedImage, 0, decodedImage.length);
                profileImage.setImageBitmap(userImage);
            }
        }
        return view;
    }

    /**
     * attach information about app's environment to the ProfileInfoFragment object
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activityContext = context;
    }

    /**
     * Ask the user if we can store a picture with their permission
     */
    private void requestPermissions() {
        if (ContextCompat.checkSelfPermission(activityContext, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE);
        }
    }

    /**
     * Method that follows on RequestPermission
     * @param requestCode The request code
     * @param permissions The permission
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Permission has been granted
                    readPermissionGranted = true;
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, activityToStart);
                } else {
                    Toast.makeText(activityContext, "Read Permission Denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    /**
     * Method invoked when profile image is selected
     * @param view
     */
    public void onClick(View view) {
        switch (view.getId()) {
            //For selecting a new profile image
            case R.id.profileImage: {
                if (!readPermissionGranted) {
                    activityToStart = RESULT_LOAD_PROFILE_PIC;
                    requestPermissions();
                } else {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, RESULT_LOAD_PROFILE_PIC);
                }
                break;
            }
            //For log out
            case R.id.logoutButton: //user wants to log out of app
                UserUtilities.removeCachedUserInfo(activityContext);
                //Send the User back to the login screen
                Intent signoutIntent = new Intent(getActivity(), StartUpContainer.class);
                startActivity(signoutIntent);
                getActivity().finish();
                break;
        }
    }

    /**
     * Method called after selecting a profile picture
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Receives the user image from gallery
        if ((requestCode == RESULT_LOAD_PROFILE_PIC)
                && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            String[] filePath = {MediaStore.Images.Media.DATA};
            Cursor cursor = activityContext.getContentResolver().query(selectedImage, filePath, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePath[0]);
            String picturePath = cursor.getString(columnIndex);
            Bitmap userImage = BitmapFactory.decodeFile(picturePath);
            cursor.close();

            //Set the image as the user's profile image
            profileImage.setImageBitmap(userImage);

            // Store the image received in the server
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            userImage.compress(Bitmap.CompressFormat.PNG, 30, byteArrayOutputStream);
            String encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
            UploadImage uploadImageTask = new UploadImage();
            uploadImageTask.execute(encodedImage);
        }

    }

    /**
     * Allows the MainActivityContainer to update its DotifyUser value upon completion of
     * the uploading of the user's profile image to the server
     *
     * @param userImageUploadedListener The listener implemented by the main activity
     */
    public void setOnUserImageUploadedListener(UserImageUploadListener userImageUploadedListener){
        this.userImageUploadListener = userImageUploadedListener;
    }

}