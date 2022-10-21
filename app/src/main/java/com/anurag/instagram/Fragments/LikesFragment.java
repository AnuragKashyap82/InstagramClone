package com.anurag.instagram.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.anurag.instagram.Adapter.AdapterLikes;
import com.anurag.instagram.Adapter.AdapterUsers;
import com.anurag.instagram.Model.ModelLikes;
import com.anurag.instagram.Model.ModelUsers;
import com.anurag.instagram.R;
import com.anurag.instagram.databinding.FragmentFollowersBinding;
import com.anurag.instagram.databinding.FragmentLikesBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LikesFragment extends Fragment {
    private FragmentLikesBinding binding;
    private FirebaseAuth firebaseAuth;
    private String postId;

    private ArrayList<ModelLikes> likesArrayList;
    private AdapterLikes adapterLikes;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentLikesBinding.inflate(inflater, container, false);
        firebaseAuth = FirebaseAuth.getInstance();

        if (getArguments() != null){
            postId = getArguments().getString("postId");
            firebaseAuth = FirebaseAuth.getInstance();
            loadAllLikes(postId);
        }

        return binding.getRoot();
    }

    private void loadAllLikes(String postId) {
        likesArrayList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Feeds");
        reference.child(postId).child("Likes")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        likesArrayList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            ModelLikes model = ds.getValue(ModelLikes.class);
                            likesArrayList.add(model);
                        }
                        adapterLikes = new AdapterLikes(getContext(), likesArrayList);
                        binding.likesRv.setAdapter(adapterLikes);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    }