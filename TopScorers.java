// Retrieves the top 10 scorers overall by computing the total goals scored by each player.
// The top scorer overall scored 52 goals.
package org.example;

import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class TopScorers {
    public Map<String, Integer> getTopScorers(int limit) {
        Map<String, Integer> scorers = new LinkedHashMap<>();

        //sql query
        String sql = """
            SELECT CONCAT(p.first_name, ' ', p.last_name) AS player_name,
                   COALESCE(SUM(ps.goals), 0) AS total_goals
            FROM players p
            LEFT JOIN player_stats ps ON p.player_id = ps.player_id
            GROUP BY p.player_id, player_name
            ORDER BY total_goals DESC
            LIMIT ?;
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, limit);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                String name = rs.getString("player_name");
                int goals = rs.getInt("total_goals");
                scorers.put(name, goals);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return scorers;
    }
    public void printTopScorers(int limit) {
        Map<String, Integer> scorers = getTopScorers(limit);

        System.out.println("\n TOP " + limit + " GOAL SCORERS");
        scorers.forEach((player, goals) ->
                System.out.printf("%-25s %3d goals%n", player, goals));
    }
    public static void main(String[] args) {
        TopScorers ts = new TopScorers();
        ts.printTopScorers(10);
    }
}



