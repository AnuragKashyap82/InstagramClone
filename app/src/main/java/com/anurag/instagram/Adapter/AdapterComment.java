package com.anurag.instagram.Adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.anurag.instagram.Model.ModelComment;
import com.anurag.instagram.R;
import com.anurag.instagram.databinding.RowCommentsBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterComment extends RecyclerView.Adapter<AdapterComment.HolderComment>{
    private Context context;
    public ArrayList<ModelComment> commentArrayList;

    public AdapterComment(Context context, ArrayList<ModelComment> commentArrayList) {
        this.context = context;
        this.commentArrayList = commentArrayList;


    }

    @NonNull
    @Override
    public HolderComment onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.row_comments, parent, false);
        return new HolderComment(view);

    }

    @Override
    public void onBindViewHolder(@NonNull HolderComment holder, int position) {

        final ModelComment modelComment = commentArrayList.get(position);
        String postId = modelComment.getPostId();
        String comment = modelComment.getComment();
        String uid = modelComment.getUid();
        String commentId = modelComment.getTimestamp();

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(commentId));
        String dateFormat = DateFormat.format("dd/MM/yyyy  K:mm a", calendar).toString();

        holder.binding.dateTv.setText(dateFormat);
        holder.binding.commentTv.setText(comment);

        loadUserDetails(modelComment, holder);

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showBottomSheet(modelComment, holder);

                return true;
            }
        });
    }
    private void showBottomSheet(ModelComment modelComment, HolderComment holder) {
        String postId = modelComment.getPostId();
        String commentId = modelComment.getTimestamp();


        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bs_feed_options);

        LinearLayout shareLl = dialog.findViewById(R.id.shareLl);
        LinearLayout linkLL = dialog.findViewById(R.id.linkLL);
        LinearLayout reportLL = dialog.findViewById(R.id.reportLL);
        TextView addFavTv = dialog.findViewById(R.id.addFavTv);
        TextView hideTv = dialog.findViewById(R.id.hideTv);
        TextView followTv = dialog.findViewById(R.id.folloTv);
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

        checkUsers(modelComment, holder, shareLl, linkLL, reportLL, addFavTv, deleteTv, editTv, hideLikeCountTv, archiveTv, hideTv, followTv, turnCommentTv);

        deleteTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete")
                        .setMessage("Are you sure want to Delete This Comment ?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                deleteComment(postId, commentId);
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
        reportLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
            }
        });
    }

    private void deleteComment(String postId, String commentId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Feeds");
        reference.child(postId).child("Comments").child(commentId)
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context, "Comment Deleted...!", Toast.LENGTH_SHORT).show();
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
    private void checkUsers(ModelComment modelFeed, HolderComment holder, LinearLayout reportLL, LinearLayout linkLL, LinearLayout ll, TextView addFavTv, TextView deleteTv, TextView editTv, TextView hideLikeCountTv, TextView archiveTv, TextView hideTv, TextView followTv, TextView turnCommentTv) {
        String userUid = modelFeed.getUid();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null) {

            uid = firebaseUser.getUid();
        }

        if (uid.equals(userUid)) {

            reportLL.setVisibility(View.GONE);
            linkLL.setVisibility(View.GONE);
            ll.setVisibility(View.GONE);
            addFavTv.setVisibility(View.GONE);
            hideTv.setVisibility(View.GONE);
            followTv.setVisibility(View.GONE);
            deleteTv.setVisibility(View.VISIBLE);
            editTv.setVisibility(View.GONE);
            hideLikeCountTv.setVisibility(View.GONE);
            archiveTv.setVisibility(View.GONE);
            turnCommentTv.setVisibility(View.GONE);

        } else {

            reportLL.setVisibility(View.GONE);
            linkLL.setVisibility(View.GONE);
            ll.setVisibility(View.VISIBLE);
            addFavTv.setVisibility(View.GONE);
            hideTv.setVisibility(View.GONE);
            followTv.setVisibility(View.GONE);
            deleteTv.setVisibility(View.GONE);
            editTv.setVisibility(View.GONE);
            hideLikeCountTv.setVisibility(View.GONE);
            archiveTv.setVisibility(View.GONE);
            turnCommentTv.setVisibility(View.GONE);

        }
    }

    private void loadUserDetails(ModelComment modelFeed, HolderComment holder) {
        String uid = modelFeed.getUid();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String username = "" + snapshot.child("username").getValue();
                        String profileImage = "" + snapshot.child("profileImage").getValue();

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

//    private void showBottomSheet(ModelFeed modelFeed, AdapterFeed.HolderFeed holder) {
//        String postId = modelFeed.getPostId();
//        String imageUri = modelFeed.getPostImage();
//
//        final Dialog dialog = new Dialog(context);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setContentView(R.layout.bs_feed_options);
//
//        LinearLayout shareLl = dialog.findViewById(R.id.shareLl);
//        LinearLayout linkLL = dialog.findViewById(R.id.linkLL);
//        LinearLayout reportLL = dialog.findViewById(R.id.reportLL);
//        TextView addFavTv = dialog.findViewById(R.id.addFavTv);
//        TextView hideTv = dialog.findViewById(R.id.hideTv);
//        TextView unFollowTv = dialog.findViewById(R.id.unFollowTv);
//        TextView archiveTv = dialog.findViewById(R.id.archiveTv);
//        TextView deleteTv = dialog.findViewById(R.id.deleteTv);
//        TextView editTv = dialog.findViewById(R.id.editTv);
//        TextView hideLikeCountTv = dialog.findViewById(R.id.hideLikeCountTv);
//        TextView turnCommentTv = dialog.findViewById(R.id.turnCommentTv);
//
//        dialog.show();
//        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialog.getWindow().getAttributes().windowAnimations = R.style.dialogAnimation;
//        dialog.getWindow().setGravity(Gravity.BOTTOM);
//
//        checkUsers(modelFeed, holder, reportLL, addFavTv, deleteTv, editTv, hideLikeCountTv, archiveTv, hideTv, unFollowTv, turnCommentTv);
//
//        editTv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dialog.dismiss();
//                Intent intent = new Intent(context, EditPostActivity.class);
//                intent.putExtra("postId", postId);
//                context.startActivity(intent);
//            }
//        });
//
//        deleteTv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                builder.setTitle("Delete")
//                        .setMessage("Are you sure want to Delete This Post ?")
//                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                deletePost(postId);
//                            }
//                        })
//                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                dialogInterface.dismiss();
//                            }
//                        })
//                        .show();
//                dialog.dismiss();
//            }
//        });
//        shareLl.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                dialog.dismiss();
//            }
//        });
//        linkLL.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                dialog.dismiss();
//            }
//        });
//        reportLL.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                dialog.dismiss();
//            }
//        });
//    }
//
//    private void deletePost(String postId) {
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Feeds");
//        reference.child(postId)
//                .removeValue()
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void unused) {
//                        Toast.makeText(context, "Post Deleted...!", Toast.LENGTH_SHORT).show();
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }

    @Override
    public int getItemCount() {
        return commentArrayList.size();
    }

    class HolderComment extends RecyclerView.ViewHolder {

        RowCommentsBinding binding;

        public HolderComment(@NonNull View itemView) {
            super(itemView);

            binding = RowCommentsBinding.bind(itemView);

        }
    }
}
