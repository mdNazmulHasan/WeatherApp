package nazmul.weatherapp.webapi;

import nazmul.weatherapp.models.CurrentWeather;
import nazmul.weatherapp.models.WeatherData;
import retrofit2.Call;
import retrofit2.http.GET;

public interface WeatherApi {
    @GET("data/2.5/find?lat=23.68&lon=90.35&cnt=50&appid=e384f9ac095b2109c751d95296f8ea76")
    Call<WeatherData>getAllWeatherInfo();
    @GET("data/2.5/weather?lat=23.68&lon=90.35&appid=e384f9ac095b2109c751d95296f8ea76")
    Call<CurrentWeather>getCurrentWeather();
}
