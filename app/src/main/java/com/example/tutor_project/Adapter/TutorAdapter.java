package com.example.tutor_project.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;


import com.example.tutor_project.Model.TutorModel;
import com.example.tutor_project.R;
import com.example.tutor_project.ShowUserProfile;
import com.example.tutor_project.ui.gallery.GalleryFragment;
import com.google.firebase.auth.FirebaseAuth;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TutorAdapter extends RecyclerView.Adapter<TutorAdapter.ViewHolder> implements Filterable {
    private List<TutorModel> listData;
    private List<TutorModel> listDataFull;
    private Context context;
    public FragmentManager f_manager;
    String firebaseUser;

    public TutorAdapter(Context context, List<TutorModel> listData, FragmentManager f_manager) {
        this.listData = listData;
        this.context = context;
        listDataFull = new ArrayList<>();
        listDataFull.addAll(listData);
        this.f_manager = f_manager;
    }

    @NonNull
    @Override
    public TutorAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_tutor_ads, parent, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull final TutorAdapter.ViewHolder holder, int position) {
        final TutorModel ld = listData.get(position);

        if (ld.getType().equals("StudentAd")) {
            holder.ad_type.setText("Need Students");
        } else {
            holder.ad_type.setText("Need Tutor");
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
        holder.category.setText(ld.getCategory());
        holder.cls.setText(ld.getTutor_class());
        holder.days.setText(ld.getDays());
        holder.subject.setText(ld.getSubject());
        holder.salary.setText(ld.getSalary());
        holder.timestamp.setText(diff);
        holder.city.setText(ld.getCity());
        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        holder.show_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ld.getCreatedBy().equals(uid)) {


                    AppCompatActivity activity = (AppCompatActivity) view.getContext();
                    Fragment myFragment = new GalleryFragment();
                    FragmentManager fragmentManager = activity.getSupportFragmentManager();
                    FragmentTransaction transaction1 = activity.getSupportFragmentManager().beginTransaction();
                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, myFragment).commit();
                } else {


                    Intent i = new Intent(context.getApplicationContext(), ShowUserProfile.class);
                    i.putExtra("uid", ld.getCreatedBy());
                    context.startActivity(i);
                }


            }
        });

    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    @Override
    public Filter getFilter() {
        return adFilters;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title, category, cls, days, pref_students, city, salary, timestamp, subject, ad_type;
        private Button show_profile;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.show_tutor_title);
            category = (TextView) itemView.findViewById(R.id.show_category);
            cls = (TextView) itemView.findViewById(R.id.show_class);
            days = (TextView) itemView.findViewById(R.id.show_days);
            subject = (TextView) itemView.findViewById(R.id.show_subjects);
            timestamp = (TextView) itemView.findViewById(R.id.show_timestamp);
            salary = (TextView) itemView.findViewById(R.id.show_salary);
            show_profile = itemView.findViewById(R.id.show_ad_profile);
            city = itemView.findViewById(R.id.show_city);
            ad_type = itemView.findViewById(R.id.ad_type);


        }
    }

    private Filter adFilters = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<TutorModel> filteredList = new ArrayList<>();
            if (charSequence == null || charSequence.length() == 0) {
                filteredList.addAll(listDataFull);
            } else {
                String filter = charSequence.toString().toLowerCase().trim();
                for (TutorModel dataItem : listDataFull) {
                    if (dataItem.getTitle().toLowerCase().contains(filter)) {
                        filteredList.add(dataItem);
                    } else if (dataItem.getSubject().toLowerCase().contains(filter)) {
                        filteredList.add(dataItem);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            listData.clear();
            listData.addAll((Collection<? extends TutorModel>) filterResults.values);
            notifyDataSetChanged();
        }
    };

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
