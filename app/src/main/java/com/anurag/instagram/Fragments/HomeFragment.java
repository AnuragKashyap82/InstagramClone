package com.anurag.instagram.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.anurag.instagram.Adapter.AdapterFeed;
import com.anurag.instagram.Adapter.AdapterStatus;
import com.anurag.instagram.Model.ModelFeed;
import com.anurag.instagram.Model.ModelStatus;
import com.anurag.instagram.Model.ModelUserStatus;
import com.anurag.instagram.R;
import com.anurag.instagram.databinding.FragmentHomeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;

    AdapterStatus statusAdapter;
    ArrayList<ModelUserStatus> modelUserStatuses;

    private ArrayList<ModelFeed> feedArrayList;
    private AdapterFeed adapterFeed;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        loadFeeds();
        loadMyInfo();

        modelUserStatuses = new ArrayList<>();
        statusAdapter = new AdapterStatus(getActivity(), modelUserStatuses);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        binding.storyRv.setLayoutManager(layoutManager);
        binding.storyRv.setAdapter(statusAdapter);

        database.getReference().child("stories").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    modelUserStatuses.clear();
                    for (DataSnapshot storySnapshot : snapshot.getChildren()){
                        ModelUserStatus status = new ModelUserStatus();
                        status.setName(storySnapshot.child("name").getValue(String.class));
                        status.setUsername(storySnapshot.child("username").getValue(String.class));
                        status.setProfileImage(storySnapshot.child("profileImage").getValue(String.class));
                        status.setLastUpdated(storySnapshot.child("lastUpdated").getValue(long.class));

                        ArrayList<ModelStatus> modelStatuses = new ArrayList<>();

                        for (DataSnapshot statusSnapshot : storySnapshot.child("statuses").getChildren()){
                            ModelStatus sampleModelStatus = statusSnapshot.getValue(ModelStatus.class);
                            modelStatuses.add(sampleModelStatus);
                        }
                        status.setStatuses(modelStatuses);
                        modelUserStatuses.add(status);
                    }
                    statusAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        return binding.getRoot();
    }
    private void loadFeeds() {
        feedArrayList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Feeds");
        reference
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
                        binding.feedsRv.setAdapter(adapterFeed);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    private void loadMyInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String username = "" + snapshot.child("username").getValue();
                        String profileImage = "" + snapshot.child("profileImage").getValue();

                        try {
                            Picasso.get().load(profileImage).placeholder(R.drawable.ic_person_filled_black).into(binding.profileIv);
                        } catch (Exception e) {
                            binding.profileIv.setImageResource(R.drawable.ic_person_filled_black);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}