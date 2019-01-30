package com.giovannimartinus.weatherapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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
