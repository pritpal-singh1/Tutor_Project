package com.example.tutor_project.Adapter;

import android.content.Context;
import android.media.Image;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tutor_project.Model.ModelChat;
import com.example.tutor_project.Model.TutorModel;
import com.example.tutor_project.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static android.content.ContentValues.TAG;

public class AdapterChat extends  RecyclerView.Adapter<AdapterChat.MyHolder>{
    private static final int MSG_TYPE_LEFT=0;
    private static final int MSG_TYPE_RIGHT=1;
    Context context;
    List<ModelChat> chatList;
    String imageUrl;
    FirebaseUser fUser;

    public AdapterChat(Context context, List<ModelChat> chatList, String imageUrl) {
        this.context = context;
        this.chatList = chatList;
        this.imageUrl = imageUrl;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType==MSG_TYPE_RIGHT)
        {
            View view= LayoutInflater.from(context).inflate(R.layout.row_chat_right,parent,false);
            return new MyHolder(view);
        }
        else
        {
            View view= LayoutInflater.from(context).inflate(R.layout.row_chat_left,parent,false);
            return new MyHolder(view);

        }

    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        final ModelChat mc = chatList.get(position);
        String timestamp= chatList.get(position).getTimestamp();
        long time = Long.parseLong(timestamp);
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        String datetime = DateFormat.format("dd-MM-yyyy hh:mm aa", c).toString();

        holder.messageTv.setText(mc.getMessage());
        holder.timeTv.setText(datetime);
        try
        {
            Picasso.get().load(imageUrl).into(holder.profileIv);
        }
        catch (Exception e)
        {
            Picasso.get().load(R.drawable.man).into(holder.profileIv);
        }
        if (position==chatList.size()-1){
            if(mc.isIsSeen()){
                holder.isSeenTv.setText("Seen");
                holder.isSeenTv.setVisibility(View.VISIBLE);
            }
            else
            {
                holder.isSeenTv.setText("Delievered");
                holder.isSeenTv.setVisibility(View.VISIBLE);
            }
        }
        else{
            holder.isSeenTv.setVisibility(View.GONE);

        }
//        if (position==chatList.size()-1){
//            if (chatList.get(position).isSeen()){
//                holder.isSeenTv.setText("Seen");
//            }
//            else{
//                holder.isSeenTv.setText("Delivered");
//            }
//
//        }
//        else {
//            holder.isSeenTv.setVisibility(View.GONE);
//        }


    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    @Override
    public int getItemViewType(int position) {
        fUser= FirebaseAuth.getInstance().getCurrentUser();
        if (chatList.get(position).getSender().equals(fUser.getUid())){
            return MSG_TYPE_RIGHT;
        }
        else
        {
            return MSG_TYPE_LEFT;
        }

    }

    class MyHolder extends RecyclerView.ViewHolder{
        ImageView profileIv;
        TextView messageTv,timeTv,isSeenTv;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            profileIv= itemView.findViewById(R.id.profileIv);
            messageTv= itemView.findViewById(R.id.messageTv);
            timeTv= itemView.findViewById(R.id.timeTv);
            profileIv=itemView.findViewById(R.id.profileIv);
            isSeenTv=itemView.findViewById(R.id.seenTv);
        }
    }
}
