package com.example.yassinekarami.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    Timer timer;

    Button btnExit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnExit = (Button)findViewById(R.id.btnExit);


        // on instancie la classe timer
        timer = new Timer();
        //on utilise la fonction schedule qui permet de changer d'activité aprés un nombre de secondes
        timer.schedule(new TimerTask() { // la classe timerTask permet de faire des actions, evenement aprés un certain temps
            @Override
            public void run() {
                // on fait la transition d'activités, la classe intent permet de faire ce changement
                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                // on commence l'activité de changement d'activité
                startActivity(intent);
                finish();

            }
        },5000);
    }


    public void exitClick(View view) {

        timer.cancel();
        finish();
        moveTaskToBack(true);
    }
}
