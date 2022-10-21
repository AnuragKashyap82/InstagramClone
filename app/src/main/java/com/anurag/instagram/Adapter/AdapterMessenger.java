package com.anurag.instagram.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.anurag.instagram.Fragments.UserProfileFragment;
import com.anurag.instagram.MainActivity;
import com.anurag.instagram.MessageViewActivity;
import com.anurag.instagram.Model.ModelMessenger;
import com.anurag.instagram.Model.ModelUsers;
import com.anurag.instagram.R;
import com.anurag.instagram.databinding.RowMessengerBinding;
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

public class AdapterMessenger extends RecyclerView.Adapter<AdapterMessenger.HolderMessenger>{

    private Context context;
    public ArrayList<ModelMessenger> messengerArrayList;

    public AdapterMessenger(Context context, ArrayList<ModelMessenger> messengerArrayList) {
        this.context = context;
        this.messengerArrayList = messengerArrayList;

    }

    @NonNull
    @Override
    public AdapterMessenger.HolderMessenger onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.row_messenger, parent, false);
        return new AdapterMessenger.HolderMessenger(view);

    }

    @Override
    public void onBindViewHolder(@NonNull AdapterMessenger.HolderMessenger holder, int position) {

        final ModelMessenger modelMessenger = messengerArrayList.get(position);
        String uid = modelMessenger.getUid();


        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        loadUsersDetails(modelMessenger , holder);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MessageViewActivity.class);
                intent.putExtra("uid", modelMessenger.getUid());
                context.startActivity(intent);
            }
        });

    }

    private void loadUsersDetails(ModelMessenger modelUsers, HolderMessenger holder) {
        String uid = modelUsers.getUid();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String username = "" + snapshot.child("username").getValue();
                        String name = "" + snapshot.child("name").getValue();
                        String profileImage = "" + snapshot.child("profileImage").getValue();

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
        return messengerArrayList.size();
    }

    class HolderMessenger extends RecyclerView.ViewHolder {

        RowMessengerBinding binding;

        public HolderMessenger(@NonNull View itemView) {
            super(itemView);

            binding = RowMessengerBinding.bind(itemView);

        }
    }
}
