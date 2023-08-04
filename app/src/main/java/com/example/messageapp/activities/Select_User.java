package com.example.messageapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.messageapp.Adapters.UsersAdapter;
import com.example.messageapp.databinding.ActivitySelectUserBinding;
import com.example.messageapp.listeners.UserListener;
import com.example.messageapp.models.User;
import com.example.messageapp.utilities.Constants;
import com.example.messageapp.utilities.PreferencesManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class Select_User extends BaseActivity implements UserListener {
    private ActivitySelectUserBinding binding;
    private PreferencesManager preferencesManager;

    @Override
    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        binding = ActivitySelectUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferencesManager = new PreferencesManager(getApplicationContext());
        setListeners();
        getUsers();
    }
    private void setListeners(){
        binding.imageBack.setOnClickListener(view -> onBackPressed());
    }


     private void getUsers(){
        loading(true);
         FirebaseFirestore database = FirebaseFirestore.getInstance();
         database.collection(Constants.KEY_COLLECTION_USERS).get().addOnCompleteListener(task ->{
             loading(false);
             String currentUserId = preferencesManager.getString(Constants.KEY_USER_ID);
             if (task.isSuccessful() && task.getResult() != null){
                 List<User> users = new ArrayList<>();
                 for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()){
                     if (currentUserId.equals(queryDocumentSnapshot.getId())){
                         continue;
                     }
                     User user = new User();
                     user.name = queryDocumentSnapshot.getString(Constants.KEY_NAME);
                     user.email = queryDocumentSnapshot.getString(Constants.KEY_EMAIL);
                     user.image = queryDocumentSnapshot.getString(Constants.KEY_IMAGE);
                     user.token = queryDocumentSnapshot.getString(Constants.KEY_FCM_TOKEN);
                             user.id = queryDocumentSnapshot.getId();
                     users.add(user);
                 }
                 if (users.size() > 0) {
                     UsersAdapter usersAdapter = new UsersAdapter(users,this);
                     binding.userRecyclerView.setAdapter(usersAdapter);
                     binding.userRecyclerView.setVisibility(View.VISIBLE);
                 } else {
                     showErrorMessage();
                 }
             } else {
                 showErrorMessage();
             }
         });

     }
private void loading (boolean isLoading) {
        if (isLoading){
            binding.progressBar2.setVisibility(View.VISIBLE);
        }else{
            binding.progressBar2.setVisibility(View.INVISIBLE);
        }
}
 private void showErrorMessage() {
        binding.textErrorMessage.setText(String.format("%s","No user available"));
        binding.textErrorMessage.setVisibility(View.VISIBLE);
 }



    @Override
    public void onUserClicked(com.example.messageapp.models.User user) {
        Intent intent = new Intent(getApplicationContext() , ChatActivity.class);
        intent.putExtra(Constants.KEY_USER , user);
        startActivity(intent);
        finish();
    }
}