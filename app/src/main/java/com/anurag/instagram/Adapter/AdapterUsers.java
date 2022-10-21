package com.anurag.instagram.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.anurag.instagram.Fragments.UserProfileFragment;
import com.anurag.instagram.MainActivity;
import com.anurag.instagram.Model.ModelUsers;
import com.anurag.instagram.R;
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

public class AdapterUsers  extends RecyclerView.Adapter<AdapterUsers.HolderUsers>{
    private Context context;
    public ArrayList<ModelUsers> usersArrayList;

    public AdapterUsers(Context context, ArrayList<ModelUsers> usersArrayList) {
        this.context = context;
        this.usersArrayList = usersArrayList;

    }

    @NonNull
    @Override
    public HolderUsers onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.row_users, parent, false);
        return new HolderUsers(view);

    }

    @Override
    public void onBindViewHolder(@NonNull HolderUsers holder, int position) {

        final ModelUsers modelUsers = usersArrayList.get(position);
        String uid = modelUsers.getUid();


        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        loadFollowersDetails(modelUsers , holder);

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

    private void loadFollowersDetails(ModelUsers modelUsers, HolderUsers holder) {
        String uid = modelUsers.getUid();

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
        return usersArrayList.size();
    }

    class HolderUsers extends RecyclerView.ViewHolder {

        RowUsersBinding binding;

        public HolderUsers(@NonNull View itemView) {
            super(itemView);

            binding = RowUsersBinding.bind(itemView);

        }
    }
}
