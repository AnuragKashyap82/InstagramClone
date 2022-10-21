package com.anurag.instagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.anurag.instagram.Adapter.AdapterMessenger;
import com.anurag.instagram.Adapter.AdapterUsers;
import com.anurag.instagram.Model.ModelMessenger;
import com.anurag.instagram.Model.ModelUsers;
import com.anurag.instagram.databinding.ActivityMessengerBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MessengerActivity extends AppCompatActivity {
    private ActivityMessengerBinding binding;
    private FirebaseAuth firebaseAuth;

    private ArrayList<ModelMessenger> messengerArrayList;
    private AdapterMessenger adapterMessenger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMessengerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            window.setStatusBarColor(ContextCompat.getColor(MessengerActivity.this, R.color.white));
        }

        firebaseAuth = FirebaseAuth.getInstance();

        loadAllUsers();
    }
    private void loadAllUsers() {
        messengerArrayList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

//                        long noOfFollower = snapshot.getChildrenCount();
//                        float noOfFollowers = noOfFollower / 1;
//                        binding.followersCountTv.setText(String.format("%.0f", noOfFollowers));

                        messengerArrayList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            ModelMessenger model = ds.getValue(ModelMessenger.class);
                            messengerArrayList.add(model);
                        }
                        adapterMessenger = new AdapterMessenger(MessengerActivity.this ,messengerArrayList);
                        binding.messengerRv.setAdapter(adapterMessenger);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}