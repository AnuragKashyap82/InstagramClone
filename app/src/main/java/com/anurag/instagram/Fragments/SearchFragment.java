package com.anurag.instagram.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.anurag.instagram.Adapter.AdapterUsers;
import com.anurag.instagram.Model.ModelUsers;
import com.anurag.instagram.databinding.FragmentSearchBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class SearchFragment extends Fragment {
    private FragmentSearchBinding binding;
    private FirebaseAuth firebaseAuth;

    private ArrayList<ModelUsers> usersArrayList;
    private AdapterUsers adapterUsers;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        firebaseAuth = FirebaseAuth.getInstance();

        loadAllUsers();

        return binding.getRoot();
    }
    private void loadAllUsers() {
        usersArrayList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

//                        long noOfFollower = snapshot.getChildrenCount();
//                        float noOfFollowers = noOfFollower / 1;
//                        binding.followersCountTv.setText(String.format("%.0f", noOfFollowers));

                        usersArrayList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            ModelUsers model = ds.getValue(ModelUsers.class);
                            usersArrayList.add(model);
                        }
                        adapterUsers = new AdapterUsers(getContext(), usersArrayList);
                        binding.usersRv.setAdapter(adapterUsers);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}