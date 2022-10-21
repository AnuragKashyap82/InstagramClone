package com.anurag.instagram;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.anurag.instagram.Adapter.AdapterComment;
import com.anurag.instagram.Model.ModelComment;
import com.anurag.instagram.databinding.ActivityCommentBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class CommentActivity extends AppCompatActivity {
    private ActivityCommentBinding binding;
    private FirebaseAuth firebaseAuth;
    private String postId, uid, comment, username;

    private ArrayList<ModelComment> commentArrayList;
    private AdapterComment adapterComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCommentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            window.setStatusBarColor(ContextCompat.getColor(CommentActivity.this, R.color.white));
        }

        postId = getIntent().getStringExtra("postId");
        uid = getIntent().getStringExtra("uid");

        firebaseAuth = FirebaseAuth.getInstance();

        loadPostSenderDetails(uid);
        loadUserDetails(postId);
        loadAllComments(postId);

        binding.postCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });
    }

    private void loadPostSenderDetails(String uid) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        username = "" + snapshot.child("username").getValue();
                        String profileImage = "" + snapshot.child("profileImage").getValue();

                        loadPostDetails(postId, username);
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
    private void loadPostDetails(String postId, String username) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Feeds");
        ref.child(postId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String caption = "" + snapshot.child("caption").getValue();
                        String profileImage = "" + snapshot.child("profileImage").getValue();

                        binding.captionTv.setText(username +" "+ caption);
                        try {
                            Picasso.get().load(profileImage).placeholder(R.drawable.ic_person_filled_black).into(binding.profileIvUser);
                        } catch (Exception e) {
                            binding.profileIvUser.setImageResource(R.drawable.ic_person_filled_black);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    private void loadUserDetails(String postId) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String username = "" + snapshot.child("username").getValue();
                        String profileImage = "" + snapshot.child("profileImage").getValue();


                        binding.commentEt.setHint("Comment as " + username);
                        try {
                            Picasso.get().load(profileImage).placeholder(R.drawable.ic_person_filled_black).into(binding.profileIvUser);
                        } catch (Exception e) {
                            binding.profileIvUser.setImageResource(R.drawable.ic_person_filled_black);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    private void loadAllComments(String postId) {
        commentArrayList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Feeds");
        reference.child(postId).child("Comments")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        commentArrayList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            ModelComment model = ds.getValue(ModelComment.class);
                            commentArrayList.add(model);
                        } Collections.sort(commentArrayList, new Comparator<ModelComment>() {
                            @Override
                            public int compare(ModelComment t1, ModelComment t2) {
                                return t1.getTimestamp().compareToIgnoreCase(t2.getTimestamp());
                            }
                        });
                        Collections.reverse(commentArrayList);
                        adapterComment = new AdapterComment(CommentActivity.this, commentArrayList);
                        binding.commentsRv.setAdapter(adapterComment);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void validateData() {

        comment = binding.commentEt.getText().toString().trim();

        if (TextUtils.isEmpty(comment)){
            Toast.makeText(CommentActivity.this, "Enter your comment....!!!!", Toast.LENGTH_SHORT).show();
        }else {
            postComment();
        }

    }

    private void postComment() {
        long timestamp = System.currentTimeMillis();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("comment", "" + comment);
        hashMap.put("uid", "" + firebaseAuth.getUid());
        hashMap.put("timestamp", "" + timestamp);
        hashMap.put("postId", "" + postId);

        DatabaseReference reference  = FirebaseDatabase.getInstance().getReference("Feeds");
        reference.child(postId).child("Comments").child("" + timestamp)
                .setValue(hashMap)
               .addOnSuccessListener(new OnSuccessListener<Void>() {
                   @Override
                   public void onSuccess(Void unused) {
                       Toast.makeText(CommentActivity.this, "Commented Successfully!!!!!!", Toast.LENGTH_SHORT).show();
                       clearText();
                   }
               }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CommentActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearText() {
        binding.commentEt.setText("");
    }
}