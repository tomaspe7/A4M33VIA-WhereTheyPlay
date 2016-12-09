package com.via.tomaspe7.wheretheyplay;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class FixturesActivity extends AppCompatActivity {

    public final int PRIMERA_DIVISION = 436;

    private List<Fixture> fixtures;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fixtures);


        readFromServer();
    }

    public void readFromServer() {
        try {
            DataRetriever dataRetriever = new DataRetriever();
            dataRetriever.execute("http://api.football-data.org/v1/competitions/" + PRIMERA_DIVISION, "minified");
            JSONObject json = dataRetriever.get();
            int currentMatchday = (Integer) json.get("currentMatchday");

            dataRetriever = new DataRetriever();
            dataRetriever.execute("http://api.football-data.org/v1/competitions/" + PRIMERA_DIVISION + "/fixtures?matchday=" + currentMatchday, "minified");
            json = dataRetriever.get();
            JSONArray fixturesJSON = json.getJSONArray("fixtures");

            fixtures.clear();
            for (int i = 0; i < fixturesJSON.length(); i++) {
                JSONObject fixtureJSON = (JSONObject) fixturesJSON.get(i);
                JSONObject result = fixtureJSON.getJSONObject("result");
                String fixtureStatus = String.valueOf(fixtureJSON.get("status"));

                String fixtureDate = String.valueOf(fixtureJSON.get("date"));
                String homeTeamName = String.valueOf(fixtureJSON.get("homeTeamName"));
                String awayTeamName = String.valueOf(fixtureJSON.get("awayTeamName"));
                String fullTimeGoalsHomeTeam = "-";
                String fullTimeGoalsAwayTeam = "-";
                String halfTimeGoalsHomeTeam = "-";
                String halfTimeGoalsAwayTeam = "-";

                if (fixtureStatus.equals(Fixture.IN_PLAY_STATUS) || fixtureStatus.equals(Fixture.FINISHED_STATUS)) {
                    JSONObject halfTimeResult = result.getJSONObject("halfTime");

                    fullTimeGoalsHomeTeam = String.valueOf(result.get("goalsHomeTeam"));
                    fullTimeGoalsAwayTeam = String.valueOf(result.get("goalsAwayTeam"));
                    halfTimeGoalsHomeTeam = String.valueOf(halfTimeResult.get("goalsHomeTeam"));
                    halfTimeGoalsAwayTeam = String.valueOf(halfTimeResult.get("goalsAwayTeam"));
                }

                Fixture fixture = new Fixture(fixtureStatus, fixtureDate, homeTeamName, awayTeamName,
                        fullTimeGoalsHomeTeam, fullTimeGoalsAwayTeam, halfTimeGoalsHomeTeam, halfTimeGoalsAwayTeam);
                fixtures.add(fixture);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class DataRetriever extends AsyncTask<String, Integer, JSONObject> {
        private ProgressDialog progressDialog = new ProgressDialog(FixturesActivity.this);

        protected void onPreExecute() {
            progressDialog.setMessage("Downloading your data...");
            progressDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            String address = args[0];
            String XResponseControl = args[1];

            try {
                URL url = new URL(address);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("X-Auth-Token", "940c8b0476bf4c4ba2f232512ff5925f");
                connection.setRequestProperty("X-Response-Control", XResponseControl);

                InputStream is = connection.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
                String jsonText = readAll(rd);

                is.close();
                return new JSONObject(jsonText);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(JSONObject json) {
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
