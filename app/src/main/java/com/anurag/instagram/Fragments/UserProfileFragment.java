package com.anurag.instagram.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.anurag.instagram.R;
import com.anurag.instagram.databinding.FragmentUserProfileBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class UserProfileFragment extends Fragment {
    private FragmentUserProfileBinding binding;
    private FirebaseAuth firebaseAuth;
    private String userUid;
    private boolean isFollowing = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUserProfileBinding.inflate(inflater, container, false);

        if (getArguments() != null){
            userUid = getArguments().getString("userUid");
            firebaseAuth = FirebaseAuth.getInstance();
            loadUserDetails(userUid);
            checkIsFollowing(userUid);
            loadNoFollowers(userUid);
            loadNoFollowings(userUid);
        }
        binding.followBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkIsFollowingToUnfollow(userUid);
            }
        });
        binding.followersRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        binding.followingRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        return binding.getRoot();
    }

    private void checkIsFollowingToUnfollow(String userUid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("following").child(userUid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        isFollowing = snapshot.exists();
                        if (isFollowing){
                            unFollowUser(userUid);
                        }
                        else{
                            followUser(userUid);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    private void loadNoFollowers(String userUid) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(userUid).child("followers")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        long noOfFollower = snapshot.getChildrenCount();
                        float noOfFollowers = noOfFollower/1;

                        binding.followersTv.setText(String.format("%.0f", noOfFollowers));

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    private void loadNoFollowings(String userUid) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(userUid).child("following")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        long noOfFollowing = snapshot.getChildrenCount();
                        float noOfFollowings = noOfFollowing/1;

                        binding.followingTv.setText(String.format("%.0f", noOfFollowings));

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void unFollowUser(String userUid) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("following").child(userUid)
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getContext(), "You unFollowed this user...!!!!", Toast.LENGTH_SHORT).show();
                        removeFromUserFollowers(userUid);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void removeFromUserFollowers(String userUid) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(userUid).child("followers").child(firebaseAuth.getUid())
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkIsFollowing(String userUid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("following").child(userUid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        isFollowing = snapshot.exists();
                        if (isFollowing){
                            binding.followBtn.setText("Following");

                        }
                        else{
                            binding.followBtn.setText("Follow");

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void followUser(String userUid) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid", "" + firebaseAuth.getUid());
        hashMap.put("userUid", userUid);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("following").child(userUid)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getContext(), "You are now following this user...!!!!", Toast.LENGTH_SHORT).show();
                        addToUserFollowers(userUid);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addToUserFollowers(String userUid) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid", "" + userUid);
        hashMap.put("userUid", firebaseAuth.getUid());

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(userUid).child("followers").child(firebaseAuth.getUid())
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadUserDetails(String userUid) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(userUid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {

                            String uid = "" + ds.child("uid").getValue();
                            String email = "" + ds.child("email").getValue();
                            String name = "" + ds.child("name").getValue();
                            String bio = "" + ds.child("bio").getValue();
                            String website = "" + ds.child("website").getValue();
                            String username = "" + ds.child("username").getValue();
                            String online = "" + ds.child("online").getValue();
                            String timestamp = "" + ds.child("timestamp").getValue();
                            String profileImage = "" + ds.child("profileImage").getValue();

                            binding.nameTv.setText(name);
                            binding.bioTv.setText(bio);
                            binding.websiteTv.setText(website);
                            binding.usernameTv.setText(username);

                            try {
                                Picasso.get().load(profileImage).placeholder(R.drawable.ic_person_outline_black).into(binding.profileIv);
                            } catch (Exception e) {
                                binding.profileIv.setImageResource(R.drawable.ic_person_outline_black);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}