package com.anurag.instagram.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.anurag.instagram.Fragments.UserProfileFragment;
import com.anurag.instagram.MainActivity;
import com.anurag.instagram.Model.ModelFollowers;
import com.anurag.instagram.Model.ModelLikes;
import com.anurag.instagram.R;
import com.anurag.instagram.databinding.RowFollowersBinding;
import com.anurag.instagram.databinding.RowLikesBinding;
import com.anurag.instagram.databinding.RowUsersBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterLikes extends RecyclerView.Adapter<AdapterLikes.HolderLikes>{
    private Context context;
    public ArrayList<ModelLikes> likesArrayList;
    boolean isFollowing = false;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    public AdapterLikes(Context context, ArrayList<ModelLikes> likesArrayList) {
        this.context = context;
        this.likesArrayList = likesArrayList;



    }

    @NonNull
    @Override
    public AdapterLikes.HolderLikes onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.row_likes, parent, false);
        return new AdapterLikes.HolderLikes(view);

    }

    @Override
    public void onBindViewHolder(@NonNull AdapterLikes.HolderLikes holder, int position) {

        final ModelLikes modelLikes = likesArrayList.get(position);
        String uid = modelLikes.getUid();


        loadLikedUserDetails(modelLikes , holder);
        checkIsFollowing(modelLikes, holder);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity myActivity = (MainActivity) context;

                Toolbar toolbar = myActivity.findViewById(R.id.toolbar);
                toolbar.setVisibility(View.GONE);

                Bundle bundle = new Bundle();
                bundle.putString("userUid", uid);
                UserProfileFragment userProfileFragment = new UserProfileFragment();
                userProfileFragment.setArguments(bundle);
                FragmentTransaction transaction =  myActivity.getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.main_container, userProfileFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

    }

    private void checkIsFollowing(ModelLikes modelLikes, HolderLikes holder) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("following").child(modelLikes.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        isFollowing = snapshot.exists();
                        if (isFollowing){
                            holder.binding.followBtn.setText("Following");

                        }
                        else{
                            holder.binding.followBtn.setText("Follow");

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadLikedUserDetails(ModelLikes modelFeed, AdapterLikes.HolderLikes holder) {
        String uid = modelFeed.getUid();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String username = "" + snapshot.child("username").getValue();
                        String name = "" + snapshot.child("name").getValue();
                        String profileImage = "" + snapshot.child("profileImage").getValue();

                        holder.binding.usernameTv.setText(username);
                        holder.binding.nameTv.setText(name);
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

    @Override
    public int getItemCount() {
        return likesArrayList.size();
    }

    class HolderLikes extends RecyclerView.ViewHolder {

        RowLikesBinding binding;

        public HolderLikes(@NonNull View itemView) {
            super(itemView);

            binding = RowLikesBinding.bind(itemView);

        }
    }
}
