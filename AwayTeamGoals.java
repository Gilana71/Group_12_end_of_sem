// Computes the total goals scored by each team when they were playing as an away team.
// Team C had the highest number of away team goals scored while Team D had the lowest
package org.example;

import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class AwayTeamGoals {
    public Map<String, Integer> printAwayTeamGoals() {
        Map<String, Integer> awayGoals = new LinkedHashMap<>();

        //sql query
        String sql = """
            SELECT m.away_team AS team,
                   COALESCE(SUM(ps.goals), 0) AS total_away_goals
            FROM matches m
            JOIN player_stats ps ON m.match_id = ps.match_id
            JOIN players p ON ps.player_id = p.player_id
            WHERE p.team = m.away_team
            GROUP BY m.away_team
            ORDER BY total_away_goals DESC;
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                String team = rs.getString("team");
                int goals = rs.getInt("total_away_goals");
                awayGoals.put(team, goals);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return awayGoals;
    }
    public void printAwayGoals() {
        Map<String, Integer> results = printAwayTeamGoals();

        System.out.println("\n TOTAL GOALS SCORED BY AN AWAY TEAM");
        System.out.printf("%-20s %-10s%n", "Team", "Away Goals");

        results.forEach((team, goals) ->
                System.out.printf("%-20s %-10d%n", team, goals));
    }

    public static void main(String[] args) {
        AwayTeamGoals ag = new AwayTeamGoals();
        ag.printAwayGoals();
    }
}

