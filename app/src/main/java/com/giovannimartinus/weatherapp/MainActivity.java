package com.giovannimartinus.weatherapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
    RelativeLayout homeLayout;
    RelativeLayout weatherLayout;

    final DownloadTask downloadTask = new DownloadTask();

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

                // get string of weather
                String weatherInfo = jsonObject.getString("weather");
                Log.i("Weather Info", weatherInfo);

                // get string of temperature, air pressure, and humidity
                JSONObject temperatureInfo = jsonObject.getJSONObject("main");
                String tempInfo = temperatureInfo.getString("temp");
                String pressureInfo = temperatureInfo.getString("pressure");
                String humidityInfo = temperatureInfo.getString("humidity");
                Log.i("Temperature (Kelvin)", tempInfo);
                Log.i("Barometric Pressure", pressureInfo);
                Log.i("Humidity Percentage", humidityInfo);

                // get string of wind speed
                JSONObject windSpeedInfo = jsonObject.getJSONObject("wind");
                String windInfo = windSpeedInfo.getString("speed");
                Log.i("Wind Speed (Knots)", windInfo);

                // pass strings of JSON objects to a JSON array
                JSONArray jsonWeatherArray = new JSONArray(weatherInfo);

                // get parts (values) of json arrays
                for (int i = 0;
                        i < jsonWeatherArray.length(); i++) {
                    JSONObject jsonWeatherPart = jsonWeatherArray.getJSONObject(i);

                    // log to test and ensure correct values are retrieved
                    Log.i("Weather", jsonWeatherPart.getString("main"));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void returnButton(View view) {
        weatherLayout.setVisibility(View.GONE);
        homeLayout.setVisibility(View.VISIBLE);
    }

    public void searchButton(View view) {
        EditText cityEditText = (EditText) findViewById(R.id.cityEditText);

        try {
            downloadTask.execute("https://api.openweathermap.org/data/2.5/weather?q=" +
                    cityEditText.getText() + "&appid=d459a5bba54428ba7c66c626f2e5495f");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
        homeLayout = (RelativeLayout) findViewById(R.id.homeLayout);
        weatherLayout = (RelativeLayout) findViewById(R.id.weatherLayout);
    }
}
