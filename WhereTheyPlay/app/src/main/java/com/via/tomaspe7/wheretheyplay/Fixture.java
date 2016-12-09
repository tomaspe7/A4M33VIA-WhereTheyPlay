package com.via.tomaspe7.wheretheyplay;

public class Fixture {
    public static final String IN_PLAY_STATUS = "IN_PLAY";
    public static final String FINISHED_STATUS = "FINISHED";

    private String fixtureStatus;
    private String fixtureDate;
    private String homeTeamName;
    private String awayTeamName;
    private String fullTimeGoalsHomeTeam;
    private String fullTimeGoalsAwayTeam;
    private String halfTimeGoalsHomeTeam;
    private String halfTimeGoalsAwayTeam;

    public Fixture(String fixtureStatus, String fixtureDate, String homeTeamName, String awayTeamName,
                   String fullTimeGoalsHomeTeam, String fullTimeGoalsAwayTeam, String halfTimeGoalsHomeTeam,
                   String halfTimeGoalsAwayTeam) {
        this.fixtureStatus = fixtureStatus;
        this.fixtureDate = fixtureDate;
        this.homeTeamName = homeTeamName;
        this.awayTeamName = awayTeamName;
        this.fullTimeGoalsHomeTeam = fullTimeGoalsHomeTeam;
        this.fullTimeGoalsAwayTeam = fullTimeGoalsAwayTeam;
        this.halfTimeGoalsHomeTeam = halfTimeGoalsHomeTeam;
        this.halfTimeGoalsAwayTeam = halfTimeGoalsAwayTeam;
    }
}
