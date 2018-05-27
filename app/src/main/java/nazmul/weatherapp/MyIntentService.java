package nazmul.weatherapp;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationManagerCompat;

import nazmul.weatherapp.models.CurrentWeather;
import nazmul.weatherapp.utils.RetrofitClient;
import nazmul.weatherapp.webapi.WeatherApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class MyIntentService extends IntentService {
    private static final int NOTIFICATION_ID = 3;
    WeatherApi weatherApi;

    public MyIntentService() {
        super("MyNewIntentService");
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle bundle=intent.getBundleExtra("data");
        String lat=bundle.getString("lat");
        String lng=bundle.getString("lng");
        String url="data/2.5/weather?lat="+lat+"&lon="+lng+"&appid=e384f9ac095b2109c751d95296f8ea76";
        weatherApi= RetrofitClient.getRetrofitClient().create(WeatherApi.class);
        Call<CurrentWeather>currentWeatherCall=weatherApi.getCurrentWeather();
        currentWeatherCall.enqueue(new Callback<CurrentWeather>() {
            @Override
            public void onResponse(Call<CurrentWeather> call, Response<CurrentWeather> response) {
                CurrentWeather currentWeather=response.body();
                double averageTemp=currentWeather.getMain().getTemp()-273.15;
                Notification.Builder builder = new Notification.Builder(MyIntentService.this);
                builder.setContentTitle("Weather App");
                char degree = '\u00B0';
                builder.setContentText("Current Temperature :"+String.format("%.1f",averageTemp)+""+degree+"c");
                builder.setSmallIcon(R.mipmap.ic_launcher_round);
                Intent notifyIntent = new Intent(MyIntentService.this, WeatherActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(MyIntentService.this, 2, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                //to be able to launch your activity from the notification
                builder.setContentIntent(pendingIntent);
                Notification notificationCompat = builder.build();
                NotificationManagerCompat managerCompat = NotificationManagerCompat.from(MyIntentService.this);
                managerCompat.notify(NOTIFICATION_ID, notificationCompat);
            }

            @Override
            public void onFailure(Call<CurrentWeather> call, Throwable t) {

            }
        });


    }
}
