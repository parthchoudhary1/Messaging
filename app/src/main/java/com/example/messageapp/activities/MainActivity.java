package com.example.messageapp.activities;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;
import com.example.messageapp.Adapters.RecentConversationAdapter;
import com.example.messageapp.R;
import com.example.messageapp.databinding.ActivityMainBinding;
import com.example.messageapp.listeners.ConversionListener;
import com.example.messageapp.models.ChatMessage;
import com.example.messageapp.utilities.Constants;
import com.example.messageapp.utilities.PreferencesManager;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends BaseActivity implements ConversionListener {


    private ActivityMainBinding binding;
    private PreferencesManager preferenceManager;
    private List<ChatMessage> conversations;
    private RecentConversationAdapter conversationsAdapter;
    private FirebaseFirestore database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState == null){
            savedInstanceState = new Bundle();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_user);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferencesManager(getApplicationContext());
        init();
        loadUserDetalis();
        getToken();
        setlisteners();
        listenConversations();

    }


    private void init() {
        conversations = new ArrayList<>();
        conversationsAdapter = new RecentConversationAdapter(conversations, this);
        binding.conversationsRecyclerView.setAdapter(conversationsAdapter);
        database = FirebaseFirestore.getInstance();

    }

    private void setlisteners() {
        binding.imagesignout.setOnClickListener(v -> signOut());
        binding.floatingBtn.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), Select_User.class));
        });
    }

    private void loadUserDetalis() {
        binding.textName.setText(preferenceManager.getString(Constants.KEY_NAME));
        byte[] bytes = Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        binding.imageProfile.setImageBitmap(bitmap);
    }

    private void showToast(String message) {
        android.widget.Toast.makeText(getApplicationContext(), "message", Toast.LENGTH_SHORT).show();

    }

    private void listenConversations() {
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_RECEIVER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);

    }

    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if (error != null)
            return;


if(value !=null)

    {
        for (DocumentChange documentChange : value.getDocumentChanges()) {
            if (documentChange.getType() == DocumentChange.Type.ADDED) {
                String senderId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                String receiverId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                ChatMessage chatMessage = new ChatMessage();
                chatMessage.senderId = senderId;
                chatMessage.receiverId = receiverId;
                if (preferenceManager.getString(Constants.KEY_USER_ID).equals(senderId)) {
                    chatMessage.conversionImage = documentChange.getDocument().getString(Constants.KEY_RECEIVER_IMAGE);
                    chatMessage.conversionName = documentChange.getDocument().getString(Constants.KEY_RECEIVER_IMAGE);
                    chatMessage.conversionId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);


                } else {
                    chatMessage.conversionName = documentChange.getDocument().getString(Constants.KEY_SENDER_NAME);
                    chatMessage.conversionImage = documentChange.getDocument().getString(Constants.KEY_SENDER_IMAGE);
                    chatMessage.conversionId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                }
                chatMessage.message = documentChange.getDocument().getString(Constants.KEY_LAST_MESSAGE);
                chatMessage.dateTime = documentChange.getDocument().getString(Constants.KEY_TIMESTAMP);
                conversations.add(chatMessage);
            } else if (documentChange.getType() == DocumentChange.Type.MODIFIED) {
                for (int i = 0; i < conversations.size(); i++) {
                    String senderId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                    String receiverId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                    if (conversations.get(1).senderId.equals(senderId) && conversations.get(1).receiverId.equals(receiverId)) {
                        conversations.get(i).message = documentChange.getDocument().getString(Constants.KEY_LAST_MESSAGE);
                        conversations.get(1).dateObject = documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);
                        break;
                    }
                }
            }
        }

        Collections.sort(conversations, (obj1, obj2) -> obj2.dateObject.compareTo(obj1.dateObject));
        conversationsAdapter.notifyDataSetChanged();
        binding.conversationsRecyclerView.smoothScrollToPosition(0);
        binding.conversationsRecyclerView.setVisibility(View.VISIBLE);
        binding.progressBar.setVisibility(View.GONE);
    }
};

private void getToken(){
    FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken);
}
private void updateToken(String  token){
    // do Firebase below
    FirebaseFirestore database = FirebaseFirestore.getInstance();
    DocumentReference documentReference =
            database.collection(Constants.KEY_COLLECTION_USERS).document(
                    preferenceManager.getString(Constants.KEY_USER_ID)
            );
    documentReference.update(Constants.KEY_FCM_TOKEN,token)
          //  .addOnSuccessListener(e -> showToast("Token Updated successfully"))
            .addOnFailureListener(e -> showToast("Unable to Update Token"));

}
 private void signOut(){
    showToast("Signing Out :)");
    FirebaseFirestore database = FirebaseFirestore.getInstance();
    DocumentReference documentReference =
            database.collection(Constants.KEY_COLLECTION_USERS).document(
              preferenceManager.getString(Constants.KEY_USER_ID)
            );
    HashMap<String , Object> updates = new HashMap<>();
    updates.put(Constants.KEY_FCM_TOKEN, FieldValue.delete());
   documentReference.update(updates)
           .addOnSuccessListener(unused -> {
               preferenceManager.clear();
               startActivity(new Intent(getApplicationContext(),Sign_In.class));
               finish();
           })
           .addOnFailureListener(e -> showToast("Unable To Sign Out"));
 }



    @Override
    public void onConversionClicked(com.example.messageapp.models.User user) {
        Intent intent = new Intent(getApplicationContext() , ChatActivity.class);
        intent.putExtra(Constants.KEY_USER, user);
        startActivity(intent);
    }
}