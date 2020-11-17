package com.example.tutor_project;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static androidx.core.content.ContextCompat.getSystemService;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PostTutorAd#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostTutorAd extends Fragment {
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    Button save_post;
    Spinner post_setdays;
    EditText post_title,post_class,post_subject;
    EditText post_city,post_address,post_salary,post_contact,noofstudents,post_state;
    AutoCompleteTextView post_days;
    AutoCompleteTextView post_category;

    MultiAutoCompleteTextView multiAutoCompleteTextView;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    ArrayList<String> listSpinner=new ArrayList<String>();
    // to store the city and state in the format : City , State. Eg: New Delhi , India
    ArrayList<String> listAll=new ArrayList<String>();
    // for listing all states
    ArrayList<String> listState=new ArrayList<String>();
    // for listing all cities
    ArrayList<String> listCity=new ArrayList<String>();
    // access all auto complete text views
    AutoCompleteTextView act;


    public PostTutorAd() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PostTutorAd.
     */
    // TODO: Rename and change types and number of parameters
    public static PostTutorAd newInstance(String param1, String param2) {
        PostTutorAd fragment = new PostTutorAd();
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

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_post_tutor_ad, container, false);

        String[] randomSuggestions = {"Maths", "Science", "Social Science", "Computer", "English", "English Grammer", "English Speaking", "Hindi Speaking", "Hindi Grammer", "Hindi",
                "Physics","Chemistry","Economics","Business Studies","Accounts",
                "Biology","Physical Education","Dance","Singing" };


        // Inflate the layout for this fragment

//        populate city and state dropdown menu here
        try
        {

            JSONObject jsonObject=new JSONObject(getJson(getContext()));

            JSONArray array=jsonObject.getJSONArray("array");

            for(int i=0;i<array.length();i++)
            {

                JSONObject object=array.getJSONObject(i);
                String city=object.getString("name");
                String state=object.getString("state");
                listSpinner.add(String.valueOf(i+1)+" : "+city+" , "+state);
                listAll.add(city+" , "+state);
                listCity.add(city);
                listState.add(state);
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        act=(AutoCompleteTextView)view.findViewById(R.id.t_city);
        adapterSetting(listCity);
//  set adapter for both
        Set<String> set = new HashSet<String>(listState);
        act=(AutoCompleteTextView)view.findViewById(R.id.actState);
        adapterSetting(new ArrayList(set));


        firebaseAuth= FirebaseAuth.getInstance();
        user=firebaseAuth.getCurrentUser();
        firebaseDatabase= FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference("PostAds");


        post_days=view.findViewById(R.id.t_days);
        String[] option={"1","2","3","4","5","6","7"};
        ArrayAdapter optionarrayAdapter= new ArrayAdapter(getActivity(),R.layout.days_option,option);
        post_days.setText(optionarrayAdapter.getItem(0).toString(),false);
        post_days.setAdapter(optionarrayAdapter);

        post_category=view.findViewById(R.id.t_category);
        String [] category_option={"CBSE","ICSE","State Board","IT Course","Arts"};
        ArrayAdapter categoryarrayAdapter= new ArrayAdapter(getActivity(),R.layout.category_option,category_option);
        post_category.setText(categoryarrayAdapter.getItem(0).toString(),false);
        post_category.setAdapter(categoryarrayAdapter);


        multiAutoCompleteTextView = view.findViewById(R.id.t_subject);
        ArrayAdapter<String> randomArray = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, randomSuggestions);
        multiAutoCompleteTextView.setAdapter(randomArray);
        multiAutoCompleteTextView.setThreshold(1);
        multiAutoCompleteTextView.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

        save_post=view.findViewById(R.id.save_post_tutor);
        post_title=view.findViewById(R.id.t_title);
        post_category=view.findViewById(R.id.t_category);
        post_class=view.findViewById(R.id.t_class);
        post_subject=view.findViewById(R.id.t_subject);
        post_city=view.findViewById(R.id.t_city);
        post_address=view.findViewById(R.id.t_address);
        post_salary=view.findViewById(R.id.t_salary);
        post_contact=view.findViewById(R.id.t_contact);
        noofstudents=view.findViewById(R.id.t_noofstudents);
        post_state=view.findViewById(R.id.actState);

        save_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String timeStamp=String.valueOf(System.currentTimeMillis());
                String title= post_title.getText().toString();
                String category= post_category.getText().toString();
                String pclass= post_class.getText().toString();
                String subject= post_subject.getText().toString();
                String setdays= post_days.getText().toString();
                String city= post_city.getText().toString();
                String address= post_address.getText().toString();
                String salary= post_salary.getText().toString();
                String contact= post_contact.getText().toString();
                String stdcount=noofstudents.getText().toString();
                String sstate=post_state.getText().toString();
                if (TextUtils.isEmpty((title))) {
                    Toast.makeText(getActivity(), "Please write post title.......", Toast.LENGTH_SHORT).show();

                }
                else if (TextUtils.isEmpty((pclass))) {
                    Toast.makeText(getActivity(), "Please write class.......", Toast.LENGTH_SHORT).show();
                }
                else if (TextUtils.isEmpty((subject))) {
                    Toast.makeText(getActivity(), "Please write subject.......", Toast.LENGTH_SHORT).show();
                }
                else if (TextUtils.isEmpty((city))) {
                    Toast.makeText(getActivity(), "Please write city.......", Toast.LENGTH_SHORT).show();
                }
                else if (TextUtils.isEmpty((address))) {
                    Toast.makeText(getActivity(), "Please write address.......", Toast.LENGTH_SHORT).show();
                }
                else if (TextUtils.isEmpty((salary))) {
                    Toast.makeText(getActivity(), "Please write salary.......", Toast.LENGTH_SHORT).show();
                }
                else if (TextUtils.isEmpty((contact))) {
                    Toast.makeText(getActivity(), "Please write contact.......", Toast.LENGTH_SHORT).show();
                }
                else if (TextUtils.isEmpty((stdcount))) {
                    Toast.makeText(getActivity(), "Please write how many students.......", Toast.LENGTH_SHORT).show();
                }

                else {
                    uploadPost( title,  category, pclass, subject, setdays,
                            city, address, salary,  contact,  timeStamp,stdcount, sstate);


                }
            }
            public void uploadPost(String title, String category,String pclass,String subject,String setdays,
                                   String city,String address,String salary, String contact, String timeStamp,String stdcount,String sstate){

                HashMap<Object, String> hashMap = new HashMap<>();
                hashMap.put("title", title);
                hashMap.put("category", category);
                hashMap.put("tutor_class", pclass);
                hashMap.put("subject", subject);
                hashMap.put("days", setdays);
                hashMap.put("city", city);
                hashMap.put("address", address);
                hashMap.put("salary", salary);
                hashMap.put("contact", contact);
                hashMap.put("state", sstate);
                hashMap.put("createdBy",user.getUid());
                hashMap.put("createdAt",timeStamp);
                hashMap.put("number_of_students",stdcount);
                hashMap.put("type","TutorAd");
                databaseReference.child("post"+timeStamp).setValue(hashMap);

                new MaterialAlertDialogBuilder(getActivity())
                        .setTitle("Post Saved")
                        .setMessage("Your Post Has Been Successfully Saved")
                        .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })

                        .show();
            }
        });
        return view;

    }

    private void adapterSetting(ArrayList<String> arrayList) {

        ArrayAdapter<String> adapter=new ArrayAdapter<String>(getContext(),android.R.layout.simple_dropdown_item_1line,arrayList);
        act.setAdapter(adapter);

    }



    private String getJson(Context context) {
        String json=null;
        try
        {
            // Opening cities.json file
            InputStream is = context.getAssets().open("cities.json");
            // is there any content in the file
            int size = is.available();
            byte[] buffer = new byte[size];
            // read values in the byte array
            is.read(buffer);
            // close the stream --- very important
            is.close();
            // convert byte to string
            json = new String(buffer, "UTF-8");
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            return json;
        }
        return json;
    }
}