package nazmul.weatherapp;

import android.graphics.Typeface;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import java.util.Date;

import nazmul.weatherapp.models.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    List list;
    TextView cityText,conditionText,humidityText,windText,maxTempText,minTempText,averageTempText;
    ImageView weatherIcon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        cityText=findViewById(R.id.city_text);
        conditionText=findViewById(R.id.condition_text);
        humidityText=findViewById(R.id.humidity_text);
        windText=findViewById(R.id.wind_text);
        maxTempText=findViewById(R.id.max_temp_text);
        minTempText=findViewById(R.id.min_temp_text);
        averageTempText=findViewById(R.id.average_temp_text);
        weatherIcon=findViewById(R.id.weather_icon);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        list= (List) getIntent().getSerializableExtra(WeatherActivity.mapdata);
        cityText.setText(list.getName());
        conditionText.setText(list.getWeather().get(0).getMain());
        humidityText.setText("Humidity: "+list.getMain().getHumidity());
        windText.setText("Wind Speed: "+list.getWind().getSpeed());
        double maxTemp=list.getMain().getTempMax()-273.15;
        double minTemp=list.getMain().getTempMin()-273.15;
        double averageTemp=list.getMain().getTemp()-273.15;
        char degree = '\u00B0';
        maxTempText.setText("Max. Temp: "+String.format("%.1f",maxTemp)+""+degree+"c");
        minTempText.setText("Min. Temp: "+String.format("%.1f",minTemp)+""+degree+"c");
        averageTempText.setText(String.format("%.1f",averageTemp)+""+degree+"c");
        String iconUrl = "http://openweathermap.org/img/w/" + list.getWeather().get(0).getIcon() + ".png";
        Picasso.get().load(iconUrl).into(weatherIcon);


    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng location = new LatLng(list.getCoord().getLat(), list.getCoord().getLon());
        mMap.addMarker(new MarkerOptions().position(location).title(list.getName()));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location,14));
    }

}
