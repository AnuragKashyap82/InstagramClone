package com.anurag.instagram.Adapter;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.anurag.instagram.CommentActivity;
import com.anurag.instagram.EditPostActivity;
import com.anurag.instagram.Fragments.LikesFragment;
import com.anurag.instagram.Fragments.ProfileFragment;
import com.anurag.instagram.Fragments.UserProfileFragment;
import com.anurag.instagram.MainActivity;
import com.anurag.instagram.Model.ModelFeed;
import com.anurag.instagram.R;
import com.anurag.instagram.databinding.RowFeedBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pedromassango.doubleclick.DoubleClick;
import com.pedromassango.doubleclick.DoubleClickListener;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterFeed extends RecyclerView.Adapter<AdapterFeed.HolderFeed> {

    private Context context;
    public ArrayList<ModelFeed> feedArrayList;
    private FirebaseUser firebaseUser;
    private boolean isFollowing = false;

    public AdapterFeed(Context context, ArrayList<ModelFeed> feedArrayList) {
        this.context = context;
        this.feedArrayList = feedArrayList;
         firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    }

    @NonNull
    @Override
    public HolderFeed onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.row_feed, parent, false);
        return new HolderFeed(view);

    }

    @Override
    public void onBindViewHolder(@NonNull HolderFeed holder, int position) {

        final ModelFeed modelFeed = feedArrayList.get(position);
        String postId = modelFeed.getPostId();
        String uid = modelFeed.getUid();
        String caption = modelFeed.getCaption();
        String postImage = modelFeed.getPostImage();

        loadUserDetails(modelFeed, holder);


        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(postId));
        String dateFormat = DateFormat.format("dd/MM/yyyy  K:mm a", calendar).toString();

        holder.binding.dateTv.setText(dateFormat);
        holder.binding.captionTv.setText(caption);

        try {
            Picasso.get().load(postImage).placeholder(R.drawable.ic_person_filled_black).into(holder.binding.imageView);
        } catch (Exception e) {
            holder.binding.imageView.setImageResource(R.drawable.ic_person_filled_black);
        }
        holder.binding.imageView.setOnClickListener( new DoubleClick(new DoubleClickListener() {
            @Override
            public void onSingleClick(View view) {
                Toast.makeText(context, "Double click to Like", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDoubleClick(View view) {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("uid", "" + firebaseUser.getUid());
                hashMap.put("value", true);

                if (holder.binding.likeBtn.getTag().equals("like")) {
                    FirebaseDatabase.getInstance().getReference().child("Feeds")
                            .child(modelFeed.getPostId()).child("Likes").child(firebaseUser.getUid()).setValue(hashMap);

                } else {
                    FirebaseDatabase.getInstance().getReference().child("Feeds")
                            .child(modelFeed.getPostId()).child("Likes").child(firebaseUser.getUid()).removeValue();
                }
            }
        }, 600));
        holder.binding.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheet(modelFeed, holder);
            }
        });
        holder.binding.usernameTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkUser(modelFeed, holder);
            }
        });
        holder.binding.profileIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkUser(modelFeed, holder);
            }
        });
        isLiked(modelFeed.getPostId(), holder.binding.likeBtn);
        noLikes(modelFeed.getPostId(), holder.binding.noLikesTv);
        holder.binding.likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("uid", "" + firebaseUser.getUid());
                hashMap.put("value", true);

                if (holder.binding.likeBtn.getTag().equals("like")) {
                    FirebaseDatabase.getInstance().getReference().child("Feeds")
                            .child(modelFeed.getPostId()).child("Likes").child(firebaseUser.getUid()).setValue(hashMap);

                } else {
                    FirebaseDatabase.getInstance().getReference().child("Feeds")
                            .child(modelFeed.getPostId()).child("Likes").child(firebaseUser.getUid()).removeValue();
                }
            }
        });
        isSaved(modelFeed.getPostId(), holder.binding.savePostBtn);
        holder.binding.savePostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("uid", uid);
                hashMap.put("postId", postId);
                hashMap.put("postImage", postImage);
                hashMap.put("caption", caption);

                if (holder.binding.savePostBtn.getTag().equals("save")) {
                    FirebaseDatabase.getInstance().getReference().child("Users")
                            .child(firebaseUser.getUid()).child("savedPost").child(modelFeed.getPostId()).setValue(hashMap);

                } else {
                    FirebaseDatabase.getInstance().getReference().child("Users")
                            .child(firebaseUser.getUid()).child("savedPost").child(modelFeed.getPostId()).removeValue();
                }
            }
        });
        holder.binding.noLikesTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity myActivity = (MainActivity) context;

                Toolbar toolbar = myActivity.findViewById(R.id.toolbar);
                toolbar.setVisibility(View.GONE);

                Bundle bundle = new Bundle();
                bundle.putString("postId", postId);
                LikesFragment likesFragment = new LikesFragment();
                likesFragment.setArguments(bundle);
                FragmentTransaction transaction =  myActivity.getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.main_container, likesFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        holder.binding.commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, CommentActivity.class);
                intent.putExtra("postId", postId);
                intent.putExtra("uid", uid);
                context.startActivity(intent);
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void isSaved(String postId, ImageView savePostBtn) {
        FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid()).child("savedPost").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(postId).exists()) {
                    savePostBtn.setImageResource(R.drawable.ic_saved_black);
                    savePostBtn.setTag("saved");
                } else {
                    savePostBtn.setImageResource(R.drawable.ic_save_black);
                    savePostBtn.setTag("save");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void isLiked(String postId, ImageView likeBtn) {
        FirebaseDatabase.getInstance().getReference().child("Feeds").child(postId).child("Likes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(firebaseUser.getUid()).exists()) {
                    likeBtn.setImageResource(R.drawable.ic_heart_filled);
                    likeBtn.setTag("liked");
                } else {
                    likeBtn.setImageResource(R.drawable.ic_heart_black);
                    likeBtn.setTag("like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void noLikes(String postId, TextView noLikesTv) {
        FirebaseDatabase.getInstance().getReference().child("Feeds").child(postId).child("Likes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                noLikesTv.setText(dataSnapshot.getChildrenCount() + " likes");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadUserDetails(ModelFeed modelFeed, HolderFeed holder) {
        String uid = modelFeed.getUid();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String username = "" + snapshot.child("username").getValue();
                        String profileImage = "" + snapshot.child("profileImage").getValue();

                        holder.binding.usernameTv.setText(username);
                        try {
                            Picasso.get().load(profileImage).placeholder(R.drawable.ic_person_filled_black).into(holder.binding.profileIv);
                        } catch (Exception e) {
                            holder.binding.profileIv.setImageResource(R.drawable.ic_person_filled_black);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void showBottomSheet(ModelFeed modelFeed, HolderFeed holder) {
        String postId = modelFeed.getPostId();
        String imageUri = modelFeed.getPostImage();

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bs_feed_options);

        LinearLayout shareLl = dialog.findViewById(R.id.shareLl);
        LinearLayout linkLL = dialog.findViewById(R.id.linkLL);
        LinearLayout reportLL = dialog.findViewById(R.id.reportLL);
        TextView addFavTv = dialog.findViewById(R.id.addFavTv);
        TextView hideTv = dialog.findViewById(R.id.hideTv);
        TextView followTv = dialog.findViewById(R.id.followTv);
        TextView archiveTv = dialog.findViewById(R.id.archiveTv);
        TextView deleteTv = dialog.findViewById(R.id.deleteTv);
        TextView editTv = dialog.findViewById(R.id.editTv);
        TextView hideLikeCountTv = dialog.findViewById(R.id.hideLikeCountTv);
        TextView turnCommentTv = dialog.findViewById(R.id.turnCommentTv);

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

        checkUsers(modelFeed, holder, reportLL, addFavTv, deleteTv, editTv, hideLikeCountTv, archiveTv, hideTv, followTv, turnCommentTv);
        followTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkIsFollowingToUnfollow(modelFeed, holder, followTv);
            }
        });
        editTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent intent = new Intent(context, EditPostActivity.class);
                intent.putExtra("postId", postId);
                context.startActivity(intent);
            }
        });
        deleteTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete")
                        .setMessage("Are you sure want to Delete This Post ?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                deletePost(postId);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .show();
                dialog.dismiss();
            }
        });
        shareLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
            }
        });
        linkLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
            }
        });
        reportLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
            }
        });
    }
    private void deletePost(String postId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Feeds");
        reference.child(postId)
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context, "Post Deleted...!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    String uid;
    private void checkUsers(ModelFeed modelFeed, HolderFeed holder, LinearLayout reportLL, TextView addFavTv, TextView deleteTv, TextView editTv, TextView hideLikeCountTv, TextView archiveTv, TextView hideTv, TextView followTv, TextView turnCommentTv) {
        String userUid = modelFeed.getUid();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null) {

            uid = firebaseUser.getUid();
        }

        if (uid.equals(userUid)) {

            reportLL.setVisibility(View.GONE);
            addFavTv.setVisibility(View.GONE);
            hideTv.setVisibility(View.GONE);
            followTv.setVisibility(View.GONE);

        } else {

            deleteTv.setVisibility(View.GONE);
            editTv.setVisibility(View.GONE);
            hideLikeCountTv.setVisibility(View.GONE);
            archiveTv.setVisibility(View.GONE);
            turnCommentTv.setVisibility(View.GONE);

        }
    }
    private void checkIsFollowingToUnfollow(ModelFeed modelFeed, HolderFeed holder, TextView followTv) {
        String userUid = modelFeed.getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseUser.getUid()).child("following").child(userUid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        isFollowing = snapshot.exists();
                        if (isFollowing){
                            unFollowUser(userUid);
                            followTv.setText("unFollow");
                        }
                        else{
                            followUser(userUid);
                            followTv.setText("follow");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    private void unFollowUser(String userUid) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseUser.getUid()).child("following").child(userUid)
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context, "You unFollowed this user...!!!!", Toast.LENGTH_SHORT).show();
                        removeFromUserFollowers(userUid);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void removeFromUserFollowers(String userUid) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(userUid).child("followers").child(firebaseUser.getUid())
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void followUser(String userUid) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid", "" + firebaseUser.getUid());
        hashMap.put("userUid", userUid);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseUser.getUid()).child("following").child(userUid)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context, "You are now following this user...!!!!", Toast.LENGTH_SHORT).show();
                        addToUserFollowers(userUid);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void addToUserFollowers(String userUid) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid", "" + userUid);
        hashMap.put("userUid", firebaseUser.getUid());

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(userUid).child("followers").child(firebaseUser.getUid())
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void checkUser(ModelFeed modelFeed, HolderFeed holder) {
        String userUid = modelFeed.getUid();


        if (firebaseUser != null) {
            uid = firebaseUser.getUid();
            if (uid.equals(userUid)) {
                MainActivity myActivity = (MainActivity) context;

                Toolbar toolbar = myActivity.findViewById(R.id.toolbar);
                toolbar.setVisibility(View.GONE);

                ProfileFragment profileFragment = new ProfileFragment();
                FragmentTransaction transaction =  myActivity.getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.main_container, profileFragment);
                transaction.addToBackStack(null);
                transaction.commit();


            } else {
                MainActivity myActivity = (MainActivity) context;

                Toolbar toolbar = myActivity.findViewById(R.id.toolbar);
                toolbar.setVisibility(View.GONE);

                Bundle bundle = new Bundle();
                bundle.putString("userUid", userUid);
                UserProfileFragment userProfileFragment = new UserProfileFragment();
                userProfileFragment.setArguments(bundle);
                FragmentTransaction transaction =  myActivity.getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.main_container, userProfileFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        }
    }
    @Override
    public int getItemCount() {
        return feedArrayList.size();
    }

    class HolderFeed extends RecyclerView.ViewHolder {

        RowFeedBinding binding;

        public HolderFeed(@NonNull View itemView) {
            super(itemView);

            binding = RowFeedBinding.bind(itemView);

        }
    }
}
