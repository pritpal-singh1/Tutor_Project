package com.example.tutor_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.tutor_project.Adapter.AdapterUserAds;
import com.example.tutor_project.Adapter.TutorAdapter;
import com.example.tutor_project.Model.TutorModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ShowUserProfile extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference userAdRef;
    DatabaseReference databaseReference;
    TextView show_name,show_college,show_city,user_joined;
    Button send_msgBtn;
    public String userId;
    private AdapterUserAds adapter;
    private List<TutorModel> listData;
    RecyclerView recview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_user_profile);
        firebaseAuth= FirebaseAuth.getInstance();
        user=firebaseAuth.getCurrentUser();
        firebaseDatabase= FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference("Users");

        show_name= findViewById(R.id.show_user_name);
        show_college= findViewById(R.id.show_ad_college);
        show_city= findViewById(R.id.show_ad_city);
        send_msgBtn= findViewById(R.id.send_msg_btn);
                user_joined= findViewById(R.id.user_joined);

        recview = findViewById(R.id.recview_user_ads);
        recview.setHasFixedSize(true);
        recview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        listData = new ArrayList<>();
        final String s = getIntent().getStringExtra("uid");
        userId=new String();
        userId=s;
        Query q= databaseReference.orderByChild("uid").equalTo(s);
        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String uname = "" + ds.child("name").getValue();
                    String college_name = "" + ds.child("college_name").getValue();
                    String city = "" + ds.child("city").getValue();
                    String timestamp=""+ds.child("joinedAt").getValue();
//                    String ugender = "" + ds.child("gender").getValue();
//                    String uaddress = "" + ds.child("address").getValue();
//                    String ucity = "" + ds.child("city").getValue();
//                    String uphone = "" + ds.child("phone").getValue();
//                    String ucollege_city = "" + ds.child("college_city").getValue();
//                    String ucollege_name = "" + ds.child("college_name").getValue();
//                    String uhighest_education = "" + ds.child("highest_education").getValue();
                    long d = Long.parseLong(timestamp);
                    Calendar calendar = Calendar.getInstance();
                    Calendar c = Calendar.getInstance();
                    c.setTimeInMillis(d);
                    String date1 = DateFormat.format("dd-MM-yyyy HH:mm:ss", c).toString();
//        Date date = c.getTime();
                    long cdate = System.currentTimeMillis();
                    calendar.setTimeInMillis(cdate);
//        Date cd=calendar.getTime();
                    String date2 = DateFormat.format("dd-MM-yyyy HH:mm:ss", calendar).toString();
                    String diff = getTimeDiffernce(date1, date2);
                    show_name.setText(uname);
                    show_college.setText(college_name);
                    show_city.setText(city);
                    user_joined.setText(diff);
//                    user_city.setText(ucity);
//                    user_address.setText(uaddress);
//                    user_phone.setText(uphone);
//                    user_highest_qual.setText(uhighest_education);
//                    user_college_name.setText(ucollege_name);
//                    user_college_city.setText(ucollege_city);




                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d( "Failed to read value.", String.valueOf(error.toException()));
            }
        });
        send_msgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent(getApplicationContext(),ChatActivity.class);
                i.putExtra("hisUid", s);
                startActivity(i);
            }
        });
        userAdRef=FirebaseDatabase.getInstance().getReference("PostAds");
        adapterCall();

    }

    private void adapterCall() {
        userAdRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        TutorModel l = ds.getValue(TutorModel.class);
                        if (l.getCreatedBy().equals(userId)){
                            listData.add(l);
                        }

                    }
                    adapter = new AdapterUserAds(listData,getApplicationContext());
                    recview.setAdapter(adapter);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public String getTimeDiffernce(String start_date,
                                   String end_date) {
        // string format to date object
        SimpleDateFormat sdf
                = new SimpleDateFormat(
                "dd-MM-yyyy HH:mm:ss");

        // Try Class
        try {

            // parse method is used to parse
            // the text from a string to
            // produce the date
            Date d1 = sdf.parse(start_date);
            Date d2 = sdf.parse(end_date);

            // Calucalte time difference
            // in milliseconds
            long difference_In_Time
                    = d2.getTime() - d1.getTime();

            // Calucalte time difference in seconds,
            // minutes, hours, years, and days
            long difference_In_Seconds
                    = TimeUnit.MILLISECONDS
                    .toSeconds(difference_In_Time)
                    % 60;

            long difference_In_Minutes
                    = TimeUnit
                    .MILLISECONDS
                    .toMinutes(difference_In_Time)
                    % 60;

            long difference_In_Hours
                    = TimeUnit
                    .MILLISECONDS
                    .toHours(difference_In_Time)
                    % 24;

            long difference_In_Days
                    = TimeUnit
                    .MILLISECONDS
                    .toDays(difference_In_Time)
                    % 365;

            long difference_In_Years
                    = TimeUnit
                    .MILLISECONDS
                    .toDays(difference_In_Time)
                    / 365l;
            if (difference_In_Days == 0 && difference_In_Hours == 0 && difference_In_Minutes == 0)
                return Long.toString(difference_In_Seconds) + " seconds ago ";
            else if (difference_In_Days == 0 && difference_In_Hours == 0)
                return Long.toString(difference_In_Minutes) + " minutes ago ";
            else if (difference_In_Days == 0)
                return Long.toString(difference_In_Hours) + " hours ago ";
            else
                return Long.toString(difference_In_Days) + " days ago";
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return start_date;
    }

}