package com.example.messageapp.Adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messageapp.databinding.ActivityItemContainerRecentConversationBinding;
import com.example.messageapp.listeners.ConversionListener;
import com.example.messageapp.models.ChatMessage;
import com.example.messageapp.models.User;

import java.util.List;

public class RecentConversationAdapter extends RecyclerView.Adapter<RecentConversationAdapter.ConversionViewHolder> {
    private final List<ChatMessage> chatmessages;
    private final ConversionListener conversionListener;

    public RecentConversationAdapter(List<ChatMessage> chatmessages, ConversionListener conversionListener) {
        this.chatmessages = chatmessages;
        this.conversionListener = conversionListener;
    }

    @NonNull
    @Override
    public ConversionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ConversionViewHolder(ActivityItemContainerRecentConversationBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        )
        );

    }

    @Override
    public void onBindViewHolder(@NonNull RecentConversationAdapter.ConversionViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return chatmessages.size();
    }

    class ConversionViewHolder extends RecyclerView.ViewHolder {
        ActivityItemContainerRecentConversationBinding binding;

        ConversionViewHolder(ActivityItemContainerRecentConversationBinding itemContainerRecentConversionBinding) {
            super(itemContainerRecentConversionBinding.getRoot());
            binding = itemContainerRecentConversionBinding;

        }

        void setData(ChatMessage chatMessage) {
            binding.imageProfile.setImageBitmap(getConversationImage(chatMessage.conversionImage));
            binding.textName.setText(chatMessage.conversionName);
            binding.textRecentMessage.setText(chatMessage.message);
            binding.getRoot().setOnClickListener(view -> {
                User user = new User();
                user.id = chatMessage.conversionId;
                user.name = chatMessage.conversionName;
                user.image = chatMessage.conversionImage;
                conversionListener.onConversionClicked(user);
            });

        }


    }
    private Bitmap getConversationImage(String encodedImage){
        byte[] bytes = Base64.decode(encodedImage,Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);

    }
}

