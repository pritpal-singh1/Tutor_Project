package com.example.tutor_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RegisterActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    Button mRegisterBtn;
    EditText inputname, inputemail, inputpasword;
    Spinner input_account_type;
    ProgressDialog loadingBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().setTitle("Register Here");
        TextView lRegister = findViewById(R.id.lRegister);

        mRegisterBtn = findViewById(R.id.registerBtn);
        inputname = findViewById(R.id.user_name);
        inputemail = findViewById(R.id.user_email);
        inputpasword = findViewById(R.id.user_password);
//        input_account_type=findViewById(R.id.account_type);
        loadingBar = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();

        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccount();

            }
        });


        Button rlogin = findViewById(R.id.rLoginBtn);
        rlogin.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        }));


//        Spinner spinner = findViewById(R.id.account_type);
//        spinner.setOnItemSelectedListener(this);

//        List<String> categories = new ArrayList<>();
//        categories.add("Tutor");
//        categories.add("Student");
//
//        ArrayAdapter<String> a = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories);
//        a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner.setAdapter(a);

    }

    private void createAccount() {
//        String user_type=input_account_type.getSelectedItem().toString();
        String name = inputname.getText().toString();
        String email = inputemail.getText().toString();
        String password = inputpasword.getText().toString();
        if (TextUtils.isEmpty((name))) {
            Toast.makeText(this, "Please write your name.......", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty((email))) {
            Toast.makeText(this, "Please write your email.......", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty((password))) {
            Toast.makeText(this, "Please write your password.......", Toast.LENGTH_SHORT).show();
        } else {
            loadingBar.setTitle("Create Button");
            loadingBar.setMessage("Please wait, while we are checking the credentials.");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            registerUser(name,email, password);
        }

    }

    private void registerUser(final String name,final String email, String password) {
        loadingBar.show();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                                FirebaseUser user = mAuth.getCurrentUser();
                                Toast.makeText(RegisterActivity.this, "Congratulations your account has been successfully created.",
                                        Toast.LENGTH_SHORT).show();
                                String uid = user.getUid();
                                long timestamp=System.currentTimeMillis();
                                HashMap<Object, String> hashMap = new HashMap<>();
                                hashMap.put("email", email);
                                hashMap.put("uid", uid);
                                hashMap.put("name", name);
                                hashMap.put("bio", "");
                                hashMap.put("gender", "");
                                hashMap.put("address", "");
                                hashMap.put("city", "");
                                hashMap.put("phone", "");
                                hashMap.put("joinedAt", String.valueOf(System.currentTimeMillis()));
                                hashMap.put("highest_education", "");
                                hashMap.put("college_name", "");
                                hashMap.put("college_city", "");
                                hashMap.put("image","");


                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference reference = database.getReference("Users");
                                reference.child(uid).setValue(hashMap);

                                Intent intent = new Intent(RegisterActivity.this, ProfileActivity.class);
                                startActivity(intent);
                                finish();


                        } else {
                            // If sign in fails, display a message to the user.

                            new MaterialAlertDialogBuilder(RegisterActivity.this)
                                    .setTitle("Error")
                                    .setMessage("The Email You Are Trying To Register Is Already Registered. Please Try With Different Email.")
                                    .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                        }
                                    })

                                    .show();


                        }

                        // ...
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loadingBar.dismiss();
                Toast.makeText(RegisterActivity.this, "Authentication failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void validateEmail(final String name, final String email, final String password) {
        final DatabaseReference rootref;
        rootref = FirebaseDatabase.getInstance().getReference();
        rootref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!(snapshot.child("Users").child(email).exists())) {
                    HashMap<String, Object> userdataMap = new HashMap<>();
                    userdataMap.put("name", name);
                    userdataMap.put("email", email);
                    userdataMap.put("password", password);
                    userdataMap.put("image","");

                    rootref.child("Users").child(email).updateChildren(userdataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {


                                        Toast.makeText(RegisterActivity.this, "Congratulations your account has been successfully created.", Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();
                                    } else {
                                        Toast.makeText(RegisterActivity.this, "Network Error: Please Try After Some Time", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(RegisterActivity.this, "This " + email + " already esists.", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {


    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}


