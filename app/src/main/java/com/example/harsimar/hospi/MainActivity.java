package com.example.harsimar.hospi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

        Log.d("harsimarSingh",loadValueOfKeyJSONFromAsset("District").toString());


    }
    public ArrayList<String> loadValueOfKeyJSONFromAsset(String key) {
        BufferedReader reader=null;
        try {
            // open and read the file into a StringBuilder
            InputStream in =getApplicationContext().getAssets().open("hospital.json");
            reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder jsonString = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                // line breaks are omitted and irrelevant
                jsonString.append(line);
            }
            // parse the JSON using JSONTokener
            JSONArray array = (JSONArray) new JSONTokener(jsonString.toString()).nextValue();
            ArrayList<String> returnArray = new ArrayList<>();
            // do something here if needed from JSONObjects
            for (int i = 0; i < array.length(); i++) {
                JSONObject hospJson=array.getJSONObject(i);
                returnArray.add(hospJson.getString(key));
                //Log.d("harsimarSingh",hospJson.getString("District"));
            }
            return returnArray;

        } catch (FileNotFoundException e) {
            // we will ignore this one, since it happens when we start fresh
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null)
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return null;
    }
}
