package nazmul.weatherapp;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

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
    private static final int LOCATION_REQUEST = 1;
    WeatherApi weatherApi;
    ListView weatherdataListView;
    ProgressDialog progDailog;
    public static String mapdata = "mapdata";
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    String lat,lng;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createLocationRequest();
        setContentView(R.layout.activity_weather);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        weatherdataListView = findViewById(R.id.weatherdataListView);
        progDailog = new ProgressDialog(this);
        getWeatherData();
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                lat=String.valueOf(locationResult.getLocations().get(0).getLatitude());
                lng=String.valueOf(locationResult.getLocations().get(0).getLongitude());
                startJob(lat,lng);


            }
        };

    }

    private void startJob(String lat, String lng) {
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
        Bundle bundle=new Bundle();
        bundle.putString("lat",lat);
        bundle.putString("lng",lng);

        Job myJob = dispatcher.newJobBuilder()
                .setService(WeatherJob.class)
                .setTag("InfoJobService")
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(3 * 60, 5 * 60))
                .setLifetime(Lifetime.UNTIL_NEXT_BOOT)
                .setReplaceCurrent(false)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setRetryStrategy(RetryStrategy.DEFAULT_LINEAR)
                .setExtras(bundle)
                .build();

        dispatcher.mustSchedule(myJob);
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(WeatherActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_REQUEST);
        }else{
            mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback,
                    null /* Looper */);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==LOCATION_REQUEST){
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                startLocationUpdates();
            }else{
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
            return;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
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
