package com.example.weatherapplication;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {
    EditText cityName;
    TextView resultTextView;
    public void findWeather(View view){
        if(cityName.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(), "Enter A City Name !", Toast.LENGTH_LONG).show();
        }else{
            InputMethodManager mgr = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(cityName.getWindowToken(),0);
            try {
                String encodedCityName = URLEncoder.encode(cityName.getText().toString(),"UTF-8");
                DownloadTask downloadTask = new DownloadTask();
                downloadTask.execute("http://api.openweathermap.org/data/2.5/weather?q="+encodedCityName+"&APPID=85f2e91d24eb6552495fbf7f5912d9ec");
            } catch (UnsupportedEncodingException e) {
                Toast.makeText(getApplicationContext(), "Could Not Find Weather", Toast.LENGTH_LONG).show();
            }
        }
    }

    public class DownloadTask extends AsyncTask<String,Void,String>{
        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection httpURLConnection = null;
            try{
                url = new URL(urls[0]);
                httpURLConnection = (HttpURLConnection)url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                int data = inputStreamReader.read();
                while (data != -1){
                    char current = (char)data;
                    result = result + current;
                    data = inputStreamReader.read();
                }
                return result;
            }catch(Exception e){
                Toast.makeText(getApplicationContext(), "Could Not Find Weather", Toast.LENGTH_LONG).show();
            }
            return result;
        }
        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            try{
                String message = "";
                JSONObject jsonObject = new JSONObject(result);
                String weatherInfo = jsonObject.getString("weather");
                Log.i("Weather Content",weatherInfo);
                JSONArray jsonArray = new JSONArray(weatherInfo);
                for(int i = 0; i < jsonArray.length() ; i++){
                    JSONObject jsonPart = jsonArray.getJSONObject(i);
                    String main = "";
                    String description = "";
                    main = jsonPart.getString("main");
                    description = jsonPart.getString("description");
                    if(main != "" && description != ""){
                    message = message + "Main : " + main + " Description : "+description;
                    }
                }
                if(message != ""){
                    resultTextView.setText(message);
                }
            }catch(JSONException e){
                Toast.makeText(getApplicationContext(), "Could Not Find Weather", Toast.LENGTH_LONG).show();
            }
        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cityName = (EditText)findViewById(R.id.cityName);
        resultTextView = (TextView)findViewById(R.id.weatherTextView);
    }
}
