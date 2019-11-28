package com.novance.novance.ui.profile;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.novance.novance.CreateAccountActivity;
import com.novance.novance.EditProfileActivity;
import com.novance.novance.LoginActivity;
import com.novance.novance.MainNavActivity;
import com.novance.novance.R;
import com.novance.novance.SettingsActivity;
import com.novance.novance.User;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    //TODO if bio text field extends past certain length, add new line
    //TODO if startup description text field extends past certain length, add new line
    //TODO create menu of profile options
    //TODO set bio, full name, user name, and profile picture to users saved data, if none show defaults
    //TODO add tags to all classes

    //creates global user variable
    User u;

    //Profile Fragment tag
    private static final String TAG = "ProfileFragment";

    private ProfileViewModel profileViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //reads in user object from MainNavActivity
        u = getActivity().getIntent().getParcelableExtra("user");

        profileViewModel =
                ViewModelProviders.of(this).get(ProfileViewModel.class);
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        //loads in profile data
        TextView nameTextView = (TextView) root.findViewById(R.id.nameTextView);
        nameTextView.setText(u.getFullName());
        TextView usernameTextView = (TextView) root.findViewById(R.id.usernameTextView);
        usernameTextView.setText("@" + u.getUsername());
        if(u.getProfileImageUri() != null){
            CircleImageView profileImageView = (CircleImageView) root.findViewById(R.id.profileImageView);
            // Image link from internet
            String link = u.getProfileImageUri().toString();

            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .placeholder(R.drawable.ic_account_circle_black_120dp)
                    .error(R.drawable.ic_account_circle_black_120dp);


            Glide.with(this).load(link).apply(options).into(profileImageView);
            profileImageView.setBorderColor(Color.parseColor("#243248"));
            profileImageView.setBorderWidth(2);
        }
        //TODO add user's bio
        //TODO add users profile picture

        //handles clicking of startup preview (redirects to full startup page)
        final RelativeLayout startupPreviewLayout = (RelativeLayout) root.findViewById(R.id.startupPreviewLayout);
        startupPreviewLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startupPreviewLayout.setBackgroundColor(Color.rgb(178,178,178));
                //TODO take to startup page!
            }
        });

        //opens settings activity when settings button clicked
        Button settings = (Button) root.findViewById(R.id.settingsBtn);
        settings.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent i = new Intent(getActivity(), SettingsActivity.class);
                startActivity(i);
            }
        });

        //opens edit profile activity when edit image view is clicked
        ImageView editProfileImageView = (ImageView) root.findViewById(R.id.editProfileImageView);
        editProfileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), EditProfileActivity.class);
                //passes user to next activity
                i.putExtra("user", u);
                startActivity(i);
            }
        });

        return root;
    }
    //TODO switch all clickable imageviews to imagebuttons
}