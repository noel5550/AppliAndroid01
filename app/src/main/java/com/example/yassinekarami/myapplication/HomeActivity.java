package com.example.yassinekarami.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;
import android.widget.Button;

import java.util.Timer;

public class HomeActivity extends AppCompatActivity {


    Button btnPompier;
    Button btnPolice;
    Button btnAmbulance;

    String choixUtilisateur;
    Timer timer;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        setContentView(R.layout.activity_home);

        btnPompier = (Button)findViewById(R.id.btnPompier);
        btnPolice = (Button)findViewById(R.id.btnPolice);
        btnAmbulance = (Button) findViewById(R.id.btnAmbulance);


    }

    public void messagePompier(View view) {
        choixUtilisateur = "Pompier";
        startSecondActivity();
    }

    public void messageAccident(View view) {
        choixUtilisateur = "Pompier et police";
        startSecondActivity();
    }

    public void messagePolice(View view) {
        choixUtilisateur = "Police";
        startSecondActivity();
    }

    public void messageAmbulance(View view) {
        choixUtilisateur = "Ambulance";
        startSecondActivity();
    }

    public void messageOtage(View view) {
        choixUtilisateur = "Prise d'otage";
    }

    public void quitter(View view) {
        finish();
    }

    private void startSecondActivity(){
        // on fait la transition d'activités, la classe intent permet de faire ce changement
        Intent intent = new Intent(HomeActivity.this, SecondActivity.class);
        // envoie de la variable message vers les autres activité
        intent.putExtra("choixUtilisateur", choixUtilisateur);
        // on commence l'activité de changement d'activité
        startActivity(intent);
    }


    public void startPopupActivity(View view) {

        // on fait la transition d'activités, la classe intent permet de faire ce changement
        Intent intent = new Intent(HomeActivity.this, PopupActivity.class);
        // on commence l'activité de changement d'activité
        startActivity(intent);


    }


}
