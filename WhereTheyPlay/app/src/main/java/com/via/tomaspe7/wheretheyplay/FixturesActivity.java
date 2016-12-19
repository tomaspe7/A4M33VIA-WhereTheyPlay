package com.via.tomaspe7.wheretheyplay;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.via.tomaspe7.wheretheyplay.model.Fixture;
import com.via.tomaspe7.wheretheyplay.recycler.MatchdayFixturesAdapter;
import com.via.tomaspe7.wheretheyplay.recycler.OnClickListener;
import com.via.tomaspe7.wheretheyplay.recycler.decoration.DividerItemDecoration;

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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FixturesActivity extends AppCompatActivity {

    public final int PRIMERA_DIVISION = 436;

    private String ip = "";
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<Fixture> fixtures;
    private MatchdayFixturesAdapter fixturesAdapter;
    private RecyclerView recyclerFixtures;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fixtures);
        if (ip.equals("")) {
            setupServerIP();
        }

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                readFromServer();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        fixtures = new ArrayList<>();
        fixturesAdapter = new MatchdayFixturesAdapter(this, fixtures);
        recyclerFixtures = (RecyclerView) findViewById(R.id.recycle_matchday_fixtures);
        recyclerFixtures.setHasFixedSize(true);
        recyclerFixtures.setLayoutManager(new LinearLayoutManager(this));
        recyclerFixtures.addItemDecoration(new DividerItemDecoration(this, null, true, true));
        recyclerFixtures.setAdapter(fixturesAdapter);
        recyclerFixtures.addOnItemTouchListener(new OnClickListener.RecyclerViewOnItemClickListener(getApplicationContext(), recyclerFixtures, new OnClickListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                if (isOnline()) {
                    Fixture fixture = fixturesAdapter.getItem(position);

                    Intent intent = new Intent(FixturesActivity.this, MapsActivity.class);
                    intent.putExtra(MapsActivity.IP_TAG, ip);
                    intent.putExtra(MapsActivity.TEAM_TAG, fixture.getHomeTeamName());
                    startActivity(intent);
                } else {
                    Toast.makeText(FixturesActivity.this, "No internet connectivity.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        readFromServer();
    }

    public void readFromServer() {
        try {
            MatchesRetriever matchesRetriever = new MatchesRetriever();
            matchesRetriever.execute("http://api.football-data.org/v1/competitions/" + PRIMERA_DIVISION, "minified");
            JSONObject json = matchesRetriever.get();
            int currentMatchday = (Integer) json.get("currentMatchday");

            matchesRetriever = new MatchesRetriever();
            matchesRetriever.execute("http://api.football-data.org/v1/competitions/" + PRIMERA_DIVISION + "/fixtures?matchday=" + currentMatchday, "minified");
            json = matchesRetriever.get();
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
                    fullTimeGoalsHomeTeam = String.valueOf(result.get("goalsHomeTeam"));
                    fullTimeGoalsAwayTeam = String.valueOf(result.get("goalsAwayTeam"));

                    try {
                        JSONObject halfTimeResult = result.getJSONObject("halfTime");

                        halfTimeGoalsHomeTeam = String.valueOf(halfTimeResult.get("goalsHomeTeam"));
                        halfTimeGoalsAwayTeam = String.valueOf(halfTimeResult.get("goalsAwayTeam"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                Fixture fixture = new Fixture(fixtureStatus, fixtureDate, homeTeamName, awayTeamName,
                        fullTimeGoalsHomeTeam, fullTimeGoalsAwayTeam, halfTimeGoalsHomeTeam, halfTimeGoalsAwayTeam);
                fixtures.add(fixture);
            }

            Collections.sort(fixtures, new Comparator<Fixture>() {
                @Override
                public int compare(Fixture fixture, Fixture t1) {
                    return fixture.getFixtureDate().compareTo(t1.getFixtureDate());
                }
            });

            fixturesAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupServerIP() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Set StadiumAPI server IP address");

        //region IP filter
        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (end > start) {
                    String destTxt = dest.toString();
                    String resultingTxt = destTxt.substring(0, dstart) + source.subSequence(start, end) + destTxt.substring(dend);
                    if (!resultingTxt.matches("^\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3})?)?)?)?)?)?")) {
                        return "";
                    } else {
                        String[] splits = resultingTxt.split("\\.");
                        for (int i = 0; i < splits.length; i++) {
                            if (Integer.valueOf(splits[i]) > 255) {
                                return "";
                            }
                        }
                    }
                }
                return null;
            }
        };
        //endregion

        final EditText input = new EditText(this);
        input.setText(ip);
        input.setInputType(InputType.TYPE_CLASS_PHONE);
        input.setFilters(filters);
        builder.setCancelable(false);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ip = input.getText().toString();
            }
        });

        builder.show();
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    class MatchesRetriever extends AsyncTask<String, Integer, JSONObject> {
        private ProgressDialog progressDialog = new ProgressDialog(FixturesActivity.this);

        protected void onPreExecute() {
            progressDialog.setMessage("Updating data ...");
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
