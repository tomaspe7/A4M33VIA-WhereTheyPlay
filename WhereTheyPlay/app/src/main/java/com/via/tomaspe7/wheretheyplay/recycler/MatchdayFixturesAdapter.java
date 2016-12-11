package com.via.tomaspe7.wheretheyplay.recycler;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.via.tomaspe7.wheretheyplay.R;
import com.via.tomaspe7.wheretheyplay.model.Fixture;

import java.util.List;

public class MatchdayFixturesAdapter extends RecyclerView.Adapter<MatchdayFixturesAdapter.FixturesViewHolder> {

    private LayoutInflater inflater;
    private List<Fixture> fixtures;
    private Context context;

    public MatchdayFixturesAdapter(Context context, List<Fixture> fixtures) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.fixtures = fixtures;
    }

    @Override
    public FixturesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_fixture, parent, false);
        FixturesViewHolder viewHolder = new FixturesViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(FixturesViewHolder holder, int position) {
        Fixture fixture = fixtures.get(position);

        holder.homeTeam.setText(fixture.getHomeTeamName());
        holder.awayTeam.setText(fixture.getAwayTeamName());
        holder.fixtureDate.setText(fixture.getFixtureDate());
        holder.homeTeamScore.setText(fixture.getFullTimeGoalsHomeTeam());
        holder.awayTeamScore.setText(fixture.getFullTimeGoalsAwayTeam());

        if (fixture.getFixtureStatus().equals(Fixture.IN_PLAY_STATUS)){
            holder.homeTeamScore.setTextColor(ContextCompat.getColor(context, R.color.in_play_fixture));
            holder.awayTeamScore.setTextColor(ContextCompat.getColor(context, R.color.in_play_fixture));
        }

        if (fixture.getFixtureStatus().equals(Fixture.FINISHED_STATUS)){
            holder.homeTeamScore.setTextColor(ContextCompat.getColor(context, R.color.finished_fixture));
            holder.awayTeamScore.setTextColor(ContextCompat.getColor(context, R.color.finished_fixture));
        }
    }

    @Override
    public int getItemCount() {
        return fixtures.size();
    }

    public Fixture getItem(int position) {
        if (fixtures == null || fixtures.get(position) == null) {
            return null;
        }
        return fixtures.get(position);
    }

    public static class FixturesViewHolder extends RecyclerView.ViewHolder {

        private TextView homeTeam;
        private TextView awayTeam;
        private TextView fixtureDate;
        private TextView homeTeamScore;
        private TextView awayTeamScore;

        public FixturesViewHolder(View itemView) {
            super(itemView);

            homeTeam = (TextView) itemView.findViewById(R.id.item_fixture_home_team);
            awayTeam = (TextView) itemView.findViewById(R.id.item_fixture_away_team);
            fixtureDate = (TextView) itemView.findViewById(R.id.item_fixture_date);
            homeTeamScore = (TextView) itemView.findViewById(R.id.item_fixture_home_team_goals);
            awayTeamScore = (TextView) itemView.findViewById(R.id.item_fixture_away_team_goals);
        }
    }
}


