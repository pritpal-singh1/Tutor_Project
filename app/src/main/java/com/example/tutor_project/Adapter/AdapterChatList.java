package com.example.tutor_project.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tutor_project.ChatActivity;
import com.example.tutor_project.Model.ModelUser;
import com.example.tutor_project.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import static android.content.ContentValues.TAG;

public class AdapterChatList extends RecyclerView.Adapter<AdapterChatList.MyHolder> {

    Context context;
    List<ModelUser> userChatList;
    public HashMap<String, Integer> unseenMap;

    public AdapterChatList(Context context, List<ModelUser> userChatList) {
        this.context = context;
        this.userChatList = userChatList;
        unseenMap = new HashMap<>();
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_chat_users, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        final String hisUid = userChatList.get(position).getUid();
//        Log.d(TAG, "onBindViewHolder: "+hisUid);
        String userImage = userChatList.get(position).getImage();
//        Log.d(TAG, "onBindViewHolder: "+userImage);
        String userName = userChatList.get(position).getName();
//        Log.d(TAG, "onBindViewHolder: "+userName);
        Log.d(TAG, "onBindViewHolder: " + unseenMap + " " + unseenMap.get(hisUid));
        Object unread_msg = unseenMap.get(hisUid);
//        Log.d(TAG, ""+unread_msg);
//

        if (unread_msg == null) {
            holder.unreadMsg.setVisibility(View.GONE);
            holder.unreadMsg.setText("");


        } else if (unread_msg.equals(Integer.valueOf(0))) {
            holder.unreadMsg.setVisibility(View.GONE);
            holder.unreadMsg.setText("");
        }

        else {
            int i=(int) unread_msg;
            holder.unreadMsg.setVisibility(View.VISIBLE);
            holder.unreadMsg.setText(i + " Unread Message");
        }

//        int test_int=Integer.parseInt(unread_msg);
//        Log.d(TAG, String.valueOf(""+unread_msg=="0"));
//        Log.d(TAG, String.valueOf(""+unread_msg==null));


        holder.char_list_nameTv.setText(userName);
        Log.d(TAG, "onBindViewHolder: "+userImage);
        if (userImage == null) {
            try {
                Picasso.get().load(R.drawable.ic_face).placeholder(R.drawable.ic_face).into(holder.chatlist_profileIv);
            } catch (Exception e) {
                Picasso.get().load(R.drawable.man).into(holder.chatlist_profileIv);
            }
        } else {

            try {
                Picasso.get().load(userImage).placeholder(R.drawable.ic_face).into(holder.chatlist_profileIv);
            } catch (Exception e) {
                Picasso.get().load(R.drawable.man).into(holder.chatlist_profileIv);
            }
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("hisUid", hisUid);
                context.startActivity(intent);
            }
        });
    }

    public void setUnseenMap(String userId, Integer unread) {
        unseenMap.put(userId, unread);
    }

    @Override
    public int getItemCount() {
        return userChatList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {
        ImageView chatlist_profileIv;
        TextView char_list_nameTv, unreadMsg;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            chatlist_profileIv = itemView.findViewById(R.id.chatList_profileIv);
            char_list_nameTv = itemView.findViewById(R.id.char_list_nameTv);
            unreadMsg = itemView.findViewById(R.id.unread_msg);

        }
    }
}


