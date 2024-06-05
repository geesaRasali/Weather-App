package com.geesar.weatherapplication;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
public class MainActivity extends AppCompatActivity {
    private FusedLocationProviderClient fusedLocationClient;
    private TextView latitudeTextView, longitudeTextView, addressTextView, timeTextView, weatherInfoTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        latitudeTextView = findViewById(R.id.exlatitude);
        longitudeTextView = findViewById(R.id.exlongitude);
        addressTextView = findViewById(R.id.exaddress);
        timeTextView = findViewById(R.id.excurrent_time);
        weatherInfoTextView = findViewById(R.id.exweather_info);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            fetchLocation();
        }

        updateTime();
    }

    @SuppressLint("MissingPermission")
    private void fetchLocation() {
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                latitudeTextView.setText("Latitude: " + latitude);
                longitudeTextView.setText("Longitude: " + longitude);

                Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                try {
                    List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                    if (addresses != null && addresses.size() > 0) {
                        Address address = addresses.get(0);
                        addressTextView.setText("Address: " + address.getAddressLine(0));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                fetchWeatherData(latitude, longitude);
            }
        });
    }

    private void fetchWeatherData(double latitude, double longitude) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Getweather service = retrofit.create(Getweather.class);
        Call<WeatherGetdata> call = service.getCurrentWeather(latitude, longitude, "534ef841b1b840d6099214fa81f2daad");
        call.enqueue(new Callback<WeatherGetdata>() {
            @Override
            public void onResponse(@NonNull Call<WeatherGetdata> call, @NonNull Response<WeatherGetdata> response) {
                if (response.isSuccessful()) {
                    WeatherGetdata weather = response.body();
                    if (weather != null) {
                        String weatherInfo = "Temperature: " + weather.getMain().getTemp() + "°C\n" +
                                "Humidity: " + weather.getMain().getHumidity() + "%\n" +
                                "Description: " + weather.getWeather().get(0).getDescription();
                        weatherInfoTextView.setText(weatherInfo);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<WeatherGetdata> call, @NonNull Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void updateTime() {
        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        timeTextView.setText("Current Time: " + currentTime);
    }
}