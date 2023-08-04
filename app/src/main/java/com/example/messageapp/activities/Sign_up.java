package com.example.messageapp.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.example.messageapp.R;
import com.example.messageapp.databinding.ActivitySignUpBinding;
import com.example.messageapp.utilities.Constants;
import com.example.messageapp.utilities.PreferencesManager;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.units.qual.C;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

public class Sign_up extends AppCompatActivity {

    Button btnSignUp, btnSignin;
    private PreferencesManager preferencesManager;
    private ActivitySignUpBinding binding;
    private String encodedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferencesManager = new PreferencesManager(getApplicationContext());
        setListeners();


        btnSignin = findViewById(R.id.Sign_In_1);

        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myInt = new Intent(getApplicationContext(), Sign_In.class);
                startActivity(myInt);


            }
        });
    }

    private void setListeners() {
        binding.textSignIn.setOnClickListener(view -> onBackPressed());
        binding.buttonSignUp.setOnClickListener(view ->{
            if (isValidSignUpDetails()){
                signup();
            }
        });
        binding.layoutImage.setOnClickListener(view->{
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);

        });
    }



    private void showToast(String msg){
        Toast.makeText(getApplicationContext() , msg , Toast.LENGTH_SHORT).show();
    }

    private void signup() {
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        HashMap<String, Object> user = new HashMap<>();
        user.put(Constants.KEY_NAME, binding.inputName.getText().toString());
        user.put(Constants.KEY_PASSWORD, binding.inputPassword.getText().toString());
        user.put(Constants.KEY_EMAIL, binding.inputEmail.getText().toString());
        user.put(Constants.KEY_IMAGE, encodedImage);
        database.collection(Constants.KEY_COLLECTION_USERS)
                .add(user)
                .addOnSuccessListener(documentReference -> {
                    loading(false);
                    preferencesManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                    preferencesManager.putString(Constants.KEY_USER_ID, documentReference.getId());
                    preferencesManager.putString(Constants.KEY_NAME, binding.inputName.getText().toString());
                    preferencesManager.putString(Constants.KEY_IMAGE, encodedImage);
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .addOnFailureListener(exception -> {
                    loading(false);
                    showToast(exception.getMessage());
                });
    }

        private String encodedImage(Bitmap bitmap) {
            int previewWidth = 15;
            int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
            Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            previewBitmap.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);
            byte[] bytes = byteArrayOutputStream.toByteArray();
            return Base64.encodeToString(bytes , Base64.DEFAULT);
        }

      private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
              new ActivityResultContracts.StartActivityForResult(),
              result -> {
                  if (result.getResultCode() == RESULT_OK){
                      if (result.getData() != null){
                          Uri imageUri = result.getData().getData();
                          try {
                              InputStream inputStream = getContentResolver().openInputStream(imageUri);

                              Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                              binding.imageProfile.setImageBitmap(bitmap);
                              binding.textAddImage.setVisibility(View.GONE);
                              encodedImage = encodedImage(bitmap);
                          }catch (FileNotFoundException e){
                              e.printStackTrace();
                          }
                      }
                  }

              }
      );
        private Boolean isValidSignUpDetails(){
            if (encodedImage == null){
                showToast("Select Image profile");
                return false;
            }else if (binding.inputName.getText().toString().trim().isEmpty()) {
                showToast("Enter Name");
                return false;
            }else if (binding.inputEmail.getText().toString().trim().isEmpty()){
                showToast("Enter Email");
                return false;
            }else if (Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.getText().toString()).matches()){
                showToast("Enter Valid Email");
                return false;
            }else if (binding.inputPassword.getText().toString().trim().isEmpty()){
                showToast("Enter password");
                return false ;
            }else if (binding.inputPassword.getText().toString().trim().isEmpty()){
                showToast("Password and confirmed Password didn't matched");
                return false;
            }else{
                return true;
            }
        }

        private void loading (Boolean isLoading) {
            if (isLoading){
                binding.buttonSignUp.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);

        }else{
            binding.progressBar.setVisibility(View.INVISIBLE);
            binding.buttonSignUp.setVisibility(View.VISIBLE);
        }

    }

}