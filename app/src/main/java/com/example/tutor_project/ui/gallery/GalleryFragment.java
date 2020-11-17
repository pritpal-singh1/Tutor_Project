package com.example.tutor_project.ui.gallery;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.tutor_project.R;
import com.example.tutor_project.ui.slideshow.SlideshowFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class GalleryFragment extends Fragment {
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    TextView user_name,user_email,user_phone,user_city,user_gender,user_address,user_highest_qual,name,email;
    TextView user_college_name,user_college_city,bio,p_about;
    ImageView user_image;
    Button edit_profile;
    private GalleryViewModel galleryViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                ViewModelProviders.of(this).get(GalleryViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_gallery, container, false);
//        final TextView textView = root.findViewById(R.id.text_gallery);
//        galleryViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
        firebaseAuth= FirebaseAuth.getInstance();
        user=firebaseAuth.getCurrentUser();
        firebaseDatabase= FirebaseDatabase.getInstance();

        user_name=root.findViewById(R.id.p_user_name);
        user_email=root.findViewById(R.id.p_user_email);
        user_gender=root.findViewById(R.id.p_user_gender);
        user_city=root.findViewById(R.id.p_user_city);
        user_address=root.findViewById(R.id.p_user_address);
        user_phone=root.findViewById(R.id.p_user_phone);
        user_highest_qual=root.findViewById(R.id.p_user_highest_qual);
        user_college_name=root.findViewById(R.id.p_user_college_name);
        user_college_city=root.findViewById(R.id.p_user_college_city);
        user_image= root.findViewById(R.id.user_image);
        edit_profile=root.findViewById(R.id.edit_profile);
        name=root.findViewById(R.id.name);
        email=root.findViewById(R.id.email);
        bio=root.findViewById(R.id.bio);
        p_about=root.findViewById(R.id.p_about);


        databaseReference=firebaseDatabase.getReference("Users");

        Query query= databaseReference.orderByChild("email").equalTo(user.getEmail());


        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String uname = "" + ds.child("name").getValue();
                    String uemail = "" + ds.child("email").getValue();
                    String uabout = "" + ds.child("bio").getValue();
                    String utype = "" + ds.child("user_type").getValue();
                    String ugender = "" + ds.child("gender").getValue();
                    String uaddress = "" + ds.child("address").getValue();
                    String ucity = "" + ds.child("city").getValue();
                    String uphone = "" + ds.child("phone").getValue();
                    String ucollege_city = "" + ds.child("college_city").getValue();
                    String ucollege_name = "" + ds.child("college_name").getValue();
                    String uhighest_education = "" + ds.child("highest_education").getValue();
                    String uimage = "" + ds.child("image").getValue();

                    user_name.setText(uname);
                    user_email.setText(uemail);
                    user_gender.setText(ugender);
                    user_city.setText(ucity);
                    user_address.setText(uaddress);
                    user_phone.setText(uphone);
                    user_highest_qual.setText(uhighest_education);
                    user_college_name.setText(ucollege_name);
                    user_college_city.setText(ucollege_city);
                    email.setText(uemail);
                    name.setText(uname);
                    bio.setText(uabout);
                    p_about.setText(uabout);

                    try {
                        Picasso.get().load(uimage).into(user_image);
                    } catch (Exception e) {
                        Picasso.get().load(R.drawable.user_icon).into(user_image);
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                SlideshowFragment NAME = new SlideshowFragment();
                fragmentTransaction.replace(R.id.nav_host_fragment, NAME);
                fragmentTransaction.commit();
            }
        });


        return root;
    }

}