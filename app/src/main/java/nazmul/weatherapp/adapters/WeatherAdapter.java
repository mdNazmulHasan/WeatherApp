package nazmul.weatherapp.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import nazmul.weatherapp.R;
import nazmul.weatherapp.models.List;

public class WeatherAdapter extends ArrayAdapter<List> {
    private Context context;
    private java.util.List<List>weatherDataList;

    public WeatherAdapter(@NonNull Context context,@NonNull java.util.List<List> objects) {
        super(context, R.layout.weather_row, objects);
        this.context=context;
        this.weatherDataList=objects;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view= LayoutInflater.from(context).inflate(R.layout.weather_row,parent,false);
        TextView cityText=view.findViewById(R.id.city_text);
        TextView conditionText=view.findViewById(R.id.condition_text);
        TextView temperatureText=view.findViewById(R.id.temperature_text);
        List list=weatherDataList.get(position);
        cityText.setText(list.getName());
        conditionText.setText(list.getWeather().get(0).getMain());
        char degree = '\u00B0';
        double temp=list.getMain().getTemp()-273.15;
        temperatureText.setText(String.format("%.1f",temp)+""+degree+"c");
        return view;
    }
}
