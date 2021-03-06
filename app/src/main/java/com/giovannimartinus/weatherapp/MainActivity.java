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
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    ImageView dayTimeIcon;
    ImageView homeBackgroundView;
    ImageView weatherBackgroundView;
    TextView cityTextView;
    TextView degreeButton;
    TextView percentageTextView;
    TextView pressureTextView;
    TextView weatherTextView;
    TextView windSpeedTextView;
    RelativeLayout homeLayout;
    RelativeLayout weatherLayout;

    final WeatherConditions weatherConditions = new WeatherConditions();
    final WeatherTemplate weatherTemplate = new WeatherTemplate();

    // encapsulation class
    private class WeatherConditions {
        private long timeStamp;
        private long sunriseTimeStamp;
        private long sunsetTimestamp;
        private String temperature;
        private String pressure;
        private String humidity;
        private String wind;
        private String weather;

        public long getTimeStamp() {
            return timeStamp;
        }

        public long getSunriseTimeStamp() {
            return sunriseTimeStamp;
        }

        public long getSunsetTimestamp() {
            return sunsetTimestamp;
        }

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

        public void setTimeStamp(long timeStamp) {
            this.timeStamp = timeStamp;
        }

        public void setSunriseTimeStamp(long sunriseTimeStamp) {
            this.sunriseTimeStamp = sunriseTimeStamp;
        }

        public void setSunsetTimestamp(long sunsetTimestamp) {
            this.sunsetTimestamp = sunsetTimestamp;
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
        boolean celsius = true;

        private void searchFunction() {
            EditText cityEditText = (EditText) findViewById(R.id.cityEditText);

            // ensure search is not empty
            if (cityEditText.getText().toString() == null || cityEditText.getText().toString().trim().isEmpty()) {
                Toast.makeText(MainActivity.this, "Enter The Name of a City", Toast.LENGTH_SHORT).show();
            } else {
                weatherLayout.setVisibility(View.VISIBLE);
                homeLayout.setVisibility(View.GONE);

                final DownloadTask downloadTask = new DownloadTask();

                try {
                    // encode string to url and call downloadTask
                    String encodedCityName = URLEncoder.encode(cityEditText.getText().toString(), "UTF-8");

                    downloadTask.execute("https://api.openweathermap.org/data/2.5/weather?q=" +
                            encodedCityName + "&appid=d459a5bba54428ba7c66c626f2e5495f");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        private void layoutSettings() {
            final EditText cityEditText = (EditText) findViewById(R.id.cityEditText);

            // set text views of city and current weather/climate conditions
            cityTextView.setText(cityEditText.getText().toString());
            degreeButton.setText(weatherConditions.getTemperature() + "\u00B0");
            percentageTextView.setText(weatherConditions.getHumidity() + "%");
            pressureTextView.setText(weatherConditions.getPressure() + "hpa");
            weatherTextView.setText(weatherConditions.getWeather());
            windSpeedTextView.setText(weatherConditions.getWind() + "km/h");


            // set background - based on current weather
            switch (weatherConditions.getWeather().toLowerCase()) {
                case "thunderstorm":
                    weatherBackgroundView.setImageResource(R.drawable.thunderstormbg);
                    break;
                case "drizzle":
                    weatherBackgroundView.setImageResource(R.drawable.rainbg);
                    break;
                case "rain":
                    weatherBackgroundView.setImageResource(R.drawable.rainbg);
                    break;
                case "snow":
                    weatherBackgroundView.setImageResource(R.drawable.snowbg);
                    break;
                case "clear":
                    weatherBackgroundView.setImageResource(R.drawable.sunnybg);
                    break;
                case "clouds":
                    weatherBackgroundView.setImageResource(R.drawable.cloudybg);
                    break;
                case "mist":
                    weatherBackgroundView.setImageResource(R.drawable.mistbg);
                    break;
                case "volcanic ash":
                    weatherBackgroundView.setImageResource(R.drawable.volcanicashbg);
                    break;
            }
        }

        // set day/night icon - based on current time
        private void timeOfDay() {
            long sunrise = weatherConditions.getSunriseTimeStamp();
            long sunset = weatherConditions.getSunsetTimestamp();
            long current = weatherConditions.getTimeStamp();

            if (current >= sunrise && current < sunset) {
                dayTimeIcon.setImageResource(R.drawable.dayicon);
            } else {
                dayTimeIcon.setImageResource(R.drawable.nighticon);
            }
        }

        // convert from celsius to fahrenheit or vice versa
        private void degreeConversion() {
            if (celsius == true) {
                celsius = false;

                int c = Integer.parseInt(weatherConditions.getTemperature());
                Double f = c * 1.8 + 32;
                int fahrenheit = f.intValue();

                degreeButton.setText(fahrenheit + "\u00B0");
            } else if (celsius == false) {
                celsius = true;
                degreeButton.setText(weatherConditions.getTemperature() + "\u00B0");
            }
        }

        private void returnFunction() {
            celsius = true;
            weatherLayout.setVisibility(View.GONE);
            homeLayout.setVisibility(View.VISIBLE);

            weatherBackgroundView.setImageResource(R.drawable.homepagebg);
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

                // get current timestamp and pass to weatherconditions
                String timeStamp = jsonObject.getString("dt");
                long longTimeStamp = Long.parseLong(timeStamp);
                weatherConditions.setTimeStamp(longTimeStamp);


                JSONObject sysInfo = jsonObject.getJSONObject("sys");

                // get sunrise timestamp and pass to weatherconditions
                String sunriseTimeStamp = sysInfo.getString("sunrise");
                long longSunrise = Long.parseLong(sunriseTimeStamp);
                weatherConditions.setSunriseTimeStamp(longSunrise);

                // get sunset timestamp and pass to weatherconditions
                String sunsetTimeStamp = sysInfo.getString("sunset");
                long longSunset = Long.parseLong(sunsetTimeStamp);
                weatherConditions.setSunsetTimestamp(longSunset);

                weatherTemplate.layoutSettings();
                weatherTemplate.timeOfDay();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void degreeButton(View view) {
        weatherTemplate.degreeConversion();
    }

    public void returnButton(View view) {
        weatherTemplate.returnFunction();
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
        degreeButton = (TextView) findViewById(R.id.degreeButton);
        percentageTextView = (TextView) findViewById(R.id.percentageTextView);
        pressureTextView = (TextView) findViewById(R.id.pressureTextView);
        weatherTextView = (TextView) findViewById(R.id.weatherTextView);
        windSpeedTextView = (TextView) findViewById(R.id.windSpeedTextView);
        homeLayout = (RelativeLayout) findViewById(R.id.homeLayout);
        weatherLayout = (RelativeLayout) findViewById(R.id.weatherLayout);
    }
}
