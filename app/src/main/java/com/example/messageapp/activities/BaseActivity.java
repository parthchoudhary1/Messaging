package com.example.messageapp.activities;

import android.os.Bundle;
import android.preference.Preference;

import androidx.appcompat.app.AppCompatActivity;

import com.example.messageapp.utilities.Constants;
import com.example.messageapp.utilities.PreferencesManager;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class BaseActivity extends AppCompatActivity {

private DocumentReference documentReference;

@Override
protected void onCreate(Bundle savedInstanceState){
    super.onCreate(savedInstanceState);
    PreferencesManager preferencesManager = new PreferencesManager(getApplicationContext());
    FirebaseFirestore database = FirebaseFirestore.getInstance();
    documentReference = database.collection(Constants.KEY_COLLECTION_USERS)
            .document(preferencesManager.getString(Constants.KEY_USER_ID));
}

    @Override
    protected void onPause() {
        super.onPause();
        documentReference.update(Constants.KEY_AVAILABILITY,0);
    }
    @Override
    protected void onResume() {
        super.onResume();
        documentReference.update(Constants.KEY_AVAILABILITY, 1);
    }


}
