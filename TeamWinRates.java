// Computes the win rates of each team.
// Team B had the highest percentage win rate while Team C had the lowest.
package org.example;

import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class TeamWinRates {

    public Map<String, Double> getTeamWinRates() {
        Map<String, Double> winRates = new LinkedHashMap<>();

        //sql query
        String sql = """
            WITH match_scores AS (
                SELECT m.match_id, m.home_team, m.away_team,
                       COALESCE(SUM(CASE WHEN p.team = m.home_team THEN ps.goals ELSE 0 END), 0) AS home_goals,
                       COALESCE(SUM(CASE WHEN p.team = m.away_team THEN ps.goals ELSE 0 END), 0) AS away_goals
                FROM matches m
                LEFT JOIN player_stats ps ON m.match_id = ps.match_id
                LEFT JOIN players p ON ps.player_id = p.player_id
                GROUP BY m.match_id, m.home_team, m.away_team
            )
            SELECT team,
                   ROUND(SUM(CASE WHEN team = winner THEN 1 ELSE 0 END) / COUNT(*) * 100, 2) AS win_rate
            FROM (
                SELECT home_team AS team,
                       CASE WHEN home_goals > away_goals THEN home_team
                            WHEN away_goals > home_goals THEN away_team END AS winner
                FROM match_scores
                UNION ALL
                SELECT away_team AS team,
                       CASE WHEN away_goals > home_goals THEN away_team
                            WHEN home_goals > away_goals THEN home_team END AS winner
                FROM match_scores
            ) t
            WHERE winner IS NOT NULL
            GROUP BY team
            ORDER BY win_rate DESC;
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                winRates.put(rs.getString("team"), rs.getDouble("win_rate"));
            }

        } catch (SQLException e) {
            System.out.println("❌ Error calculating team win rates:");
            e.printStackTrace();
        }
        return winRates;
    }

    public void printWinRates() {
        Map<String, Double> results = getTeamWinRates();

        System.out.println("\n TEAM WIN RATES");
        System.out.printf("%-20s %s%n", "Team", "Win Rate (%)");

        results.forEach((team, rate) ->
                System.out.printf("%-20s %.2f%%%n", team, rate));
    }

    public static void main(String[] args) {
        TeamWinRates twr = new TeamWinRates();
        twr.printWinRates();
    }
}





