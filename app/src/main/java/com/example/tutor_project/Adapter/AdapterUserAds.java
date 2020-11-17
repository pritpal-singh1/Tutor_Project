package com.example.tutor_project.Adapter;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tutor_project.Model.TutorModel;
import com.example.tutor_project.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AdapterUserAds extends RecyclerView.Adapter<AdapterUserAds.MyHolder> {
    private List<TutorModel> listData;
    private Context context;


    public AdapterUserAds(List<TutorModel> listData, Context context) {
        this.listData = listData;
        this.context = context;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_ads, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
      final TutorModel ld = listData.get(position);
        if (ld.getType().equals("StudentAd")) {
            holder.type.setText("Need Students");
        } else {
            holder.type.setText("Need Tutor");
        }
        String t = ld.getCreatedAt();
        long d = Long.parseLong(t);
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
        holder.title.setText(ld.getTitle());
        holder.daysAgo.setText(diff);
        holder.dayperweek.setText(ld.getDays());
        holder.address.setText(ld.getAddress());
        holder.cls.setText(ld.getTutor_class());
        holder.subject.setText(ld.getSubject());
        holder.category.setText(ld.getCategory());
        holder.fees.setText(ld.getSalary());


    }

    @Override
    public int getItemCount() {
        return listData.size();
    }


    public class MyHolder extends RecyclerView.ViewHolder {
        private TextView title,daysAgo,dayperweek,address,cls,subject,category,fees,type;
//                category, cls, days, pref_students, city, salary, timestamp, subject, ad_type;
//        private Button show_profile;

        public MyHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.ad_title);
            daysAgo = itemView.findViewById(R.id.daysAgo);
            address = itemView.findViewById(R.id.address);
            cls = itemView.findViewById(R.id.cls);
            subject = itemView.findViewById(R.id.subject);
            dayperweek = itemView.findViewById(R.id.dayperweek);
            category = itemView.findViewById(R.id.category);
            type = itemView.findViewById(R.id.type);
            fees = itemView.findViewById(R.id.fees);
//            contact = itemView.findViewById(R.id.ad_title);

//            category = (TextView) itemView.findViewById(R.id.show_category);
//            cls = (TextView) itemView.findViewById(R.id.show_class);
//            days = (TextView) itemView.findViewById(R.id.show_days);
//            subject = (TextView) itemView.findViewById(R.id.show_subjects);
//            timestamp = (TextView) itemView.findViewById(R.id.show_timestamp);
//            salary = (TextView) itemView.findViewById(R.id.show_salary);
//            show_profile = itemView.findViewById(R.id.show_ad_profile);
//            city = itemView.findViewById(R.id.show_city);
//            ad_type = itemView.findViewById(R.id.ad_type);


        }
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
