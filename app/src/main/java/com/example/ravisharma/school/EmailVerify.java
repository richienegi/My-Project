package com.example.ravisharma.school;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ravisharma.school.model.Info;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EmailVerify extends AppCompatActivity {

    String id, frstN, lstN, em, radioText;
    TextView emailVerify;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verify);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        databaseRef = FirebaseDatabase.getInstance().getReference("users");

        emailVerify = findViewById(R.id.emailVerified);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("info", MODE_PRIVATE);
        id = pref.getString("id", "");
        frstN = pref.getString("frstN", "");
        lstN = pref.getString("lstN", "");
        em = pref.getString("em", "");
        radioText = pref.getString("radioText", "");

        Log.d("INFO 2", (id+" "+radioText+" "+frstN+" "+lstN+" "+em));
        Toast.makeText(this, mUser.getDisplayName(), Toast.LENGTH_SHORT).show();

        if(mUser.isEmailVerified()){
            Toast.makeText(EmailVerify.this, "hello", Toast.LENGTH_SHORT).show();
            Info inf = new Info(id, frstN, lstN, em, radioText);
            databaseRef.child(mAuth.getCurrentUser().getUid()).setValue(inf);
            startActivity(new Intent(EmailVerify.this, Dashboard.class));
            finish();
            SharedPreferences pref2 = getApplicationContext().getSharedPreferences("info", MODE_PRIVATE);
            pref2.edit().clear();

        }
        else{
            Toast.makeText(EmailVerify.this, "not hello", Toast.LENGTH_SHORT).show();
            emailVerify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mUser.sendEmailVerification();
                    emailVerify.setText("Email Sent \n Please Check Your Mail Box");
                    Intent i = new Intent(EmailVerify.this, Login.class);
                    Toast.makeText(EmailVerify.this, "Verify your Email and Login", Toast.LENGTH_SHORT).show();
                    startActivity(i);
                    finish();

                }
            });
        }

    }
}
