package com.anurag.instagram.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.anurag.instagram.MainActivity;
import com.anurag.instagram.Model.ModelStatus;
import com.anurag.instagram.Model.ModelUserStatus;
import com.anurag.instagram.R;
import com.anurag.instagram.databinding.RowStoryBinding;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import omari.hamza.storyview.StoryView;
import omari.hamza.storyview.callback.StoryClickListeners;
import omari.hamza.storyview.model.MyStory;

public class AdapterStatus extends RecyclerView.Adapter<AdapterStatus.TopStatusViewHolder> {

    Context context;
    ArrayList<ModelUserStatus> modelUserStatuses;

    public AdapterStatus(Context context, ArrayList<ModelUserStatus> modelUserStatuses) {
        this.context = context;
        this.modelUserStatuses = modelUserStatuses;
    }

    @NonNull
    @Override
    public TopStatusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_story, parent, false);
        return new TopStatusViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopStatusViewHolder holder, int position) {

        ModelUserStatus modelUserStatus = modelUserStatuses.get(position);

        ModelStatus lastModelStatus = modelUserStatus.getStatuses().get(modelUserStatus.getStatuses().size() - 1);
        Glide.with(context).load(modelUserStatus.getProfileImage()).into(holder.binding.image);
        holder.binding.circularStatusView.setPortionsCount(modelUserStatus.getStatuses().size());
        holder.binding.usernameTv.setText(modelUserStatus.getUsername());

        holder.binding.circularStatusView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<MyStory> myStories = new ArrayList<>();
                for (ModelStatus modelStatus : modelUserStatus.getStatuses()){
                    myStories.add(new MyStory(modelStatus.getImageUrl()));
                }
                new StoryView.Builder(((MainActivity)context).getSupportFragmentManager())
                        .setStoriesList(myStories) // Required
                        .setStoryDuration(5000) // Default is 2000 Millis (2 Seconds)
                        .setTitleText(modelUserStatus.getUsername()) // Default is Hidden
                        .setSubtitleText("") // Default is Hidden
                        .setTitleLogoUrl(modelUserStatus.getProfileImage()) // Default is Hidden
                        .setStoryClickListeners(new StoryClickListeners() {
                            @Override
                            public void onDescriptionClickListener(int position) {
                                //your action
                            }

                            @Override
                            public void onTitleIconClickListener(int position) {
                                //your action
                            }
                        }) // Optional Listeners
                        .build() // Must be called before calling show method
                        .show();
            }
        });
    }
    @Override
    public int getItemCount() {
        return modelUserStatuses.size();
    }

    public class TopStatusViewHolder extends RecyclerView.ViewHolder{
        RowStoryBinding binding;
        public TopStatusViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = RowStoryBinding.bind(itemView);
        }
    }
}