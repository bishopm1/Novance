package com.novance.novance.ui.featured;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.novance.novance.R;

public class FeaturedFragment extends Fragment {

    private FeaturedViewModel featuredViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        featuredViewModel =
                ViewModelProviders.of(this).get(FeaturedViewModel.class);
        View root = inflater.inflate(R.layout.fragment_featured, container, false);
        final TextView textView = root.findViewById(R.id.text_featured);
        featuredViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
                textView.setTextColor(Color.BLACK);
            }
        });
        return root;
    }
}