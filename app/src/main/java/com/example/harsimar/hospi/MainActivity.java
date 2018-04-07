package com.example.harsimar.hospi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public static final String PREF_NAME = "DotSlash-Hospi";
    public static final String IS_LOGIN = "is_logged_in";
    int PRIVATE_MODE = 0;

    private SharedPreferences mPref;
    static SharedPreferences.Editor mEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPref = getApplicationContext().getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        mEditor = mPref.edit();

        String isLoggedIn = mPref.getString(IS_LOGIN, null);

        if (isLoggedIn == null) {
            //Toast.makeText(this,"going in",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, Login_Activity.class));
            finish();
        } else {

        }
        Button butt =  (Button) findViewById(R.id.but);
        butt.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                startActivity(new Intent(MainActivity.this,ProfileActivity.class));
            }
        });
    }



}
