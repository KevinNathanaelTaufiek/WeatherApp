package com.kevinnt.weatherapp;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WeatherAPIService {

    public static final String  QUERY_REQUEST_CITY_ID   = "https://www.metaweather.com/api/location/search/?query=";
    public static final String  QUERY_REQUEST_WEATHER_BY_CITY_ID   = "https://www.metaweather.com/api/location/";

    private Context context;

    public WeatherAPIService(Context context) {
        this.context = context;
    }

    public interface VolleyResponseListener{
        void onResponse(String cityId);

        void onError(String message);
    }

    public void getCityID(String cityName, VolleyResponseListener volleyResponseListener){
        //RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        String url = QUERY_REQUEST_CITY_ID  + cityName;

        // Request a string response from the provided URL.
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        String woeid = "";
                        try {
                            JSONObject jsonObject = response.getJSONObject(0);
                            woeid = jsonObject.getString("woeid");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //Toast.makeText(context, "City ID is " + woeid, Toast.LENGTH_SHORT).show();
                        volleyResponseListener.onResponse(woeid);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(context, "Error: " + error.toString(), Toast.LENGTH_SHORT).show();
                volleyResponseListener.onError(error.toString());
            }
        });

        // Add the request to the RequestQueue.
        //queue.add(request);
        QueueSingelton.getInstance(context).addToRequestQueue(request);
    }

    public interface WeatherVolleyResponseListener{
        void onResponse(List<CityWeatherModel> cityWeathers);

        void onError(String message);
    }

    public void getWeatherById (String cityId, WeatherVolleyResponseListener weatherVolleyResponseListener){

        List<CityWeatherModel> cityWeathersReport = new ArrayList<>();

        String url = QUERY_REQUEST_WEATHER_BY_CITY_ID + cityId;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
//                        Toast.makeText(context, response.toString(), Toast.LENGTH_SHORT).show();

                        try {
                            JSONArray consolidated_weather = response.getJSONArray("consolidated_weather");

                            for(int i=0; i<consolidated_weather.length(); i++) {
                                JSONObject closestItem = (JSONObject) consolidated_weather.get(i);

                                CityWeatherModel cityWeatherModel = new CityWeatherModel();

                                cityWeatherModel.setId(closestItem.getString("id"));
                                cityWeatherModel.setWeather_state_name(closestItem.getString("weather_state_name"));
                                cityWeatherModel.setWeather_state_abbr(closestItem.getString("weather_state_abbr"));
                                cityWeatherModel.setWind_direction_compass(closestItem.getString("wind_direction_compass"));
                                cityWeatherModel.setCreated(closestItem.getString("created"));
                                cityWeatherModel.setApplicable_date(closestItem.getString("applicable_date"));
                                cityWeatherModel.setMin_temp((float) closestItem.getDouble("min_temp"));
                                cityWeatherModel.setMax_temp((float) closestItem.getDouble("max_temp"));
                                cityWeatherModel.setThe_temp((float) closestItem.getDouble("the_temp"));
                                cityWeatherModel.setWind_speed((float) closestItem.getDouble("wind_speed"));
                                cityWeatherModel.setWind_direction((float) closestItem.getDouble("wind_direction"));
                                cityWeatherModel.setAir_pressure((float) closestItem.getDouble("air_pressure"));
                                cityWeatherModel.setHumidity(closestItem.getInt("humidity"));
                                cityWeatherModel.setVisibility((float) closestItem.getDouble("visibility"));
                                cityWeatherModel.setPredictability(closestItem.getInt("predictability"));

                                cityWeathersReport.add(cityWeatherModel);
                            }

                            weatherVolleyResponseListener.onResponse(cityWeathersReport);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }



                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(context, "ERROR", Toast.LENGTH_SHORT).show();
                weatherVolleyResponseListener.onError(error.toString());
            }
        });

        QueueSingelton.getInstance(context).addToRequestQueue(request);

    }

    public interface WeatherByNameCallback{
        void onResponse(List<CityWeatherModel> cityWeathers);
        void onError (String message);
    }


    public void getWeatherByName(String cityName, WeatherByNameCallback weatherByNameCallback){

        getCityID(cityName, new VolleyResponseListener() {
            @Override
            public void onResponse(String cityId) {
                getWeatherById(cityId, new WeatherVolleyResponseListener() {
                    @Override
                    public void onResponse(List<CityWeatherModel> cityWeathers) {
                        weatherByNameCallback.onResponse(cityWeathers);
                    }

                    @Override
                    public void onError(String message) {

                    }
                });
            }

            @Override
            public void onError(String message) {

            }
        });

    }

}
