package com.example.asusstrix.security;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.util.ArrayList;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class listActivity extends AppCompatActivity {

    protected ListView customers;
    ArrayAdapter<String> adapter;
    ArrayList<String> items;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        customers=findViewById(R.id.userName);
        items = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);

        customers.setAdapter(adapter);

        SharedPreferences shPref=getSharedPreferences("shPrefFile",MODE_PRIVATE);
        Map<String,?> keys = shPref.getAll();

        String list="";
        for(Map.Entry<String,?> entry : keys.entrySet()){
            Log.d("map values",entry.getKey() + ": " +
                    entry.getValue().toString());
            items.add(entry.getKey());
            adapter.notifyDataSetChanged();
        }

        Intent receivedIn=getIntent();
        final String secretKey=receivedIn.getStringExtra("key");
        customers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedFromList = (customers.getItemAtPosition(i).toString());

                SharedPreferences bPref=getSharedPreferences("balancePrefFile",MODE_PRIVATE);
                //beditor.apply();

                String currentBalance=aes.decrypt(bPref.getString(selectedFromList,""),secretKey);
                Toast.makeText(getApplicationContext(), selectedFromList+"\nBalance: "+currentBalance, Toast.LENGTH_SHORT).show();


                // Transform the chars to a String

            }
        });
        //TODO: read customer history and balance by decrypting customer's log file
    }

}
