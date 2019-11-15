package com.novance.novance.ui.feed;

import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.novance.novance.CreatePostActivity;
import com.novance.novance.LoginActivity;
import com.novance.novance.MainNavActivity;
import com.novance.novance.R;
import com.novance.novance.User;

public class FeedFragment extends Fragment {

    private FeedViewModel feedViewModel;

    //creates global user variable
    User u;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        //reads in user object from MainNavActivity
        u = getActivity().getIntent().getParcelableExtra("user");

        feedViewModel =
                ViewModelProviders.of(this).get(FeedViewModel.class);
        View root = inflater.inflate(R.layout.fragment_feed, container, false);

        VideoView videoView = (VideoView) root.findViewById(R.id.startupVideoView);
        String videoPath = "android.resource://" + getActivity().getPackageName() + "/" + R.raw.sample_startup_video;
        Uri uri = Uri.parse(videoPath);
        videoView.setVideoURI(uri);
        videoView.seekTo(1);

        MediaController mediaController = new MediaController(getActivity());
        videoView.setMediaController(mediaController);
        mediaController.setAnchorView(videoView);

        Toolbar toolbar = (Toolbar) root.findViewById(R.id.toolbar);
        ImageButton addPostButton =  (ImageButton) toolbar.findViewById(R.id.addPostButton);
        addPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), CreatePostActivity.class);
                //passes user to next activity
                i.putExtra("user", u);
                startActivity(i);
            }
        });

        return root;
    }
}