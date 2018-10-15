package com.example.thai.dotify;

import android.Manifest;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thai.dotify.Server.Dotify;
import com.google.gson.Gson;
import de.hdodenhof.circleimageview.CircleImageView;


import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

public class ProfileInfoFragment extends Fragment implements View.OnClickListener{

    private Context activityContext;
    private TextView usernameEditText;
    private LoginFragment loginFragment;
    private final int RESULT_LOAD_PROFILE_PIC = 1;
    private boolean readPermissionGranted;
    private int activityToStart;
    private final int REQUEST_CODE = 1052;
    private DotifyUser user;
    private CircleImageView profileImage;

    public interface OnChangeFragmentListener {
        void buttonClicked(StartUpContainer.AuthFragmentType fragmentType);
    }

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
        user = UserUtilities.getCachedUserInfo(activityContext);
        if (user.getUsername() != null) {
            usernameEditText.setText(user.getUsername());
        }
        else{
            usernameEditText.setText("Not Found");
        }
        //Permission Granted
        readPermissionGranted = UserPermission.checkUserPermission(activityContext, UserPermission.Permission.READ_PERMISSION);

        Bitmap userImage = InternalStorage.getProfilePic(activityContext, user.getUsername());
        //Bitmap for user profile imaage
        if (userImage!= null) {
            profileImage.setImageBitmap(userImage);
        }


        return view;
    }

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
     * Method for onClick buttons
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
            case R.id.logoutButton:
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

            //Rotates the Bitmap image
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            Bitmap rotatedBitmap = Bitmap.createBitmap(userImage, 0, 0, userImage.getWidth(), userImage.getHeight(), matrix, true);
            userImage = rotatedBitmap;

            //Set the image as the user's profile image
            profileImage.setImageBitmap(userImage);
            //Store the image received in the internal storage
            InternalStorage.saveImageFile(activityContext, InternalStorage.ImageType.PROFILE, user.getUsername(), userImage);

        }



    }


}