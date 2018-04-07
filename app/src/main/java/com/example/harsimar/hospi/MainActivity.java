package com.example.harsimar.hospi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button butt =  (Button) findViewById(R.id.but);
//        butt.setOnClickListener(new Button.OnClickListener(){
//            public void onClick(View v){
//                startActivity(new Intent(MainActivity.this,ProfileActivity.class));
//            }
//        });
    }



        //Log.d("harsimarSingh",loadValueOfKeyJSONFromAsset("District").toString());
        Intent intent = new Intent(MainActivity.this,HospiMaps.class);
        startActivity(intent);

    }

}
