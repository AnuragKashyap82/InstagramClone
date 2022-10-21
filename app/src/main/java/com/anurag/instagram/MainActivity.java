package com.anurag.instagram;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.anurag.instagram.Fragments.HomeFragment;
import com.anurag.instagram.Fragments.ProfileFragment;
import com.anurag.instagram.Fragments.SearchFragment;
import com.anurag.instagram.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;


public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            window.setStatusBarColor(ContextCompat.getColor(MainActivity.this, R.color.white));
        }

        firebaseAuth = FirebaseAuth.getInstance();

        setSupportActionBar(binding.toolbar);
        binding.messengerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Messenger btn is clicked....!!!!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, MessengerActivity.class));
            }
        });
        binding.createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheetDialog();
            }
        });
        getSupportFragmentManager().beginTransaction().replace(R.id.main_container, new HomeFragment()).commit();
        binding.bottomNavigation.setSelectedItemId(R.id.navigation_home);

        binding.bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        binding.toolbar.setVisibility(View.VISIBLE);
                        fragment = new HomeFragment();
                        break;
                    case R.id.navigation_search:
                        binding.toolbar.setVisibility(View.GONE);
                        fragment = new SearchFragment();
                        break;
                    case R.id.navigation_reels:
                        Toast.makeText(MainActivity.this, "Reels to be integrated...!!!!", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.navigation_heart:
                        Toast.makeText(MainActivity.this, "Activity to be integrated...!!!!", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.navigation_profile:
                        binding.toolbar.setVisibility(View.GONE);
                        fragment = new ProfileFragment();
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.main_container, fragment).commit();
                return true;
            }
        });
    }

    private void showBottomSheetDialog() {

        final Dialog dialog = new Dialog(MainActivity.this);
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
                startActivity(new Intent(MainActivity.this, CreatePostActivity.class));
            }
        });
        storyLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                startActivity(new Intent(MainActivity.this, CreateStoryActivity.class));
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
}