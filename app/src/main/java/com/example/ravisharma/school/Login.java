package com.example.ravisharma.school;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.ravisharma.school.model.Info;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Login extends AppCompatActivity implements View.OnClickListener{
    String radioText = null;
    Button newUser, login;
    ProgressDialog pdialog;
    FirebaseAuth mAuth;
    DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        newUser=(Button) findViewById(R.id.signIn);
        login=(Button) findViewById(R.id.logIn);
        pdialog = new ProgressDialog(Login.this);
        mAuth = FirebaseAuth.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference("users");
        newUser.setOnClickListener(this);
        login.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(v==newUser){
            newUserForm();
        }
        if(v==login){
            loginForm();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser()!=null){
            if(mAuth.getCurrentUser().isEmailVerified()){
                startActivity(new Intent(this, Dashboard.class));
                finish();
            }
        }
    }

    private void newUserForm(){
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        final View v = inflater.inflate(R.layout.box_newuser, null);
        alertDialog.setView(v);
        final AlertDialog dialog = alertDialog.create();
        dialog.setTitle("Registration");

        final EditText fname, lname, email, pswrd, repswrd;
        final RadioGroup rg;

        rg = (RadioGroup)v.findViewById(R.id.gender);
        fname = (EditText)v.findViewById(R.id.firstname);
        lname = (EditText)v.findViewById(R.id.lastname);
        email = (EditText)v.findViewById(R.id.email);
        pswrd = (EditText)v.findViewById(R.id.password);
        repswrd = (EditText)v.findViewById(R.id.rePassword);
        Button register = (Button)v.findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final String frstN = fname.getText().toString().trim();
                final String lstN = lname.getText().toString().trim();
                final String em = email.getText().toString().trim();
                final String ps = pswrd.getText().toString();
                String reps = repswrd.getText().toString();
                final int selected = rg.getCheckedRadioButtonId();
                Toast.makeText(Login.this, String.valueOf(selected), Toast.LENGTH_SHORT).show();
                final String id = databaseRef.push().getKey();

                if(frstN.isEmpty()){
                    Toast.makeText(Login.this, "Enter First Name", Toast.LENGTH_SHORT).show();
                    fname.requestFocus();
                    return;
                }
                else if(lstN.isEmpty()){
                    Toast.makeText(Login.this, "Enter Last Name", Toast.LENGTH_SHORT).show();
                    lname.requestFocus();
                    return;
                }
                else if(em.isEmpty()){
                    Toast.makeText(Login.this, "Enter Email ID", Toast.LENGTH_SHORT).show();
                    email.requestFocus();
                    return;
                }
                else if(ps.isEmpty()){
                    Toast.makeText(Login.this, "Enter Password", Toast.LENGTH_SHORT).show();
                    pswrd.requestFocus();
                    return;
                }
                else if(reps.isEmpty()){
                    Toast.makeText(Login.this, "Re-Enter Password", Toast.LENGTH_SHORT).show();
                    repswrd.requestFocus();
                    return;
                }
                else if(!ps.equals(reps)){
                    Toast.makeText(Login.this, "Enter Same Password", Toast.LENGTH_SHORT).show();
                    pswrd.requestFocus();
                    return;
                }
                else if(!Patterns.EMAIL_ADDRESS.matcher(em).matches()){
                    Toast.makeText(Login.this, "Enter Valid Email", Toast.LENGTH_SHORT).show();
                    email.requestFocus();
                    return;
                }
                else if(ps.length()<8){
                    Toast.makeText(Login.this, "Passoword Length is Short\nMinimum 8 Characters Required", Toast.LENGTH_SHORT).show();
                    pswrd.requestFocus();
                    return;
                }
                else if(selected==-1){
                    Toast.makeText(Login.this, "Select Gender", Toast.LENGTH_SHORT).show();
                }
                else{
                    pdialog.setMessage("Registering Please Wait...");
                    pdialog.setCancelable(false);
                    pdialog.show();
                    mAuth.createUserWithEmailAndPassword(em, ps)
                            .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        if(selected==1){
                                            radioText = "Male";
                                        }
                                        else if(selected==2){
                                            radioText = "Female";
                                        }
                                        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                                                .setDisplayName(frstN+" "+lstN)
                                                .build();
                                        mAuth.getCurrentUser().updateProfile(profileChangeRequest);
                                        SharedPreferences pref = getApplicationContext().getSharedPreferences("info", MODE_PRIVATE);
                                        SharedPreferences.Editor edit = pref.edit();
                                        edit.putString("id", id);
                                        edit.putString("frstN", frstN);
                                        edit.putString("lstN", lstN);
                                        edit.putString("em", em);
                                        edit.putString("radioText", radioText);
                                        edit.commit();
                                        Intent b = new Intent(Login.this, EmailVerify.class);
                                        startActivity(b);
                                        finish();
                                        Log.d("Info", (id+" "+radioText+" "+frstN+" "+lstN+" "+em));
                                        dialog.cancel();
                                    }
                                    else{
                                        Toast.makeText(Login.this, "Already Registered\nPlease Login", Toast.LENGTH_SHORT).show();
                                    }
                                    pdialog.dismiss();
                                }
                            });
                }
            }
        });
        dialog.show();
    }

    private void loginForm(){
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.box_login, null);
        alertDialog.setView(v);

        final AlertDialog dialog = alertDialog.create();
        dialog.setTitle("Login");

        final EditText email, password;
        email = (EditText)v.findViewById(R.id.login_email);
        password = (EditText)v.findViewById(R.id.login_password);
        Button login = (Button) v.findViewById(R.id.login_btn);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String em = email.getText().toString();
                String ps = password.getText().toString();

                if(em.isEmpty()){
                    Toast.makeText(Login.this, "Enter Email to Login", Toast.LENGTH_SHORT).show();
                }
                else if(ps.isEmpty()){
                    Toast.makeText(Login.this, "Enter Password to Login", Toast.LENGTH_SHORT).show();
                }
                else{
                    pdialog.setMessage("Logging In");
                    pdialog.show();
                    mAuth.signInWithEmailAndPassword(em, ps)
                            .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    pdialog.dismiss();
                                    if(task.isSuccessful()){
                                        Intent i = new Intent(Login.this, EmailVerify.class);
                                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(i);
                                        finish();
                                        dialog.cancel();
                                    }
                                    else {
                                        Toast.makeText(Login.this, "Enter Valid Information", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
        dialog.show();
    }

}
