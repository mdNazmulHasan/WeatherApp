package nazmul.weatherapp;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
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
    private static final String CHANNEL_ID = "weather_app1";
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
        Call<CurrentWeather>currentWeatherCall=weatherApi.getCurrentWeather(url);
        currentWeatherCall.enqueue(new Callback<CurrentWeather>() {
            @TargetApi(Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<CurrentWeather> call, Response<CurrentWeather> response) {
                CurrentWeather currentWeather=response.body();
                char degree = '\u00B0';
                double averageTemp=currentWeather.getMain().getTemp()-273.15;

                Intent notifyIntent = new Intent(MyIntentService.this, WeatherActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(MyIntentService.this, 2, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(MyIntentService.this, CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        .setContentTitle("Weather App")
                        .setContentText("Current Temperature :"+String.format("%.1f",averageTemp)+""+degree+"c")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    CharSequence name = getString(R.string.channel_name);
                    String description = getString(R.string.channel_description);
                    int importance = NotificationManager.IMPORTANCE_DEFAULT;
                    NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
                    channel.setDescription(description);
                    // Register the channel with the system; you can't change the importance
                    // or other notification behaviors after this
                    NotificationManager notificationManager = getSystemService(NotificationManager.class);
                    notificationManager.createNotificationChannel(channel);
                    notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
                }else{
                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MyIntentService.this);
                    notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
                }
            }

            @Override
            public void onFailure(Call<CurrentWeather> call, Throwable t) {

            }
        });


    }
}
