package nazmul.weatherapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import nazmul.weatherapp.models.WeatherData;
import nazmul.weatherapp.utils.RetrofitClient;
import nazmul.weatherapp.webapi.WeatherApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherActivity extends AppCompatActivity {
    WeatherApi weatherApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        getWeatherData();
    }

    private void getWeatherData() {
        weatherApi= RetrofitClient.getRetrofitClient().create(WeatherApi.class);
        Call<WeatherData> weatherDataCall=weatherApi.getAllWeatherInfo();
        weatherDataCall.enqueue(new Callback<WeatherData>() {
            @Override
            public void onResponse(Call<WeatherData> call, Response<WeatherData> response) {
                WeatherData weatherData=response.body();
                Log.e("weather data ", "onResponse: "+weatherData.getMessage() );
            }

            @Override
            public void onFailure(Call<WeatherData> call, Throwable t) {

            }
        });


    }
}
