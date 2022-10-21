package com.anurag.instagram.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.anurag.instagram.Adapter.AdapterFeed;
import com.anurag.instagram.Model.ModelFeed;
import com.anurag.instagram.R;
import com.anurag.instagram.databinding.FragmentMyPostBinding;
import com.anurag.instagram.databinding.FragmentSavedPostBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SavedPostFragment extends Fragment {
    private FragmentSavedPostBinding binding;
    private FirebaseAuth firebaseAuth;

    private ArrayList<ModelFeed> feedArrayList;
    private AdapterFeed adapterFeed;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSavedPostBinding.inflate(inflater, container, false);
        firebaseAuth = FirebaseAuth.getInstance();

        loadSavedPost();

        return binding.getRoot();
    }
    private void loadSavedPost() {
        feedArrayList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("savedPost")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        feedArrayList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            ModelFeed model = ds.getValue(ModelFeed.class);
                            feedArrayList.add(model);
                        } Collections.sort(feedArrayList, new Comparator<ModelFeed>() {
                            @Override
                            public int compare(ModelFeed t1, ModelFeed t2) {
                                return t1.getPostId().compareToIgnoreCase(t2.getPostId());
                            }
                        });
                        Collections.reverse(feedArrayList);
                        adapterFeed = new AdapterFeed(getContext(), feedArrayList);
                        binding.mySavedPostRv.setAdapter(adapterFeed);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}