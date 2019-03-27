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
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class SecondActivity extends AppCompatActivity {


    SmsManager smsManager;
    String message = null;
    ProgressBar progressBar;
    private final String numero = "0658406185";


    // verification des permissions
    private static final int REQUEST_SEND_SMS = 1;
    private static final int REQUEST_GPS_LOCATION = 1;
    private static final int READ_PHONE_STATE = 1;

    LocationManager locationManager;
    LocationListener locationListener;

    double myLatitude;
    double myLongitude;
    boolean sendFlag = false;

    String adress = "";
    String choixUtilisateur = null;
    String numeroSerie = null;

    TelephonyManager tMgr;

    ViewFlipper viewFlipper;
    private ImageAdapter adapter = null;
    private int[] imageSamu = new int[]{R.drawable.slidesamu1, R.drawable.slidesamu2, R.drawable.slidesamu3};
    private int[] imagePompier = new int[]{R.drawable.slidepompiers1, R.drawable.slidepompiers2, R.drawable.slidepompiers3};
    private int[] imagePolice = new int[]{R.drawable.slidepolice1, R.drawable.slidepolice2, R.drawable.slidepolice3};
    private int[] imageAccident = new int[]{R.drawable.slideaccident1, R.drawable.slideaccident2, R.drawable.slideaccident3};


    private int[] slideImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_second);
        choixUtilisateur = getIntent().getStringExtra("choixUtilisateur");

      //  ViewPager viewPager = findViewById(R.id.viewPager);
        viewFlipper = findViewById(R.id.viewFlipper);
        progressBar = findViewById(R.id.progressBar);

        // check phone request
        if (checkPermission(Manifest.permission.READ_PHONE_STATE)) {
            // get  the number
            tMgr =  (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

            //Toast.makeText(SecondActivity.this, "ergerger "+tMgr.getLine1Number(), Toast.LENGTH_LONG).show();
            numeroSerie = tMgr.getSimSerialNumber();
        }
        else {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_PHONE_STATE},READ_PHONE_STATE);
            recreate();
        }

        switch (choixUtilisateur)
        {
            case "Pompier":

                message = "Besoin des POMPIERS à l'adresse : ";
                slideImage = imagePompier;
                break;
            case "Pompier,samu et police" :

                message = "Besoin des POMPIERS, du SAMU et de la POLICE à l'adresse : ";
                slideImage = imageAccident;
                break;
            case "Police" :

                message = "Besoin de la POLICE à l'adresse : ";
                slideImage = imagePolice;
                break;
            case "Ambulance" :

                message = "Besoin d'une AMBULANCE à l'adresse : ";
                slideImage = imageSamu;
                break;
        }

        for (int image : slideImage)
        {
            flipperImage(image);
        }

     //   viewPager.setAdapter(adapter);

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

                    if (!sendFlag)
                    {
                        message = message +" "+  getAdress(myLatitude,myLongitude)+"\nInfo Complementaire \nNumero de serie : "+numeroSerie;
                        smsManager = SmsManager.getDefault();

                        smsManager.sendTextMessage(numero, null, message , null, null);
                        sendFlag = true;

                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(SecondActivity.this, "Message envoyé", Toast.LENGTH_SHORT).show();
                        Toast.makeText(SecondActivity.this, message, Toast.LENGTH_SHORT).show();
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
        String result = "";

        Geocoder geocoder = new Geocoder(SecondActivity.this, Locale.getDefault());
        try
        {
            List<Address> adresseList = geocoder.getFromLocation(latitude,longitude,1);
            // concatenation
            result = adress.concat(adresseList.get(0).getAddressLine(0));

        }catch(IOException e )
        {
            Toast.makeText(SecondActivity.this, "erreur localication", Toast.LENGTH_LONG).show();
        }
        return  result;
    }

    private void flipperImage(int image){
        ImageView imageView = new ImageView(this);
        imageView.setBackgroundResource(image);

        viewFlipper.addView(imageView);
        viewFlipper.setFlipInterval(4000);
        viewFlipper.setAutoStart(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
}
