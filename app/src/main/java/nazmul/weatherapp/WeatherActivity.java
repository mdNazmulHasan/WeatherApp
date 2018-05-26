package nazmul.weatherapp;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import nazmul.weatherapp.adapters.WeatherAdapter;
import nazmul.weatherapp.models.WeatherData;
import nazmul.weatherapp.utils.RetrofitClient;
import nazmul.weatherapp.webapi.WeatherApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherActivity extends AppCompatActivity {
    WeatherApi weatherApi;
    ListView weatherdataListView;
    ProgressDialog progDailog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        weatherdataListView=findViewById(R.id.weatherdataListView);
        progDailog=new ProgressDialog(this);
        getWeatherData();

    }

    private void getWeatherData() {
        showprogessdialog();
        weatherApi= RetrofitClient.getRetrofitClient().create(WeatherApi.class);
        Call<WeatherData> weatherDataCall=weatherApi.getAllWeatherInfo();
        weatherDataCall.enqueue(new Callback<WeatherData>() {
            @Override
            public void onResponse(Call<WeatherData> call, Response<WeatherData> response) {
                hiddenProgressDialog();
                WeatherData weatherData=response.body();
                WeatherAdapter weatherAdapter=new WeatherAdapter(WeatherActivity.this,weatherData.getList());
                weatherdataListView.setAdapter(weatherAdapter);
            }

            @Override
            public void onFailure(Call<WeatherData> call, Throwable t) {
                hiddenProgressDialog();
                Toast.makeText(WeatherActivity.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });


    }
    public void showprogessdialog() {
        if (progDailog != null) {
            progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDailog.setMessage("Please wait...");
            progDailog.setCancelable(false);
            progDailog.show();

        }
    }

    public void hiddenProgressDialog() {
        if (progDailog != null) {
            progDailog.dismiss();
        }
    }
}
