package com.example.messageapp.Adapters;


import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messageapp.databinding.ActivityItemContainerReceiveMessageBinding;
import com.example.messageapp.databinding.ActivityItemContainerSentMessageBinding;
import com.example.messageapp.models.ChatMessage;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<ChatMessage> chatMessages;
    private final String senderId;
    private final Bitmap receiverProfileImage;
public static final int VIEW_TYPE_SENT = 1;
public static final int VIEW_TYPE_RECEIVED = 2;

    public ChatAdapter(List<ChatMessage> chatMessages , String senderId , Bitmap receiverProfileImage) {
        this.chatMessages = chatMessages;
        this.senderId = senderId;
        this.receiverProfileImage = receiverProfileImage;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent , int viewType) {
        if (viewType == VIEW_TYPE_SENT){
            return new SentMessageViewHolder(ActivityItemContainerSentMessageBinding.inflate(LayoutInflater.from(parent.getContext()),
                    parent , false)
            );
        }else {
            return new ReceivedMessageViewHolder(ActivityItemContainerReceiveMessageBinding.inflate(LayoutInflater.from(parent.getContext()),
                    parent , false
                  )
            );
        }
    }
@Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder , int position){
        if (getItemViewType(position) == VIEW_TYPE_SENT){
            ((SentMessageViewHolder)holder).setData(chatMessages.get(position));

        }else {
            ((ReceivedMessageViewHolder)holder).setData(chatMessages.get(position) , receiverProfileImage);
        }
}
@Override
    public int getItemCount() {return chatMessages.size();}
    @Override
    public int getItemViewType(int position){
        if (chatMessages.get(position).senderId.equals(senderId)){
            return VIEW_TYPE_SENT;

        }else {
            return VIEW_TYPE_RECEIVED;
        }
    }


  static class SentMessageViewHolder extends RecyclerView.ViewHolder{
        private final ActivityItemContainerSentMessageBinding binding;


        SentMessageViewHolder(@NonNull ActivityItemContainerSentMessageBinding itemContainerSentMessageBinding){
            super(itemContainerSentMessageBinding.getRoot());
            binding = itemContainerSentMessageBinding;
        }
        void setData (ChatMessage chatMessage){
            binding.textMessage.setText(chatMessage.message);
            binding.textDateTime.setText(chatMessage.dateTime);
        }


}

static  class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        private final ActivityItemContainerReceiveMessageBinding binding ;
        ReceivedMessageViewHolder (@NonNull ActivityItemContainerReceiveMessageBinding itemContainerReceiveMessageBinding){
            super(itemContainerReceiveMessageBinding.getRoot());
            binding = itemContainerReceiveMessageBinding;
        }
        void setData(ChatMessage chatMessage , Bitmap receiverProfileImage){
            binding.textmessage.setText(chatMessage.message);
            binding.textDateTime.setText(chatMessage.dateTime);
            // binding.imageProfile.setImageBitmap(receiverprofileImage);
        }
     }
 }
