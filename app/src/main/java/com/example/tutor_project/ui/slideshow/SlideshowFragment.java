package com.example.tutor_project.ui.slideshow;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.tutor_project.ProfileActivity;
import com.example.tutor_project.R;
import com.example.tutor_project.RegisterActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static android.app.Activity.RESULT_OK;
import static com.google.firebase.storage.FirebaseStorage.getInstance;

public class SlideshowFragment extends Fragment {
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    StorageReference storageReference;
    String storagePath = "Users_Profile_Cover_Imgs/";
    TextView user_name, user_email, u_type, user_name_show;
    EditText address, phone, city;
    RadioGroup gender;
    RadioButton gender_selected, maleRB, femaleRB;
    EditText highest_qualification, college_name, college_city,show_about;
    ImageView user_avatar;
    String image_uri_string;
    Button saveProfile, editImage;
    ProgressDialog pd;

    Uri image_uri;
    ArrayList<String> listAll=new ArrayList<String>();
    // for listing all states
    ArrayList<String> listState=new ArrayList<String>();
    // for listing all cities
    ArrayList<String> listCity=new ArrayList<String>();
    // access all auto complete text views
    AutoCompleteTextView act;
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_GALLERY_CODE = 300;
    private static final int IMAGE_PICK_CAMERA_CODE = 400;

    String cameraPermissions[];
    String storagePermissions[];

    private SlideshowViewModel slideshowViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        slideshowViewModel =
                ViewModelProviders.of(this).get(SlideshowViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_slideshow, container, false);
//        final TextView textView = root.findViewById(R.id.text_slideshow);
//        slideshowViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");
        storageReference = getInstance().getReference();

        try
        {

            JSONObject jsonObject=new JSONObject(getJson(getContext()));

            JSONArray array=jsonObject.getJSONArray("array");

            for(int i=0;i<array.length();i++)
            {

                JSONObject object=array.getJSONObject(i);
                String city=object.getString("name");
                String state=object.getString("state");

                listCity.add(city);
                listState.add(state);
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        act=(AutoCompleteTextView)root.findViewById(R.id.user_city);
        adapterSetting(listCity);
//  set adapter for both
//        Set<String> set = new HashSet<String>(listState);
//        act=(AutoCompleteTextView)root.findViewById(R.id.actState);
//        adapterSetting(new ArrayList(set));


        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        pd = new ProgressDialog(getActivity());
        user_name = root.findViewById(R.id.user_name);
        user_name_show = root.findViewById(R.id.user_name_show);
        u_type = root.findViewById(R.id.user_type);
        user_email = root.findViewById(R.id.user_email);
        gender = root.findViewById(R.id.gender_enabled);
        maleRB = root.findViewById(R.id.gender_male);
        femaleRB = root.findViewById(R.id.gender_female);
        address = root.findViewById(R.id.user_address);
        phone = root.findViewById(R.id.user_phone);
        city = root.findViewById(R.id.user_city);
        user_avatar = root.findViewById(R.id.user_image);
        show_about= root.findViewById(R.id.show_about);

        highest_qualification = root.findViewById(R.id.highest_qual);
        college_name = root.findViewById(R.id.tutor_college);
        college_city = root.findViewById(R.id.tutor_college_city);

        saveProfile = root.findViewById(R.id.save_profile);
        editImage = root.findViewById(R.id.user_edit_photo);

        pd = new ProgressDialog(getActivity());
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage("Loading Profile Information....");
        pd.show();
        loadData();

        saveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                int selected_genderId = gender.getCheckedRadioButtonId();
                if (selected_genderId == -1) {
                    Toast.makeText(getActivity(), "Please select correct gender....", Toast.LENGTH_SHORT).show();
                } else {

                    gender_selected = root.findViewById(selected_genderId);
                    String uname = user_name.getText().toString();
                    String ugender = gender_selected.getText().toString();
                    String uadd = address.getText().toString();
//                    String utype = u_type.getText().toString();
                    String ucity = city.getText().toString();
                    String uphone = phone.getText().toString();
                    String uhighest_education = highest_qualification.getText().toString();
                    String ucollege_name = college_name.getText().toString();
                    String ucollege_city = college_city.getText().toString();
                    String about = show_about.getText().toString();


                    FirebaseUser user = firebaseAuth.getCurrentUser();

                    String uid = user.getUid();
                    HashMap<Object, String> hashMap = new HashMap<>();
                    hashMap.put("email", user.getEmail());
                    hashMap.put("uid", uid);
                    hashMap.put("name", uname);
                    hashMap.put("gender", ugender);
                    hashMap.put("address", uadd);
                    hashMap.put("city", ucity);
                    hashMap.put("phone", uphone);
                    hashMap.put("highest_education", uhighest_education);
                    hashMap.put("college_city", ucollege_city);
                    hashMap.put("college_name", ucollege_name);
                    hashMap.put("image", image_uri_string);
                    hashMap.put("bio", about);


                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference reference = database.getReference("Users");
                    reference.child(uid).setValue(hashMap);
                    Toast.makeText(getActivity(), "Profile has been succesfully updated.",
                            Toast.LENGTH_SHORT).show();
                }
            }

        });
        editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditImageDialog();
            }
        });


        return root;
    }

    private void adapterSetting(ArrayList<String> listCity) {
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(getContext(),android.R.layout.simple_dropdown_item_1line,listCity);
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

    private void loadData()
    {

        Query querytutor = databaseReference.orderByChild("email").equalTo(user.getEmail());


        querytutor.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String uname = "" + ds.child("name").getValue();
                    String uemail = "" + ds.child("email").getValue();
                    String utype = "" + ds.child("user_type").getValue();
                    String ugender = "" + ds.child("gender").getValue();
                    String uaddress = "" + ds.child("address").getValue();
                    String ucity = "" + ds.child("city").getValue();
                    String uphone = "" + ds.child("phone").getValue();
                    String ucollege_city = "" + ds.child("college_city").getValue();
                    String ucollege_name = "" + ds.child("college_name").getValue();
                    String uhighest_education = "" + ds.child("highest_education").getValue();
                    String uimage = "" + ds.child("image").getValue();
                    String uabout = "" + ds.child("bio").getValue();
                    image_uri_string = "" + ds.child("image").getValue();
                    if (ugender.equals("Male")) {
                        gender.check(R.id.gender_male);
                    } else {
                        gender.check(R.id.gender_female);
                    }
                    user_name.setText(uname);
                    user_name_show.setText("Hi," + uname);
                    user_email.setText(uemail);
                    address.setText(uaddress);
                    city.setText(ucity);
                    phone.setText(uphone);
                    college_city.setText(ucollege_city);
                    college_name.setText(ucollege_name);
                    highest_qualification.setText(uhighest_education);
                    show_about.setText(uabout);


//                    u_type.setText(utype);
                    try {
                        Picasso.get().load(uimage).into(user_avatar);
                    } catch (Exception e) {
                        Picasso.get().load(R.drawable.user_icon).into(user_avatar);
                    }


                }
                pd.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermission() {
        requestPermissions(storagePermissions, STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    private void requestCameraPermission() {
        requestPermissions(cameraPermissions, CAMERA_REQUEST_CODE);
    }

    private void showEditImageDialog() {
        String options[] = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Pick Image From");

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    if (!checkCameraPermission()) {
                        requestCameraPermission();
                    } else {
                        pickFromCamera();
                    }

                } else if (which == 1) {
                    if (!checkStoragePermission()) {
                        requestStoragePermission();
                    } else {
                        pickFromGallery();
                    }

                }

            }
        });
        builder.create().show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && writeStorageAccepted) {
                        pickFromCamera();
                    } else {
                        Toast.makeText(getActivity(), "Please enable camera & storage permission", Toast.LENGTH_SHORT).show();
                    }
                }

            }
            case STORAGE_REQUEST_CODE: {

                boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                if (writeStorageAccepted) {
                    pickFromGallery();
                } else {
                    Toast.makeText(getActivity(), "Please enable storage permission", Toast.LENGTH_SHORT).show();
                }

            }
            break;
        }


        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                image_uri = data.getData();
                uploadProfilePhoto(image_uri);

            }
            if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                uploadProfilePhoto(image_uri);
            }
        }


        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadProfilePhoto(final Uri image_uri) {
        pd.setMessage("Image uploading......");
        pd.setCanceledOnTouchOutside(false);
        pd.show();
        String filePathAndName = storagePath + "profile" + "_" + user.getUid();
        StorageReference storageReference2nd = storageReference.child(filePathAndName);
        storageReference2nd.putFile(image_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful()) ;
                Uri donwloadUri = uriTask.getResult();

                if (uriTask.isSuccessful()) {
                    HashMap<String, Object> result = new HashMap<>();
                    result.put("image", donwloadUri.toString());
                    databaseReference.child(user.getUid()).updateChildren(result).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            pd.dismiss();
                            Toast.makeText(getActivity(), "Image Updated.... But you are so ugly....", Toast.LENGTH_SHORT).show();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Toast.makeText(getActivity(), "Error updating image....", Toast.LENGTH_SHORT).show();

                        }
                    });

                }
//                else
//                    pd.dismiss();
//                Toast.makeText(getActivity(),"Some error occured",Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void pickFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, IMAGE_PICK_GALLERY_CODE);
    }

    private void pickFromCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Temp Pic");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Temp Description");
        image_uri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);
    }
}