package com.example.adopy.ui.favorites;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.adopy.R;

public class FavoritesFragment extends Fragment {

    private FavoritesViewModel favoritesViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
//        favoritesViewModel =
//                ViewModelProviders.of(this).get(FavoritesViewModel.class);
        View root = inflater.inflate(R.layout.fragment_favorites, container, false);
//        final TextView textView = root.findViewById(R.id.text_notifications);
//        favoritesViewModel.getText().observe(this, new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
        return root;
    }
}