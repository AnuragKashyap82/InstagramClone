package com.anurag.instagram.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.anurag.instagram.Adapter.AdapterFollowers;
import com.anurag.instagram.Model.ModelFollowers;
import com.anurag.instagram.databinding.FragmentFollowingsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class FollowingsFragment extends Fragment {
    private FragmentFollowingsBinding binding;
    private FirebaseAuth firebaseAuth;

    private ArrayList<ModelFollowers> followersArrayList;
    private AdapterFollowers adapterFollowers;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFollowingsBinding.inflate(inflater, container, false);
        firebaseAuth = FirebaseAuth.getInstance();

        loadFollowings();

        return binding.getRoot();
    }

    private void loadFollowings() {
        followersArrayList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("following")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        long noOfFollower = snapshot.getChildrenCount();
                        float noOfFollowers = noOfFollower / 1;
                        binding.followersCountTv.setText(String.format("%.0f", noOfFollowers));

                        followersArrayList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            ModelFollowers model = ds.getValue(ModelFollowers.class);
                            followersArrayList.add(model);
                        }
                        adapterFollowers = new AdapterFollowers(getContext(), followersArrayList);
                        binding.followersRv.setAdapter(adapterFollowers);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}