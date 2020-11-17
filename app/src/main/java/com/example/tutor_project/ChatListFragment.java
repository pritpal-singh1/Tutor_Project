package com.example.tutor_project;

import android.app.ProgressDialog;
import android.graphics.ColorSpace;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.tutor_project.Adapter.AdapterChatList;
import com.example.tutor_project.Model.ModelChat;
import com.example.tutor_project.Model.ModelChatList;
import com.example.tutor_project.Model.ModelUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatListFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    TextView unreadMsg;

    FirebaseAuth firebaseAuth;
    RecyclerView recyclerView;
    List<ModelChatList> chatlistList;
    List<ModelUser> userList;
    DatabaseReference reference;

    FirebaseUser currentUser;
    AdapterChatList adapterChatList;
    ProgressDialog pd;

    public ChatListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChatListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatListFragment newInstance(String param1, String param2) {
        ChatListFragment fragment = new ChatListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_chat_list, container, false);
        final View v= inflater.inflate(R.layout.list_chat_users, container, false);
        unreadMsg=v.findViewById(R.id.unread_msg);
        firebaseAuth= FirebaseAuth.getInstance();
        recyclerView= view.findViewById(R.id.chatList_recyclerView);
        currentUser= FirebaseAuth.getInstance().getCurrentUser();
        pd= new ProgressDialog(getActivity());
        pd.setMessage("Loading Chats. Please Wait.....");
        pd.setCanceledOnTouchOutside(false);
        pd.show();

        chatlistList = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Chatlist").child(currentUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatlistList.clear();
                for (DataSnapshot ds: snapshot.getChildren()) {
                    ModelChatList chatlist = ds.getValue(ModelChatList.class);
                    chatlistList.add(chatlist);
                }

                loadChats();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }

    private void loadChats() {
        userList= new ArrayList<>();
        reference=FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    ModelUser user= ds.getValue(ModelUser.class);
                    for (ModelChatList chatlist: chatlistList){
                        if (user.getUid()!=null && user.getUid().equals(chatlist.getId())){
                            userList.add(user);
                            break;
                        }
                    }
                    adapterChatList= new AdapterChatList(getContext(),userList);
                    recyclerView.setAdapter(adapterChatList);
                    for (int i=0;i<userList.size();i++){
                        unseenCount(userList.get(i).getUid());
                    }
                }
                pd.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void unseenCount(final String uid) {
        DatabaseReference seenref= FirebaseDatabase.getInstance().getReference("Chats");
        seenref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int unread=0;
                for (DataSnapshot ds : snapshot.getChildren() ){

                    ModelChat chat=ds.getValue(ModelChat.class);
                    String rcv = chat.getReciever();
                    String cu = currentUser.getUid();
                    boolean sc = chat.isIsSeen();
//                    Log.d(TAG, "onDataChange: "+rcv+" "+cu+" "+sc+" "+chat.getMessage());
//                    Log.d(TAG, "onDataChange: "+(chat.getReciever().equals(currentUser.getUid()) && !(chat.isIsSeen())));
                    if (chat.getReciever().equals(currentUser.getUid()) && !(chat.isIsSeen())){
                        unread++;
                        Log.d(TAG, "Unread messages: "+unread);
                    }
                    Log.d(TAG, "onDataChange: "+uid+" "+unread);


                }
                adapterChatList.setUnseenMap(uid,
                        unread);
                adapterChatList.notifyDataSetChanged();

//                if (unread==0){
//                    Log.d(TAG, "onDataChange: inside unread =0");
//                    unreadMsg.setText(unread+" Unread Messages");
//                    unreadMsg.setVisibility(View.GONE);
//                }
//                else
//                {
//                    Log.d(TAG, "onDataChange: inside unread else");
//                    unreadMsg.setVisibility(View.VISIBLE);
//                    unreadMsg.setText(unread+" Unread Messages");
//                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}