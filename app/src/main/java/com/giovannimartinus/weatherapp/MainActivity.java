package com.giovannimartinus.weatherapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    ImageView dayTimeIcon;
    ImageView homeBackgroundView;
    ImageView weatherBackgroundView;
    TextView cityTextView;
    TextView degreeTextView;
    TextView percentageTextView;
    TextView pressureTextView;
    TextView weatherTextView;
    TextView windSpeedTextView;

    public class DownloadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection;

            try {
                // connect to the browser with the given url
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();

                // download the input stream
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader streamReader = new InputStreamReader(inputStream);

                int data = streamReader.read();

                // add scraped web content to result
                while (data != -1) {
                    char currentChar = (char) data;
                    result += currentChar;
                    data = streamReader.read();
                }

                return result;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {

                // convert get string of web content to JSON object
                JSONObject jsonObject = new JSONObject(result);
                String weatherInfo = jsonObject.getString("weather");
                String temperatureInfo = jsonObject.getString("main");
                String windInfo = jsonObject.getString("wind");

                // pass strings of JSON objects to a JSON array
                JSONArray jsonWeatherArray = new JSONArray(weatherInfo);
                JSONArray jsonTemperatureArray = new JSONArray(temperatureInfo);
                JSONArray jsonWindArray = new JSONArray(windInfo);

                // get parts (values) of json arrays
                for (int i = 0;
                        i < jsonWeatherArray.length() &&
                        i < jsonTemperatureArray.length() &&
                        i < jsonWindArray.length(); i++) {
                    JSONObject jsonWeatherPart = jsonWeatherArray.getJSONObject(i);
                    JSONObject jsonTemperaturePart = jsonTemperatureArray.getJSONObject(i);
                    JSONObject jsonWindPart = jsonWindArray.getJSONObject(i);

                    // log to test and ensure correct values are retrieved
                    Log.i("Weather", jsonWeatherPart.getString("main"));
                    Log.i("Temperature", jsonTemperaturePart.getString("temp"));
                    Log.i("Humidity", jsonTemperaturePart.getString("humidity"));
                    Log.i("Air Pressure", jsonTemperaturePart.getString("pressure"));
                    Log.i("Wind Speed", jsonWindPart.getString("speed"));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void returnButton(View view) {}

    public void searchButton(View view) {}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dayTimeIcon = (ImageView) findViewById(R.id.dayTimeIcon);
        homeBackgroundView = (ImageView) findViewById(R.id.homeBackgroundView);
        weatherBackgroundView = (ImageView) findViewById(R.id.weatherBackgroundView);
        cityTextView = (TextView) findViewById(R.id.cityTextView);
        degreeTextView = (TextView) findViewById(R.id.degreeTextView);
        percentageTextView = (TextView) findViewById(R.id.percentageTextView);
        pressureTextView = (TextView) findViewById(R.id.pressureTextView);
        weatherTextView = (TextView) findViewById(R.id.weatherTextView);
        windSpeedTextView = (TextView) findViewById(R.id.windSpeedTextView);
    }
}
