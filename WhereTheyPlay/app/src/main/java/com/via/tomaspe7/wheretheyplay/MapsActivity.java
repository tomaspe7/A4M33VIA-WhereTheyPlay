package com.via.tomaspe7.wheretheyplay;

import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    public final static String TEAM_TAG = "TEAM";

    private GoogleMap map;
    private HashMap<String, String> stadiumNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        stadiumNames = new HashMap<>();
        stadiumNames.put("Deportivo Alavés", "Estadio de Mendizorroza");
        stadiumNames.put("Athletic Club", "Estadio San Mamés");
        stadiumNames.put("Club Atlético de Madrid", "Vicente Calderón");
        stadiumNames.put("FC Barcelona", "Camp Nou");
        stadiumNames.put("Real Betis", "Estadio Benito Villamarín");
        stadiumNames.put("RC Celta de Vigo", "Estadio de Balaídos");
        stadiumNames.put("RC Deportivo La Coruna", "Estadio Municipal de Riazor");
        stadiumNames.put("SD Eibar", "Ipurua");
        stadiumNames.put("RCD Espanyol", "Cornellà-El Prat");
        stadiumNames.put("Granada CF", "Estadio Nuevo Los Cármenes");
        stadiumNames.put("UD Las Palmas", "Estadio Gran Canaria");
        stadiumNames.put("CD Leganés", "Estadio Municipal de Butarque");
        stadiumNames.put("Málaga CF", "Estadio La Rosaleda");
        stadiumNames.put("CA Osasuna", "Estadio El Sadar");
        stadiumNames.put("Real Madrid CF", "Santiago Bernabéu");
        stadiumNames.put("Real Sociedad de Fútbol", "Estadio Anoeta");
        stadiumNames.put("Sevilla FC", "Estadio Ramón Sánchez-Pizjuán");
        stadiumNames.put("Sporting Gijón", "Estadio El Molinón");
        stadiumNames.put("Valencia CF", "Estadio de Mestalla");
        stadiumNames.put("Villarreal CF", "Estadio El Madrigal");
    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;

        try {
            String homeTeamName = getIntent().getStringExtra(TEAM_TAG);
            String homeTeamStadiumName = stadiumNames.get(homeTeamName);    //TODO create own API providing stadium names

            Geocoder geocoder = new Geocoder(this);
            List<Address> addresses = geocoder.getFromLocationName(homeTeamStadiumName, 1);
            LatLng stadiumPosition = new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());

            this.map.moveCamera(CameraUpdateFactory.newLatLngZoom(stadiumPosition, 13));
            this.map.addMarker(new MarkerOptions()
                    .title(homeTeamStadiumName)
                    .snippet("Stadium of " + homeTeamName)
                    .position(stadiumPosition));
        } catch (IOException e){
            e.printStackTrace();
            Toast.makeText(this, "Unable to connect GoogleMaps.", Toast.LENGTH_LONG).show();
            finish();
        }
    }
}
