package com.example.yassinekarami.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class SecondActivity extends AppCompatActivity {

    TextView textView;
    SmsManager smsManager;
    String message ="bonjour";

    //private final String numero = "0658406185";
    private final String numero = "0650664099";


    // verification des permissions
    private static final int REQUEST_SEND_SMS = 1;
    private static final int REQUEST_GPS_LOCATION = 1;

    LocationManager locationManager;
    double myLatitude;
    double myLongitude;
    String adress = "";
    LocationListener locationListener;
    boolean sendFlag = false;

    Timer timer;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        textView = (TextView)findViewById(R.id.text_view);
        smsManager = SmsManager.getDefault();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        if (ContextCompat.checkSelfPermission(SecondActivity.this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.SEND_SMS},REQUEST_SEND_SMS);
            recreate();

        }


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION

                    ,Manifest.permission.ACCESS_COARSE_LOCATION},REQUEST_GPS_LOCATION);

            recreate();

        }

        // récupération des donnés GPS
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION ))
        {

            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

                    myLatitude = location.getLatitude();
                    myLongitude = location.getLongitude();
                    adress = getAdress(myLatitude,myLongitude);
                    message = getAdress(myLatitude,myLongitude);

                    if (!sendFlag)
                    {
                        smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(numero, null, message, null, null);
                        sendFlag = true;

                        textView.append(adress);
                        // on attend 5s avant de fermer l'application
                        Toast.makeText(SecondActivity.this, "Message envoyé", Toast.LENGTH_LONG).show();
                        timer = new Timer();
                        timer.schedule(new TimerTask() { // la classe timerTask permet de faire des actions, evenement aprés un certain temps
                            @Override
                            public void run() {
                                // on fait la transition d'activités, la classe intent permet de faire ce changement
                                timer.cancel();
                                finish();
                                moveTaskToBack(true);

                            }
                        },5000);
                    }
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            };

            locationManager.requestLocationUpdates("gps", 1, 0, locationListener);
        }


    }
    private boolean checkPermission(String permission)
    {
        int ok = ContextCompat.checkSelfPermission(this,permission);
        if (ok ==  PackageManager.PERMISSION_GRANTED)
            return true;

        return false;
    }

    private String getAdress(double latitude, double longitude)
    {
        String adress ="";
        Geocoder geocoder = new Geocoder(SecondActivity.this, Locale.getDefault());
        try
        {
            List<Address> adresseList = geocoder.getFromLocation(latitude,longitude,1);
            adress = adresseList.get(0).getAddressLine(0);
        }catch(IOException e )
        {
            textView.setText("erreur localisation");
        };
        return adress;
    }
}
