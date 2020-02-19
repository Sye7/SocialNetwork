package com.example.socialnetwork.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.socialnetwork.R;
import com.example.socialnetwork.model.Profile;
import com.example.socialnetwork.model.UserLoginModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class SignUpActivity extends AppCompatActivity {
    Toolbar toolbar;
    EditText email, password, name;
    CheckBox checkBox;
    ImageButton signup;
    Button signin;
    private FirebaseAuth mAuth;

    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_sign_up);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);

        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        name = (EditText) findViewById(R.id.name);

        checkBox = (CheckBox) findViewById(R.id.checkbox);

        signup = (ImageButton) findViewById(R.id.signup);

        signin = (Button) findViewById(R.id.signin);

        mAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference("UserLoginModel");


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                createAccount();
            }
        });

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SignUpActivity.this, StartActivity.class);
                startActivity(i);
                finish();
            }
        });


    }

    public void createAccount() {


        try {
            mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                FirebaseUser user = mAuth.getCurrentUser();
                                Toast.makeText(SignUpActivity.this, "Registered Successfully", Toast.LENGTH_LONG).show();
                                storeInDb();
                            } else {
                                //display some message here
                                Toast.makeText(SignUpActivity.this, "Email already exists", Toast.LENGTH_LONG).show();

                            }

                            // ...
                        }
                    });
        } catch (Exception e) {
            Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }


    }
/*
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

 */

    String userName;
    String userEmail;
    String userPass;
    String id;

    public void storeInDb() {
        userName = name.getText().toString().trim();
        userEmail = email.getText().toString();
        userPass = password.getText().toString();
        id = mAuth.getCurrentUser().getUid();

        if(userName == null || userName.length() <3 )
        {
            Toast.makeText(this, "Enter valid UserName", Toast.LENGTH_SHORT).show();
            return;
        }

        Profile profile = new Profile(id,userName,"Blogger",0,null,null,"noo");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Profile");
        ref.push().setValue(profile);


        UserLoginModel userLoginModel = new UserLoginModel(userName, userEmail, userPass,id );
        reference.push().setValue(userLoginModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                updateUI( mAuth.getCurrentUser());
            }

        });

    }

    public void updateUI(FirebaseUser user) {

        Intent intent = new Intent(getApplicationContext(), StartActivity.class);
        startActivity(intent);
    }
}
