package com.example.harsimar.hospi;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by harsimar on 07/04/18.
 */

public class JsonLoader {
    public static JSONArray loadJSONFromAsset(Context context) {
        BufferedReader reader=null;
        try {
            // open and read the file into a StringBuilder
            InputStream in =context.getAssets().open("hospital.json");
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
//            // do something here if needed from JSONObjects
//            for (int i = 0; i < array.length(); i++) {
//                JSONObject hospJson=array.getJSONObject(i);
//                returnArray.add(hospJson.getString(key));
//                //Log.d("harsimarSingh",hospJson.getString("District"));
//            }
            return array;

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
