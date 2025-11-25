// Computes the total goals scored by each team when they were playing as a home team.
// Team B had the highest number of home team goals scored while Team C had the lowest.
package org.example;

import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class HomeTeamGoals {
    public Map<String, Integer> printHomeTeamGoals() {
        Map<String, Integer> homeGoals = new LinkedHashMap<>();

        //sql query
        String sql = """
            SELECT m.home_team AS team,
                   COALESCE(SUM(ps.goals), 0) AS total_home_goals
            FROM matches m
            JOIN player_stats ps ON m.match_id = ps.match_id
            JOIN players p ON ps.player_id = p.player_id
            WHERE p.team = m.home_team
            GROUP BY m.home_team
            ORDER BY total_home_goals DESC;
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                String team = rs.getString("team");
                int goals = rs.getInt("total_home_goals");
                homeGoals.put(team, goals);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return homeGoals;
    }
    public void printHomeGoals() {
        Map<String, Integer> results = printHomeTeamGoals();

        System.out.println("\n TOTAL GOALS SCORED BY A HOME TEAM ");
        System.out.printf("%-20s %-10s%n", "Team", "Home Goals");

        results.forEach((team, goals) ->
                System.out.printf("%-20s %-10d%n", team, goals));
    }

    public static void main(String[] args) {
        HomeTeamGoals hg = new HomeTeamGoals();
        hg.printHomeGoals();
    }
}

