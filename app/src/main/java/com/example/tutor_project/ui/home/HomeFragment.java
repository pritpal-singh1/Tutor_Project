package com.example.tutor_project.ui.home;

import android.app.ProgressDialog;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tutor_project.Adapter.TutorAdapter;
import com.example.tutor_project.Model.TutorModel;
import com.example.tutor_project.R;
import com.example.tutor_project.ShowUserProfile;
import com.example.tutor_project.StudentAds;
import com.example.tutor_project.TutorAds;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class HomeFragment extends Fragment {
    BottomNavigationView bottomNavigationView;
    private HomeViewModel homeViewModel;
    RecyclerView recview;
    private TutorAdapter adapter;
    private List<TutorModel> listData;
    ProgressDialog loadingBar;
    private RecyclerView rv;
    private DatabaseReference nm;
    TextView empty_view;
    private Button show_profile;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
//        View tutorFragment = inflater.inflate(R.layout.fragment_tutor_ads, container, false);
//        final TextView textView = root.findViewById(R.id.text_home);
//        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
//        loadingBar= new ProgressDialog(getContext());
//        loadingBar.show();
//        loadingBar.setContentView(R.layout.progress_dialog);
//        loadingBar.getWindow().setBackgroundDrawableResource(R.color.grey);

        loadingBar = new ProgressDialog(getActivity());
        rv = root.findViewById(R.id.recycler_view);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        empty_view=root.findViewById(R.id.empty_view);
        listData = new ArrayList<>();
        nm = FirebaseDatabase.getInstance().getReference("PostAds");
        loadingBar.setMessage("Loading Data. Please Wait.....");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();
        adapterCall();
//        bottomNavigationView = root.findViewById(R.id.navigation_view);
//        bottomNavigationView.setOnNavigationItemSelectedListener((BottomNavigationView.OnNavigationItemSelectedListener) bottomNavMethod);
//        getParentFragmentManager().beginTransaction().replace(R.id.fragmentframe, new StudentAds()).commit();
        return root;

    }
    public void adapterCall() {
        nm.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot npsnapshot : dataSnapshot.getChildren()) {
                        TutorModel l = npsnapshot.getValue(TutorModel.class);
                        listData.add(l);
                    }
                    if (listData.isEmpty()){
                        rv.setVisibility(View.GONE);
                        empty_view.setVisibility(View.VISIBLE);

                    }else{
                        rv.setVisibility(View.VISIBLE);
                        empty_view.setVisibility(View.GONE);
                        adapter = new TutorAdapter(getContext(), listData,getChildFragmentManager());
                        rv.setAdapter(adapter);
                    }

                }
                loadingBar.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
//        Log.d(TAG, "onCreateOptionsMenu: ");
        inflater.inflate(R.menu.app_bar_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.search_view);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
//                Log.d(TAG, "onCreateOptionsMenu1: ");
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
//                Log.d(TAG, "onCreateOptionsMenu2: ");
                adapter.getFilter().filter(s);
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    //    public void onStart() {
//        super.onStart();
//        adapter.startListening();
//    }
//
//    public void onStop() {
//        super.onStop();
//        adapter.stopListening();
//    }

//    BottomNavigationView.OnNavigationItemSelectedListener bottomNavMethod = new BottomNavigationView.OnNavigationItemSelectedListener() {
//        @Override
//        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//            Fragment fragment = null;
//            switch (item.getItemId()) {
//                case R.id.studentAdMenu:
//                    fragment = new StudentAds();
//                    break;
//
//                case R.id.tutorAdMenu:
//                    fragment = new TutorAds();
//                    break;
//            }
//            getParentFragmentManager().beginTransaction().replace(R.id.fragmentframe, fragment).commit();
//            return false;
//        }
//    };

}