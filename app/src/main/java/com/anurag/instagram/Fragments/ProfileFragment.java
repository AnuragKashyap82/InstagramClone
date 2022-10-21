package com.anurag.instagram.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.anurag.instagram.Adapter.AdapterFeed;
import com.anurag.instagram.Adapter.TabLayoutAdapter;
import com.anurag.instagram.LoginActivity;
import com.anurag.instagram.MainActivity;
import com.anurag.instagram.Model.ModelFeed;
import com.anurag.instagram.ProfileEditActivity;
import com.anurag.instagram.R;
import com.anurag.instagram.databinding.FragmentProfileBinding;

import com.google.android.material.tabs.TabLayout;
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
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;
    private FirebaseAuth firebaseAuth;
    TabLayoutAdapter tabLayoutAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        loadUserDetails();
        loadNoFollowers();
        loadNoFollowings();
        loadNoPosts();

        binding.editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), ProfileEditActivity.class));
            }
        });
        binding.createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheetDialog();
            }
        });
        binding.noFollowersRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity myActivity = (MainActivity) getActivity();

                Toolbar toolbar = myActivity.findViewById(R.id.toolbar);
                toolbar.setVisibility(View.GONE);

                FollowersFragment followersFragment = new FollowersFragment();
                FragmentTransaction transaction =  myActivity.getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.main_container, followersFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        binding.noFollowingsRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity myActivity = (MainActivity) getActivity();

                Toolbar toolbar = myActivity.findViewById(R.id.toolbar);
                toolbar.setVisibility(View.GONE);

                FollowingsFragment followingsFragment = new FollowingsFragment();
                FragmentTransaction transaction =  myActivity.getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.main_container, followingsFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        binding.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMoreBottomSheetDialog();
            }
        });
        tabLayoutAdapter = new TabLayoutAdapter(getActivity());
        binding.viewPager.setAdapter(tabLayoutAdapter);

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                binding.viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                binding.tabLayout.getTabAt(position).select();
            }
        });


        return binding.getRoot();
    }

    private void loadUserDetails() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
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
    private void loadNoFollowers() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("followers")
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
    private void loadNoFollowings() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("following")
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
    private void loadNoPosts() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("MyPost")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        long noOfPost = snapshot.getChildrenCount();
                        float noOfPosts = noOfPost/1;

                        binding.noPostTv.setText(String.format("%.0f", noOfPosts));

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    private void showBottomSheetDialog() {

        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bs_create_options);

        LinearLayout postReelLl = dialog.findViewById(R.id.postReelLl);
        LinearLayout postLl = dialog.findViewById(R.id.postLl);
        LinearLayout storyLl = dialog.findViewById(R.id.storyLl);
        LinearLayout storyHighlightLl = dialog.findViewById(R.id.storyHighlightLl);
        LinearLayout liveLl = dialog.findViewById(R.id.liveLl);

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

        postReelLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        postLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        storyLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        storyHighlightLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        liveLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }
    private void showMoreBottomSheetDialog() {

        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bs_more_options);

        LinearLayout settingLl = dialog.findViewById(R.id.settingLl);
        LinearLayout archiveLl = dialog.findViewById(R.id.archiveLl);
        LinearLayout activityLl = dialog.findViewById(R.id.activityLl);
        LinearLayout qrCodeLl = dialog.findViewById(R.id.qrCodeLl);
        LinearLayout savedLl = dialog.findViewById(R.id.savedLl);

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

        settingLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                MainActivity myActivity = (MainActivity) getActivity();

                Toolbar toolbar = myActivity.findViewById(R.id.toolbar);
                toolbar.setVisibility(View.GONE);

                SettingsFragment settingsFragment = new SettingsFragment();
                FragmentTransaction transaction =  myActivity.getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.main_container, settingsFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        archiveLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        activityLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        qrCodeLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        savedLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

}