package com.smtafe.acmecurrencyconverter;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * JSONFetch Class
 *
 * @author Tom Green
 * @version 1.0
 */

public class JSONFetch {

    //getJSON method to fetch live json data from the apilayer server
    public static JSONObject getJSON(Context context) {
        //Live API Call String
        final String CURRENCYLAYER_API = "http://www.apilayer.net/api/live?access_key=";
        try {
            //Create a new URL using the API String and the api key
            URL url = new URL(String.format(CURRENCYLAYER_API +
                    context.getString(R.string.currencylayer_access_key) + "&format=1"));

            //Create a HttpURLConnection using the url
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(connection.getInputStream()));

            StringBuffer json = new StringBuffer(1024);
            String tmp = "";

            //While the connection returns data append it to json
            while ((tmp = reader.readLine()) != null)
                json.append(tmp).append("\n");
            reader.close();

            //Create a JSONObject using the StringBuffer json
            JSONObject data = new JSONObject(json.toString());

            //Save the JSON
            saveJSON(context, data);

            return data;
        } catch (Exception e) {
            return null;
        }
    }

    //historicalComparison method to get data from a specific date
    public static JSONObject historicalComparison(Context context, String date) {
        //Historical API Call String
        final String CURRENCYLAYER_API = "http://apilayer.net/api/historical?access_key=";
        try {
            //Create a new URL using the API String, the api key and the date
            URL url = new URL(String.format(CURRENCYLAYER_API +
                    context.getString(R.string.currencylayer_access_key) +
                    "&date=" + date));

            //Create a HttpURLConnection using the url
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(connection.getInputStream()));

            StringBuffer json = new StringBuffer(1024);
            String tmp = "";

            //While the connection returns data append it to json
            while ((tmp = reader.readLine()) != null)
                json.append(tmp).append("\n");
            reader.close();

            //Create a JSONObject using the StringBuffer json
            JSONObject data = new JSONObject(json.toString());

            //Save the JSON
            saveJSON(context, data);

            return data;
        } catch (Exception e) {
            return null;
        }
    }

    //saveJSON method to save json data to the file "api-data.json"
    public static void saveJSON(Context context, JSONObject json) {
        Writer output = null;
        try {
            //Create/Overwrite the file with the json data
            File file = new File(context.getFilesDir(), "api-data.json");
            output = new BufferedWriter(new FileWriter(file));
            output.write(json.toString());
            output.close();
        } catch (Exception e) {
            return;
        }
    }

    //Save Conversion method to save the conversions
    public static void saveConversion(Context context, JSONObject json, String saveName,
                                      String input, String exchange, String conversion1,
                                      String conversion2, int picker1, int picker2) {
        Writer output = null;
        String jsonString = json.toString().substring(0, json.toString().length() - 1);
        jsonString += ",\"input\":\"" + input + "\",\"exchange\":\"" + exchange
                + "\",\"comparison1\":\"" + conversion1 + "\",\"comparison2\":\""
                + conversion2 + "\",\"picker1\":" + picker1 + ",\"picker2\":"
                + picker2 + "}";
        try {
            json = new JSONObject(jsonString);

            //Create/Overwrite the file with the json data
            File file = new File(context.getFilesDir(), "saves");
            //If the directory doesn't exist, create it
            if (!file.exists())
                file.mkdir();
            file = new File(context.getFilesDir(), "saves/" + saveName + ".json");
            output = new BufferedWriter(new FileWriter(file));
            output.write(json.toString());
            output.close();
        } catch (Exception e) {
            return;
        }
    }

    //loadJSON method to load the JSON data from "api-data.json"
    public static JSONObject loadJSON(Context context) throws JSONException, IOException, ClassNotFoundException {
        JSONObject json = null;
        String path = "/api-data.json";

        //Create a new FileInputStream using the api-data.json file
        InputStream is = new FileInputStream(context.getFilesDir() + path);
        BufferedReader buf = new BufferedReader(new InputStreamReader(is));

        String line = buf.readLine();
        StringBuilder sb = new StringBuilder();

        //While the file returns data append it to the StringBuilder
        while (line != null) {
            sb.append(line).append("\n");
            line = buf.readLine();
        }

        String fileAsString = sb.toString();

        //Use fileAsString to create a JSONObject
        json = new JSONObject(fileAsString);
        return json;
    }

    //loadConversion method to load a Conversion from the saves file
    public static JSONObject loadConversion(Context context, String saveName) throws JSONException, IOException {
        JSONObject json = null;
        String path = "/saves/" + saveName + ".json";

        //Create a new FileInputStream using the file in the path
        InputStream is = new FileInputStream(context.getFilesDir() + path);
        BufferedReader buf = new BufferedReader(new InputStreamReader(is));

        String line = buf.readLine();
        StringBuilder sb = new StringBuilder();

        //While the file returns data append it to the StringBuilder
        while (line != null) {
            sb.append(line).append("\n");
            line = buf.readLine();
        }

        String fileAsString = sb.toString();

        //Use fileAsString to create a JSONObject
        json = new JSONObject(fileAsString);
        return json;
    }
}
