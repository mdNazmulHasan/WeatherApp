package nazmul.weatherapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import nazmul.weatherapp.adapters.WeatherAdapter;
import nazmul.weatherapp.models.List;
import nazmul.weatherapp.models.WeatherData;
import nazmul.weatherapp.utils.RetrofitClient;
import nazmul.weatherapp.webapi.WeatherApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherActivity extends AppCompatActivity {
    private static final int NOTIFICATION_REMINDER_NIGHT = 2;
    WeatherApi weatherApi;
    ListView weatherdataListView;
    ProgressDialog progDailog;
    public static String mapdata="mapdata";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        weatherdataListView=findViewById(R.id.weatherdataListView);
        progDailog=new ProgressDialog(this);
        getWeatherData();
        setRepeatingNotification();
    }

    private void setRepeatingNotification() {
        Intent notifyIntent = new Intent(this,MyReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast
                (this, NOTIFICATION_REMINDER_NIGHT, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,  System.currentTimeMillis(),
                1000 * 60 * 60 * 24, pendingIntent);
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
                weatherdataListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent=new Intent(WeatherActivity.this,MapsActivity.class);
                        List list= (List) parent.getItemAtPosition(position);
                        intent.putExtra(mapdata,list);
                        startActivity(intent);
                    }
                });
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
