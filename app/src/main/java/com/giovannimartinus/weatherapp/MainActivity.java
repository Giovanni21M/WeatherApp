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

    final WeatherConditions weatherConditions = new WeatherConditions();
    final WeatherTemplate weatherTemplate = new WeatherTemplate();

    private class WeatherConditions {
        private int temperature;
        private int pressure;
        private int humidity;
        private int wind;
        private String weather;

        public int getTemperature() {
            return temperature;
        }

        public int getPressure() {
            return pressure;
        }

        public int getHumidity() {
            return humidity;
        }

        public int getWind() {
            return wind;
        }

        public String getWeather() {
            return weather;
        }

        public void setTemperature(int temperature) {
            // need to convert from Kelvin to Celsius
            this.temperature = temperature - 273;
        }

        public void setPressure(int pressure) {
            this.pressure = pressure;
        }

        public void setHumidity(int humidity) {
            this.humidity = humidity;
        }

        public void setWind(int wind) {
            this.wind = wind;
        }

        public void setWeather(String weather) {
            this.weather = weather;
        }
    }

    private class WeatherTemplate {
        private void searchFunction() {
            weatherLayout.setVisibility(View.VISIBLE);
            homeLayout.setVisibility(View.GONE);
            EditText cityEditText = (EditText) findViewById(R.id.cityEditText);
            final DownloadTask downloadTask = new DownloadTask();

            try {
                downloadTask.execute("https://api.openweathermap.org/data/2.5/weather?q=" +
                        cityEditText.getText() + "&appid=d459a5bba54428ba7c66c626f2e5495f");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

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

                // get and convert string of web content to JSON object
                JSONObject jsonObject = new JSONObject(result);

                // get list of temperature, air pressure, and humidity
                JSONObject conditionsInfo = jsonObject.getJSONObject("main");

                // set temperature in WeatherConditions()
                String temperature = conditionsInfo.getString("temp");
                Double temperatureDouble = Double.parseDouble(temperature);
                int temperatureInfo = temperatureDouble.intValue();
                weatherConditions.setTemperature(temperatureInfo);

                // set air pressure in WeatherConditions()
                String airPressure = conditionsInfo.getString("pressure");
                Double pressureDouble = Double.parseDouble(airPressure);
                int pressureInfo = pressureDouble.intValue();
                weatherConditions.setPressure(pressureInfo);

                // set humidity in WeatherConditions()
                String humidity = conditionsInfo.getString("humidity");
                Double humidityDouble = Double.parseDouble(humidity);
                int humidityInfo = humidityDouble.intValue();
                weatherConditions.setPressure(humidityInfo);

                // get string of wind speed from JSONobject and set in WeatherConditions()
                JSONObject windSpeedInfo = jsonObject.getJSONObject("wind");
                String windSpeed = windSpeedInfo.getString("speed");
                Double windDouble = Double.parseDouble(windSpeed);
                int windInfo = windDouble.intValue();
                weatherConditions.setPressure(windInfo);


                // get string of weather pass strings of JSON objects to a JSON array
                String weatherInfo = jsonObject.getString("weather");
                JSONArray jsonWeatherArray = new JSONArray(weatherInfo);

                // get parts (values) of json arrays and set in WeatherConditions()
                for (int i = 0; i < jsonWeatherArray.length(); i++) {
                    JSONObject jsonWeatherPart = jsonWeatherArray.getJSONObject(i);
                    weatherConditions.setWeather(jsonWeatherPart.getString("main"));

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
        weatherTemplate.searchFunction();
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
