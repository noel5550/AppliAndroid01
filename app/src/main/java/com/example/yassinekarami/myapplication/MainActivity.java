package com.example.yassinekarami.myapplication;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    TextView textView;
    SmsManager smsManager;
    String message ="bonjour";

    private final String numero = "0658406185";
    //private final String numero = "0650664099";
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;
    private static final int MY_PERMISSIONS_REQUEST_GPS_LOCATION = 1;
    private static final int MY_PERMISSIONS_REQUEST_INTERNET = 1;


    LocationManager locationManager;
    double myLatitude;
    double myLongitude;
    String adress = "";
    LocationListener locationListener;
    boolean sendFlag = false;

    ConnectivityManager connManager;
    NetworkReceiver netReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView)findViewById(R.id.text_view);
        smsManager = SmsManager.getDefault();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        connManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        netReceiver = new NetworkReceiver(); // network event

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(netReceiver,filter);

        textView.setText(numero);


        // permission pour envoyé des message
        if (!checkPermission(Manifest.permission.SEND_SMS))
        {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.SEND_SMS},
                    MY_PERMISSIONS_REQUEST_SEND_SMS);
        }

        // permission pour accéder au GPS
        if (!checkPermission(Manifest.permission.ACCESS_FINE_LOCATION))
        {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_GPS_LOCATION);
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
                    textView.append(adress);
                    if (!sendFlag)
                    {
                        smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(numero, null, message, null, null);
                        sendFlag = true;
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

            locationManager.requestLocationUpdates("gps", 5000, 0, locationListener);
        }


    }

    public void SendMessageClick(View view){

        // on récupère le message a envoyé

        smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(numero, null, message, null, null);
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
        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
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

    // class permettant d'utiliser l'evenement onReceive
    public class NetworkReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
            if (networkInfo != null)
            {
                boolean wifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();
                boolean mobileData = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected();
                if (wifi)
                {
                    Toast.makeText(context,"wifi",Toast.LENGTH_SHORT).show();
                }
                else if (mobileData)
                {
                    Toast.makeText(context,"4g",Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                Toast.makeText(context,"no",Toast.LENGTH_SHORT).show();

            }




        }
    }
}
