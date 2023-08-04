package com.example.messageapp.Adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.example.messageapp.databinding.ActivityItemContainerUserBinding;
import com.example.messageapp.listeners.UserListener;
import com.example.messageapp.models.User;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> {
    private final List<User> users;
    private final UserListener userListener;

    public UsersAdapter(List<User> users, UserListener userListener){
        this.users = users;
        this.userListener = userListener;


    }
@NonNull
@Override
public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent , int viewType){

        ActivityItemContainerUserBinding itemContaineruserBinding = ActivityItemContainerUserBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent, false
        );
        return new UserViewHolder(itemContaineruserBinding);
}
@Override
public void onBindViewHolder(@NonNull UserViewHolder holder , int position){
        holder.setUserData(users.get(position));

}
@Override
public int getItemCount(){
        return users.size();
}
class UserViewHolder extends RecyclerView.ViewHolder{
    ActivityItemContainerUserBinding binding;

    UserViewHolder(ActivityItemContainerUserBinding itemContainerUserBinding){
        super(itemContainerUserBinding.getRoot());
        binding = itemContainerUserBinding;
    }

    void setUserData(User user ){
        binding.textName.setText(user.name);
        binding.textEmail.setText(user.email);
        // binding.imageProfile.setImage(getUserImage(user.Image));
        binding.getRoot().setOnClickListener(view -> userListener.onUserClicked(user));
    }
}
private Bitmap getUserImage(String encodedImage){
        byte[] bytes = Base64.decode(encodedImage , Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
}

}
