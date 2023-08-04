package com.example.messageapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.messageapp.R;
import com.example.messageapp.databinding.ActivitySignInBinding;
import com.example.messageapp.utilities.Constants;
import com.example.messageapp.utilities.PreferencesManager;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Sign_In extends AppCompatActivity {
    Button btnSIGNIN, btnSIGNUP;
    private PreferencesManager PreferencesManager;
    private ActivitySignInBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        if (PreferencesManager.getBoolean(Constants.KEY_IS_SIGNED_IN)) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();

        }
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
    }

    private void setListeners() {
        binding.textCreateNewAcount.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), Sign_up.class)));
        binding.SignInBtn.setOnClickListener(view -> {
            if (isValidSignInDetails()) {
                signIn();
            }
        });
    }

    private void signIn() {
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.KEY_EMAIL, binding.editTextTextEmailAddress2.getText().toString())
                .whereEqualTo(Constants.KEY_PASSWORD, binding.inputPassword.getText().toString())
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null
                            && task.getResult().getDocuments().size() > 0) {
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        PreferencesManager.putBoolean(Constants.KEY_AVAILABILITY, true);
                        PreferencesManager.putString(Constants.KEY_USER_ID, documentSnapshot.getId());
                        PreferencesManager.putString(Constants.KEY_NAME, documentSnapshot.getString(Constants.KEY_NAME));
                        PreferencesManager.putString(Constants.KEY_IMAGE, documentSnapshot.getString(Constants.KEY_IMAGE));
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);


                    } else {
                        loading(false);
                        showToast("Unable To Sign In");
                    }
                });

    }



    private void loading(Boolean isLoading) {
        if (isLoading) {
            binding.SignInBtn.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.progressBar.setVisibility(View.INVISIBLE);
            binding.SignInBtn.setVisibility(View.VISIBLE);
        }

    }
    private void showToast(String message) {
        Toast.makeText(getApplicationContext() , message , Toast.LENGTH_SHORT).show();
    }

    private Boolean isValidSignInDetails(){
        if (binding.editTextTextEmailAddress2.getText().toString().trim().isEmpty()){
            showToast("Enter email");
            return false ;
        }else if (!Patterns.EMAIL_ADDRESS.matcher(binding.editTextTextEmailAddress2.getText().toString()).matches()){
            showToast("Enter valid Email");
            return false;
        }else if (binding.inputPassword.getText().toString().trim().isEmpty()){
            showToast("Enter Password");
            return false;
        }else{
            return true;
        }

    }
}