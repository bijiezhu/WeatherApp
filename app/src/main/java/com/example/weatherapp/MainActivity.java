package com.example.weatherapp;

import android.content.DialogInterface;
import android.database.DefaultDatabaseErrorHandler;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.DateFormat;
import android.icu.text.DecimalFormat;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.sql.Date;
import java.util.zip.DataFormatException;

import Util.Utils;
import data.CityPreference;
import data.JSONWeatherParser;
import data.WeatherHttpClient;
import model.Weather;

public class MainActivity extends AppCompatActivity {

    private TextView cityName;
    private TextView temp;
    private ImageView iconView;
    private TextView  description;
    private TextView  humidity;
    private TextView pressure;
    private TextView wind;
    private TextView sunrise;
    private TextView sunset;
    private TextView updated;

    Weather weather=new Weather();




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setContentView(R.layout.activity_main);

   //try to fectch the text view to id
        // Displays text to the user and optionally allows them to edit it
        cityName=(TextView) findViewById(R.id.cityText);
        temp=(TextView) findViewById(R.id.tempText);
        description=(TextView) findViewById(R.id.cloudText);
        humidity=(TextView) findViewById(R.id.humidText);
        pressure=(TextView) findViewById(R.id.pressureText);
        wind=(TextView) findViewById(R.id.windText);
        sunrise=(TextView) findViewById(R.id.riseText);
        sunset=(TextView) findViewById(R.id.setText);
        updated=(TextView) findViewById(R.id.updateText);

        CityPreference cityPreference=new CityPreference(MainActivity.this);

        renderWeatherData(cityPreference.getCity());
    }

    public void renderWeatherData(String city){
        WeatherTask weatherTask=new WeatherTask();
        //try to fetch data now
        weatherTask.execute(new String[]{city+"&units=metric"});

        }


    //reduce the word load for app and do things in the background
    private class WeatherTask extends AsyncTask<String,Void,Weather>{

        @Override
        protected Weather doInBackground(String... params){
            //everything in the background away from the main thread application
            //instantiate weatherhttpclient class
            //params is the string passed in

            //first get data then parse it
            String data=((new WeatherHttpClient()).getWeatherData(params[0]));

            weather= JSONWeatherParser.getWeather(data);

            //test printout
            //Log.v("Data: ",weather.currentCondition.getDescription());

            return weather;
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected void onPostExecute(Weather weather){
            //populate the data you get and show the user
            super.onPostExecute(weather);

            //format the date
            DateFormat df= null;

                df = DateFormat.getTimeInstance();
                String sunriseDate=df.format(new Date(weather.place.getSunrise()));
                String sunsetDate=df.format(new Date(weather.place.getSunset()));
                String updateDate=df.format(new Date(weather.place.getLastupdate()));

            DecimalFormat decimalFormat=new DecimalFormat("#,#");

            String tempFormat=decimalFormat.format(weather.currentCondition.getTemperature());





            //print to the app
            cityName.setText(weather.place.getCity()+","+weather.place.getCountry());
            temp.setText(""+tempFormat+"°C");
            humidity.setText("Humidity:"+weather.currentCondition.getHumidity()+"%");
            pressure.setText("pressure:"+weather.currentCondition.getPressure()+"hPa");
            wind.setText("Wind: "+weather.wind.getSpeed()+"mps");
            sunrise.setText("Sunrise"+ sunriseDate);
            sunset.setText("Sunset :"+sunriseDate);
            updated.setText("Last Updated: "+ updateDate);
            description.setText("Condition :"+weather.currentCondition.getCondition()+"("+
            weather.currentCondition.getDescription()+")");


        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu_main,menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item){
        int id=item.getItemId();

        if(id==R.id.change_cityId){
            showInputDialog();
        }

        return super.onOptionsItemSelected(item);
    }


    private void showInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Change City");

        final EditText cityInput = new EditText(MainActivity.this);
        cityInput.setInputType(InputType.TYPE_CLASS_TEXT);
        cityInput.setHint("Portland,US");
        builder.setView(cityInput);
        builder.setPositiveButton("Submit",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                CityPreference cityPreference = new CityPreference(MainActivity.this);
                cityPreference.setCity(cityInput.getText().toString());

                String newCity = cityPreference.getCity();

                renderWeatherData(newCity);

            }

        });
        builder.show();



    }


}

