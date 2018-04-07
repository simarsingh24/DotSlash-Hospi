package com.example.harsimar.hospi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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



}
