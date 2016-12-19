package com.via.tomaspe7.wheretheyplay;

import android.app.ProgressDialog;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    public final static String TEAM_TAG = "TEAM";
    public final static String IP_TAG = "IP";

    private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;

        try {
            StadiumRetriever stadiumRetriever = new StadiumRetriever();
            String ip = getIntent().getStringExtra(IP_TAG);
            String homeTeamName = getIntent().getStringExtra(TEAM_TAG);
            stadiumRetriever.execute(ip, homeTeamName);
            String homeTeamStadiumName = stadiumRetriever.get();

            if (homeTeamStadiumName == null){
                throw new ExecutionException("Obtained null as homeTeamStadiumName", null);
            }

            Geocoder geocoder = new Geocoder(this);
            List<Address> addresses = geocoder.getFromLocationName(homeTeamStadiumName, 1);
            LatLng stadiumPosition = new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());

            this.map.moveCamera(CameraUpdateFactory.newLatLngZoom(stadiumPosition, 15));
            this.map.addMarker(new MarkerOptions()
                    .title(homeTeamStadiumName)
                    .snippet("Stadium of " + homeTeamName)
                    .position(stadiumPosition));
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            Toast.makeText(this, "Unable to connect to StadiumAPI.", Toast.LENGTH_LONG).show();
            finish();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Unable to connect to GoogleMaps.", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    class StadiumRetriever extends AsyncTask<String, Integer, String> {
        private ProgressDialog progressDialog = new ProgressDialog(MapsActivity.this);

        protected void onPreExecute() {
            progressDialog.setMessage("Getting home team stadium ...");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            try {
                String address = "http://" + args[0] + ":8088/stadium?club=" + URLEncoder.encode(args[1], "UTF-8");

                URL url = new URL(address);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                InputStream is = connection.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
                String homeTeamStadium = readAll(rd);

                is.close();
                return homeTeamStadium;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String homeTeamStadium) {
            progressDialog.dismiss();
        }

        private String readAll(Reader rd) throws IOException {
            StringBuilder sb = new StringBuilder();
            int cp;
            while ((cp = rd.read()) != -1) {
                sb.append((char) cp);
            }
            return sb.toString();
        }
    }
}
