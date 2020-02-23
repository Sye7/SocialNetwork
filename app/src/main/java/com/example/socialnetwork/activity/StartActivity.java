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

import com.example.socialnetwork.MainTabActivity;
import com.example.socialnetwork.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class StartActivity extends AppCompatActivity {

    Toolbar toolbar;
    EditText email, password;
    CheckBox checkBox;
    ImageButton signin;
    Button signup;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_start);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);

        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);

        checkBox = (CheckBox) findViewById(R.id.checkbox);

        signin = (ImageButton) findViewById(R.id.signin);

        signup = (Button) findViewById(R.id.signup);

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null)
        {
            startActivity(new Intent(getApplicationContext(), MainTabActivity.class));
        }

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                login();

            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(StartActivity.this, SignUpActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    public void login() {
        //logging in the user

        try {
            mAuth.signInWithEmailAndPassword(email.getText().toString().trim(), password.getText().toString().trim())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            //if the task is successfull
                            if (task.isSuccessful()) {
                                //start the profile activity
                                finish();
                                startActivity(new Intent(getApplicationContext(), MainTabActivity.class));
                            } else {
                                Toast.makeText(StartActivity.this, "Credentials does not match" , Toast.LENGTH_SHORT).show();
                            }
                        }

                    });
        } catch (Exception e) {
            Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }
}
