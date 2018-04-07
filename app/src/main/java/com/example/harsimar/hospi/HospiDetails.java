package com.example.harsimar.hospi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

public class HospiDetails extends AppCompatActivity {

    private String hospiName;
    TextView hosName;
    TextView hosLoc;
    TextView hosCategory;
    TextView hosDisc;
    TextView hosContact;
    TextView hosweb;
    TextView hosEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospi_details);
        initViews();
        Bundle bundle= getIntent().getExtras();
        String hospiName = (String) bundle.getString(HospiMaps.HOSPI_NAME);
        Log.d("harsimarSingh","Recieved hos name "+hospiName);
        for(int i=0;i<HospiMaps.jsonArray.length();i++){
            try {
                JSONObject hospJosn = HospiMaps.jsonArray.getJSONObject(i);
                String hospName = hospJosn.getString("Hospital_Name");
                if(!hospName.equals(hospiName)){
                    continue;
                }
                String location = hospJosn.getString("Location");
                String category = hospJosn.getString("Hospital_Category");
                String discipline = hospJosn.getString("Discipline_Systems_of_Medicine");
                String telephone = hospJosn.getString("Telephone");
                String mobileNumber = hospJosn.getString("Mobile_Number");
                String emergencyNum = hospJosn.getString("Emergency_Num");
                String ambulancePhoneNo = hospJosn.getString("Ambulance_Phone_No");
                String bloodbankPhoneNo = hospJosn.getString("Bloodbank_Phone_No");
                String tollfree = hospJosn.getString("Tollfree");
                String hospital_primary_email_id = hospJosn.getString("Hospital_Primary_Email_Id");
                String secondary_email_id = hospJosn.getString("Hospital_Secondary_Email_Id");
                String website = hospJosn.getString("Website");

                hosName.setText(hospiName);
                hosLoc.setText(location);
                hosCategory.setText(category);
                hosDisc.setText(discipline);
                hosContact.setText(telephone + "\n"+mobileNumber+"\n"+emergencyNum+"\n"
                        +ambulancePhoneNo+"\n"+bloodbankPhoneNo+"\n"+tollfree);
                hosEmail.setText(hospital_primary_email_id + "\n"+secondary_email_id);
                hosweb.setText(website);



            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
        Button back = (Button)findViewById(R.id.back_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }


    private void initViews() {
        hosName = (TextView) findViewById(R.id.hos_name);
        hosLoc = (TextView) findViewById(R.id.hos_location);
        hosCategory = (TextView) findViewById(R.id.hos_category);
        hosContact = (TextView) findViewById(R.id.hos_contact);
        hosweb = (TextView) findViewById(R.id.hos_web);
        hosDisc = (TextView) findViewById(R.id.hos_discipline);
        hosName = (TextView) findViewById(R.id.hos_name);
        hosName = (TextView) findViewById(R.id.hos_name);
        hosEmail = (TextView) findViewById(R.id.hos_email);
    }
}
