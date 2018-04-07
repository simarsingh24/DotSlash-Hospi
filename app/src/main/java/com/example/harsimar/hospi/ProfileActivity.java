package com.example.harsimar.hospi;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.Firebase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by shivaraju on 7/4/18.
 */

public class ProfileActivity extends AppCompatActivity {

    DatabaseReference databaseData;
    Button submitgoInside;

    EditText name;
    EditText aadhar;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);

        name = (EditText) findViewById(R.id.e1);
        aadhar = (EditText) findViewById(R.id.e2);

        submitgoInside = findViewById(R.id.but2);

        databaseData = FirebaseDatabase.getInstance().getReference("Aadhar Number");

        submitgoInside.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean q = false;
                q = addData();

                if (q) {
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(i);
                } else {
                    Toast.makeText(ProfileActivity.this, "Enter all details", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    public boolean addData(){

        String aadharData= aadhar.getText().toString().trim();
        Intent i2 = new Intent(this,MainActivity.class);
        i2.putExtra("aadhar",aadharData);

        String nm = name.getText().toString().trim();
        if (!TextUtils.isEmpty(nm)){

            String id1=databaseData.push().getKey();
            Data msg = new Data(id1,nm);
            databaseData.child(aadharData).child("name").setValue(nm);

        }else{
            Toast.makeText(this, "enter name", Toast.LENGTH_SHORT).show();
        }
        return true;
    }


    public static void aData(String aadhar, String  name){

    }
}
