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
import android.widget.Toast;

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
        private String temperature;
        private String pressure;
        private String humidity;
        private String wind;
        private String weather;

        public String getTemperature() {
            return temperature;
        }

        public String getPressure() {
            return pressure;
        }

        public String getHumidity() {
            return humidity;
        }

        public String getWind() {
            return wind;
        }

        public String getWeather() {
            return weather;
        }

        public void setTemperature(int temperature) {
            // need to convert from Kelvin to Celsius
            this.temperature = String.valueOf(temperature - 273);
        }

        public void setPressure(int pressure) {
            this.pressure = String.valueOf(pressure);
        }

        public void setHumidity(int humidity) {
            this.humidity = String.valueOf(humidity);
        }

        public void setWind(int wind) {
            this.wind = String.valueOf(wind);
        }

        public void setWeather(String weather) {
            this.weather = String.valueOf(weather);
        }
    }

    private class WeatherTemplate {
        private void searchFunction() {
            EditText cityEditText = (EditText) findViewById(R.id.cityEditText);

            if (cityEditText.getText().toString() == null || cityEditText.getText().toString().trim().isEmpty()) {
                Toast.makeText(MainActivity.this, "Enter The Name of a City", Toast.LENGTH_SHORT).show();
            } else {
                weatherLayout.setVisibility(View.VISIBLE);
                homeLayout.setVisibility(View.GONE);

                final DownloadTask downloadTask = new DownloadTask();

                try {
                    downloadTask.execute("https://api.openweathermap.org/data/2.5/weather?q=" +
                            cityEditText.getText() + "&appid=d459a5bba54428ba7c66c626f2e5495f");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        private void layoutSettings() {
            final EditText cityEditText = (EditText) findViewById(R.id.cityEditText);
            cityTextView.setText(cityEditText.getText().toString());
            degreeTextView.setText(weatherConditions.getTemperature() + "\u00B0");
            percentageTextView.setText(weatherConditions.getHumidity() + "%");
            pressureTextView.setText(weatherConditions.getPressure() + "hpa");
            weatherTextView.setText(weatherConditions.getWeather());
            windSpeedTextView.setText(weatherConditions.getWind() + "km/h");
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
                weatherConditions.setHumidity(humidityInfo);

                // get string of wind speed from JSONobject and set in WeatherConditions()
                JSONObject windSpeedInfo = jsonObject.getJSONObject("wind");
                String windSpeed = windSpeedInfo.getString("speed");
                Double windDouble = Double.parseDouble(windSpeed);
                int windInfo = windDouble.intValue();
                weatherConditions.setWind(windInfo);


                // get string of weather pass strings of JSON objects to a JSON array
                String weatherInfo = jsonObject.getString("weather");
                JSONArray jsonWeatherArray = new JSONArray(weatherInfo);

                // get parts (values) of json arrays and set in WeatherConditions()
                for (int i = 0; i < jsonWeatherArray.length(); i++) {
                    JSONObject jsonWeatherPart = jsonWeatherArray.getJSONObject(i);
                    weatherConditions.setWeather(jsonWeatherPart.getString("main"));
                }

                weatherTemplate.layoutSettings();

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
