package com.anurag.instagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.anurag.instagram.databinding.ActivityMessageViewBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class MessageViewActivity extends AppCompatActivity {
    private ActivityMessageViewBinding binding;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMessageViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            window.setStatusBarColor(ContextCompat.getColor(MessageViewActivity.this, R.color.white));
        }

        uid = getIntent().getStringExtra("uid");
        loadUserDetails(uid);
        loadNoFollowers(uid);
        loadNoPosts(uid);

        binding.profileIv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast = new Toast(MessageViewActivity.this);
                View view1 = getLayoutInflater().inflate(R.layout.toast_layout, (ViewGroup) findViewById(R.id.viewToast));
                toast.setView(view1);
                TextView txtMsg = view1.findViewById(R.id.txtMsg);
                txtMsg.setText("Profile Pic Clicked");
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();
            }
        });

    }
    private void loadUserDetails(String uid) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String name = "" + snapshot.child("name").getValue();
                        String username = "" + snapshot.child("username").getValue();
                        String profileImage = "" + snapshot.child("profileImage").getValue();

                        binding.nameTv.setText(name);
                        binding.nameTv2.setText(name);
                        binding.usernameTv.setText(username);
                        binding.usernameTv2.setText(username);

                        try {
                            Picasso.get().load(profileImage).placeholder(R.drawable.ic_person_filled_black).into(binding.profileIv2);
                        } catch (Exception e) {
                            binding.profileIv2.setImageResource(R.drawable.ic_person_filled_black);
                        }
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
    private void loadNoFollowers(String uid) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(uid).child("followers")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        long noOfFollower = snapshot.getChildrenCount();
                        float noOfFollowers = noOfFollower/1;

                        binding.followersTv.setText(String.format("%.0f", noOfFollowers) + " followers");

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    private void loadNoPosts(String uid) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(uid).child("MyPost")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        long noOfPost = snapshot.getChildrenCount();
                        float noOfPosts = noOfPost/1;

                        binding.noPostTv.setText(String.format("%.0f", noOfPosts) + " posts");

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}