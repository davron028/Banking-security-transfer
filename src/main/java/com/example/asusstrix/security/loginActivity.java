package com.example.asusstrix.security;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.util.Random;


public class loginActivity extends AppCompatActivity {
    protected Button LoginButton;
    protected Button SignUpButton;
    protected EditText UserId;
    protected EditText Password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();

    }

    private void init()
    {
        LoginButton=findViewById(R.id.login_btn);
        SignUpButton=findViewById(R.id.signup_btn);
        UserId= findViewById(R.id.user_id);
        Password=findViewById(R.id.password);

        SignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usr=UserId.getText().toString();
                String pwd=Password.getText().toString();

                SharedPreferences shPref=getSharedPreferences("shPrefFile",MODE_PRIVATE);
                SharedPreferences.Editor editor=shPref.edit();


                SharedPreferences bPref=getSharedPreferences("balancePrefFile",MODE_PRIVATE);
                SharedPreferences.Editor beditor=bPref.edit();

                //region salt generation
                final Random r = new SecureRandom();
                byte[] pre_salt = new byte[20];
                r.nextBytes(pre_salt);
                //endregion



                String salt = null;
                try {
                    salt = new String(pre_salt, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                if(shPref.contains(usr))
                {
                    Toast.makeText(getApplicationContext(),"User already exists",Toast.LENGTH_LONG).show();
                }
                else if(usr.length()<6||pwd.length()<6)
                {
                    Toast.makeText(getApplicationContext(), "Please enter Id or Password not less than 6!!!", Toast.LENGTH_SHORT).show();
                }
                else {
                    try {
                        editor.putString(usr,salt+";"+hashing.SHA1(pwd));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    beditor.putString(usr,aes.encrypt("0",salt+pwd));
                    beditor.apply();
                    editor.apply();
                    Toast.makeText(getApplicationContext(),"User is saved",Toast.LENGTH_LONG).show();
                }
            }
        });
        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usr=UserId.getText().toString();
                String pwd=Password.getText().toString();
                SharedPreferences shPref=getSharedPreferences("shPrefFile",MODE_PRIVATE);
                String savedPwd=shPref.getString(usr,"");
                String[]hash_pwd=savedPwd.split(";");// 0 element is salt and 1 element is hash pwd
                try {
                    if(hash_pwd[1].equals(hashing.SHA1(pwd)))
                    {
                        Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                        intent.putExtra("loginName",usr);
                        intent.putExtra("secret",hash_pwd[0]+pwd);
                        startActivity(intent);
                        //SaveSharedPreference.setUserName(getApplicationContext(),usr);
                        finish();

                        //create empty log file (balance and history)

                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),"Please check username or password",Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
