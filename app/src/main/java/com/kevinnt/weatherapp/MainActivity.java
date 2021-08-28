package com.kevinnt.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button btn_get_city_id,btn_get_weather_by_id,btn_get_weather_by_name;
    private ListView lv_data;
    private EditText et_input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WeatherAPIService weatherAPIService = new WeatherAPIService(MainActivity.this);
        btn_get_city_id = findViewById(R.id.btn_get_city_id);
        btn_get_weather_by_id = findViewById(R.id.btn_get_weather_by_id);
        btn_get_weather_by_name = findViewById(R.id.btn_get_weather_by_name);
        lv_data = findViewById(R.id.lv_data);
        et_input = findViewById(R.id.et_input);

        btn_get_city_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                weatherAPIService.getCityID(et_input.getText().toString(), new WeatherAPIService.VolleyResponseListener() {
                    @Override
                    public void onResponse(String cityId) {
                        Toast.makeText(MainActivity.this, "City IDnya adalah " + cityId, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(String message) {
                        Toast.makeText(MainActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        btn_get_weather_by_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weatherAPIService.getWeatherById(et_input.getText().toString(), new WeatherAPIService.WeatherVolleyResponseListener() {
                    @Override
                    public void onResponse(List<CityWeatherModel> cityWeather) {
//                        Toast.makeText(MainActivity.this, cityWeather.toString(), Toast.LENGTH_SHORT).show();

                        ArrayAdapter cityWeathersReport = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, cityWeather);
                        lv_data.setAdapter(cityWeathersReport);
                    }

                    @Override
                    public void onError(String message) {
                        Toast.makeText(MainActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        btn_get_weather_by_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weatherAPIService.getWeatherByName(et_input.getText().toString(), new WeatherAPIService.WeatherByNameCallback() {
                    @Override
                    public void onResponse(List<CityWeatherModel> cityWeathers) {
                        ArrayAdapter cityWeathersReport = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, cityWeathers);
                        lv_data.setAdapter(cityWeathersReport);
                    }

                    @Override
                    public void onError(String message) {
                        Toast.makeText(MainActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


    }
}