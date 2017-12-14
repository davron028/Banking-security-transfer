package com.example.asusstrix.security;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    protected TextView login;
    protected Button del;
    protected Button chng;
    protected EditText et_pwd;
    protected Button logOutBtn;
    protected Button depositBtn;
    protected Button trasnferBtn;
    protected Button withdrawBtn;
    protected EditText moneyTXT;
    protected TextView balanceTxt;
    protected TextView historyTxt;
    AlertDialog alertDialog1;

    String name;
    String KEY;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        //TODO: write encrypted data to file
    }

    private String customerName;
    //temporary balance
    private String secretKey;
    private SharedPreferences bPref;
    private SharedPreferences.Editor beditor;

    //region Initialization
    private void init()
    {
        login=findViewById(R.id.user);
        del=findViewById(R.id.del);
        chng=findViewById(R.id.chng_pwd);
        et_pwd=findViewById(R.id.et_pwd);
        logOutBtn = findViewById(R.id.logout);
        depositBtn = findViewById(R.id.depositBtn);
        trasnferBtn = findViewById(R.id.transferBtn);
        withdrawBtn = findViewById(R.id.withdrawBtn);
        moneyTXT = findViewById(R.id.transaction);
        balanceTxt = findViewById(R.id.balance);



        Intent receivedIn=getIntent();
        customerName=receivedIn.getStringExtra("loginName");
        secretKey=receivedIn.getStringExtra("secret");
        login.setText(customerName);
        KEY = secretKey;

        //TODO: ebcrypted balance and history
          history(KEY);


        bPref=getSharedPreferences("balancePrefFile",MODE_PRIVATE);
        beditor=bPref.edit();
        //beditor.apply();

        String currentBalance=aes.decrypt(bPref.getString(customerName,""),secretKey);
        balanceTxt.setText(currentBalance);

        //region transaction and deletinig info from file
        String file = "temp";
        String data = readFromFile(file, getApplicationContext());
        String[] transaction = data.split(";");


        String newtemp="";
        for (int i = 0; i < transaction.length;i++)
        {
            if(customerName.equals(transaction[i]))
            {
                //TODO: finish
                int total = Integer.parseInt(balanceTxt.getText().toString()) + Integer.parseInt(transaction[i+1]);
                beditor.putString(customerName,aes.encrypt(String.valueOf(total),secretKey));
                beditor.apply();
                balanceTxt.setText(String.valueOf(total));
                transaction[i]=transaction[i+1]="";


            }

            if(!transaction[i].equals(""))
            {
                newtemp += transaction[i] + ";";
            }
        }
        writeToTmpFile(newtemp,file,getApplicationContext());
        history(KEY);
        //endregion



        //region Transaction operations
        if(Integer.parseInt(balanceTxt.getText().toString())<10000001)
        {
            depositBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String balance = balanceTxt.getText().toString();
                    String money = moneyTXT.getText().toString();
                    name = login.getText().toString() + "history";
                    int total;
                    if(money.isEmpty())
                    {
                        Toast.makeText(getApplicationContext(), "Please enter the number", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else if(Integer.parseInt(money) < 0)
                    {
                        Toast.makeText(getApplicationContext(), "Please enter the number", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String date = dateAndtime();
                    String encrypt = date + "\n" + "Deposit: " + money + "\n";
                    String encText = aes.encrypt(encrypt, KEY);
                    writeToFile(encText, name, getApplicationContext());

                    //String currentBalance=aes.decrypt(bPref.getString(customerName,""),secretKey);
                    total = Integer.parseInt(balanceTxt.getText().toString()) + Integer.parseInt(money);
                    beditor.putString(customerName,aes.encrypt(String.valueOf(total),secretKey));
                    beditor.apply();
                    balanceTxt.setText(String.valueOf(total));
                    moneyTXT.setText("");
                    history(KEY);

                }
            });
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Account can not accept more than 10 million", Toast.LENGTH_SHORT).show();
        }


        withdrawBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //String currentBalance=aes.decrypt(bPref.getString(customerName,""),secretKey);
                String money = moneyTXT.getText().toString();
                int total;
                if(money.isEmpty())
                {
                    Toast.makeText(getApplicationContext(), "Please enter the number", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(Integer.parseInt(balanceTxt.getText().toString())>Integer.parseInt(money)){
                    total = Integer.parseInt(balanceTxt.getText().toString()) - Integer.parseInt(money);
                    beditor.putString(customerName,aes.encrypt(String.valueOf(total),secretKey));
                    beditor.apply();
                    balanceTxt.setText(String.valueOf(total));
                    String date = dateAndtime();
                    String encrypt = date + "\n" + "Withdraw: " + "-" + money + "\n";
                    String encText = aes.encrypt(encrypt, KEY);
                    writeToFile( encText, name, getApplicationContext());
                    moneyTXT.setText("");
                    history(KEY);
                }
                else if(Integer.parseInt(balanceTxt.getText().toString()) == 0){
                    Toast.makeText(getApplicationContext(), "Your balance is empty", Toast.LENGTH_SHORT).show();
                    moneyTXT.setText("");
                    return;
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Error check your balance", Toast.LENGTH_SHORT).show();
                    moneyTXT.setText("");
                    return;

                }
                //balanceTxt.setText(total);
                moneyTXT.setText("");
            }
        });

        //trasnfering substracting from balance and checking the transaction money have to be less than balance
        trasnferBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Choose username");
                SharedPreferences shPref = getSharedPreferences("shPrefFile", MODE_PRIVATE);
                Map<String, ?> keys = shPref.getAll();
                final List<String> listItems = new ArrayList<String>();
                for (Map.Entry<String, ?> entry : keys.entrySet()) {
                    listItems.add(entry.getKey());
                }
                String money = moneyTXT.getText().toString();
                String balance = balanceTxt.getText().toString();
                if (money.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please enter amount of money", Toast.LENGTH_SHORT).show();
                    return;
                } else if (Integer.parseInt(balance) < Integer.parseInt(money)) {
                    Toast.makeText(getApplicationContext(), "You don't have enough money", Toast.LENGTH_SHORT).show();
                    moneyTXT.setText("");
                    return;
                } else {
                    final CharSequence[] customList = listItems.toArray(new CharSequence[listItems.size()]);
                    builder.setSingleChoiceItems(customList, -1, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int item) {
                            String money = moneyTXT.getText().toString();
                            String balance = balanceTxt.getText().toString();
                            name = login.getText().toString() + "history";
                            int total=(Integer.parseInt(balance) - Integer.parseInt(money));
                            beditor.putString(customerName,aes.encrypt(String.valueOf(total),secretKey));
                            beditor.apply();
                            balanceTxt.setText(String.valueOf(total));
                            writeToFile(customList[item] + ";" + money + ";", "temp", getApplicationContext());
                            String DateToStr = dateAndtime();
                            String encrypt = DateToStr + "\n" + "Transfered to " + customList[item] + "   " + money + "\n";
                            String encText = aes.encrypt(encrypt, KEY);
                            writeToFile(encText, name, getApplicationContext());

                            moneyTXT.setText("");
                            history(KEY);
                            alertDialog1.dismiss();
                        }
                    });
                    alertDialog1 = builder.create();
                    alertDialog1.show();

                }
            }
        });
        beditor.apply();
        //TODO: Transfer
        //endregion

        //region User Management
        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences mySPrefs = getSharedPreferences("shPrefFile",MODE_PRIVATE);
                SharedPreferences.Editor editor = mySPrefs.edit();
                editor.remove(customerName);
                beditor.remove(customerName);
                deleteFile(customerName+"history.txt");
                editor.apply();
                Intent i=new Intent(getApplicationContext(),loginActivity.class);
                startActivity(i);
                finish();
            }
        });



        chng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String new_pwd=et_pwd.getText().toString();
                if(new_pwd.length()<6)
                {
                    Toast.makeText(getApplicationContext(), "Password should be 6 or more elements"+new_pwd.length(), Toast.LENGTH_SHORT).show();
                }
                else {
                    SharedPreferences shPref=getSharedPreferences("shPrefFile",MODE_PRIVATE);
                    SharedPreferences.Editor editor=shPref.edit();
                    String savedPwd=shPref.getString(customerName,"");
                    String[]hash_pwd=savedPwd.split(";");// 0 element is salt and 1 element is hash pwd
                    try {
                        editor.putString(customerName,hash_pwd[0]+";"+hashing.SHA1(new_pwd));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    editor.apply();
                    Toast.makeText(getApplicationContext(), "Password changed", Toast.LENGTH_SHORT).show();
                }

            }
        });
        //endregion

    }
 //endregion

    //region OptionMenu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.customer_list:
                customers_list();
                return true;
            case R.id.logout:
                logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void customers_list()
    {
        Intent intent=new Intent(getApplicationContext(),listActivity.class);
        intent.putExtra("key",secretKey);
        startActivity(intent);
    }
    //endregion

    private void logout()
    {
        Intent i=new Intent(getApplicationContext(),loginActivity.class);
        startActivity(i);
        finish();
    }
    private void writeToFile(String data,String filename,Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(filename+".txt", Context.MODE_APPEND));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private String readFromFile(String filename,Context context) {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput(filename+".txt");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }

    private void writeToTmpFile(String data,String filename,Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(filename+".txt", context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private String dateAndtime()
    {
        Date curDate = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");
        String DateToStr = format.format(curDate);
        return DateToStr;
    }

    private void history(String KEY){
        String historyList = readFromFile(name, getApplicationContext());
        String decrypt = aes.decrypt(historyList,KEY);
        String[] histArr = decrypt.split("\n");
        String text = "";
        for (int i = 0; i<histArr.length;i++)
        {

            if (histArr[i] != null && !histArr[i].trim().isEmpty())
                text += histArr[i] + "\n";

        }
        historyTxt = findViewById(R.id.history);
        historyTxt.setText("");
        historyTxt.setText(text);
    }
}
