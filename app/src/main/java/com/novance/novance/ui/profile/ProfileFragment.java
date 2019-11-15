package com.novance.novance.ui.profile;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.Image;
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

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.novance.novance.CreateAccountActivity;
import com.novance.novance.EditProfileActivity;
import com.novance.novance.LoginActivity;
import com.novance.novance.MainNavActivity;
import com.novance.novance.R;
import com.novance.novance.SettingsActivity;
import com.novance.novance.User;

import java.lang.reflect.Type;

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