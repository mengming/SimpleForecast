package com.example.phelps.simpleforecast.Base;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Phelps on 2016/8/21.
 */
public class JsonReader {

    public JSONObject jsonObject(){
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("assets/cityList.json");
        InputStreamReader streamReader = new InputStreamReader(inputStream);
        BufferedReader reader = new BufferedReader(streamReader);
        String line;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            reader.close();
            reader.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            JSONObject cityJson = new JSONObject(stringBuilder.toString());
            return cityJson;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}
