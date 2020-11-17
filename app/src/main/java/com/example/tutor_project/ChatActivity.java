package com.example.tutor_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tutor_project.Adapter.AdapterChat;
import com.example.tutor_project.Model.ModelChat;
import com.google.android.gms.common.data.DataBufferObserverSet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.ContentValues.TAG;


public class ChatActivity extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView recyclerView;
    ImageView profileIv;
    TextView nameIv, userStatusTv;
    EditText messageEt;
    ImageButton sendBtn;

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference usersDbRef;


    ValueEventListener seenListener;
    DatabaseReference userRefForSeen;
    List<ModelChat> chatList;

    AdapterChat adapterChat;


    String hisUid;
    String myUid;
    String hisImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Toolbar toolbar =findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        recyclerView= findViewById(R.id.chat_recyclerView);
        profileIv= findViewById(R.id.profileIv);
        nameIv= findViewById(R.id.nameTv);
        userStatusTv= findViewById(R.id.userStatusTv);
        messageEt= findViewById(R.id.messageEt);
        sendBtn=findViewById(R.id.sendBtn);

        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        firebaseAuth=FirebaseAuth.getInstance();
        FirebaseUser user= firebaseAuth.getCurrentUser();
        myUid=user.getUid();

        Intent intent=getIntent();
        hisUid=intent.getStringExtra("hisUid");

        firebaseDatabase=FirebaseDatabase.getInstance();
        usersDbRef= firebaseDatabase.getReference("Users");

        Query userQuery= usersDbRef.orderByChild("uid").equalTo(hisUid);
        userQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren()){
                    String name= ""+ds.child("name").getValue();

                    hisImage=""+ds.child("image").getValue();
                    nameIv.setText(name);
                    try {
                        Picasso.get().load(hisImage).placeholder(R.drawable.ic_face).into(profileIv);
                    } catch (Exception e) {
                        Picasso.get().load(R.drawable.user_icon).into(profileIv);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String message= messageEt.getText().toString().trim();
                if(TextUtils.isEmpty(message)){
                    Toast.makeText(ChatActivity.this,"Cannot send the empty message....",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    sendMessage(message);

                }
            }
        });
        readMessages();

//        seenMessages();

        final DatabaseReference chatRef1 =FirebaseDatabase.getInstance().getReference("Chatlist").child(myUid).child(hisUid);

        chatRef1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    chatRef1.child("id").setValue(hisUid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        final DatabaseReference chatRef2 =FirebaseDatabase.getInstance().getReference("Chatlist").child(hisUid).child(myUid);
        chatRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    chatRef2.child("id").setValue(myUid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        seenMessages();
    }

    private void seenMessages() {
        userRefForSeen= FirebaseDatabase.getInstance().getReference("Chats");
        seenListener=userRefForSeen.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren())
                {
                    ModelChat chat= ds.getValue(ModelChat.class);
                    if (chat.getReciever().equals(myUid)&& chat.getSender().equals(hisUid)){
                        HashMap<String, Object> hasSeenHashMap=new HashMap<>();
                        hasSeenHashMap.put("isSeen",true);
                        ds.getRef().updateChildren(hasSeenHashMap);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readMessages() {
        chatList= new ArrayList<>();
        DatabaseReference dbRef= FirebaseDatabase.getInstance().getReference("Chats");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatList.clear();
                for (DataSnapshot ds:snapshot.getChildren())
                {
                    ModelChat chat= ds.getValue(ModelChat.class);
                    if (chat.getReciever().equals(myUid)&& chat.getSender().equals(hisUid)||
                            chat.getReciever().equals(hisUid)&&chat.getSender().equals(myUid)){
                        chatList.add(chat);
//                        Log.d(TAG,chat.getMessage()+"Data sent");

                    }
                    adapterChat=new AdapterChat(ChatActivity.this,chatList,hisImage);
                    adapterChat.notifyDataSetChanged();
                    recyclerView.setAdapter(adapterChat);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendMessage(String message) {
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference();
        String timestamp= String.valueOf(System.currentTimeMillis());
        HashMap<String, Object> hashMap= new HashMap<>();
        hashMap.put("sender",myUid);
        hashMap.put("reciever",hisUid);
        hashMap.put("message",message);
        hashMap.put("timestamp",timestamp);
        hashMap.put("isSeen",false);
        databaseReference.child("Chats").push().setValue(hashMap);
        messageEt.setText("");
    }

    @Override
    protected void onPause() {
        userRefForSeen.removeEventListener(seenListener);
        super.onPause();
    }
}