// Computes the total goals scored by each player.
package org.example;

import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class TotalGoals {

    public Map<String, Integer> getTotalGoals() {
        Map<String, Integer> playerGoals = new LinkedHashMap<>();

        //sql query
        String sql = """
            SELECT CONCAT(p.first_name, ' ', p.last_name) AS player_name,
                   COALESCE(SUM(ps.goals), 0) AS total_goals
            FROM players p
            LEFT JOIN player_stats ps ON p.player_id = ps.player_id
            GROUP BY p.player_id, player_name
            ORDER BY total_goals DESC;
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                String name = rs.getString("player_name");
                int goals = rs.getInt("total_goals");
                playerGoals.put(name, goals);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return playerGoals;
    }
    public void printTotalGoals() {
        Map<String, Integer> playerGoals = getTotalGoals();
        System.out.println("\n TOTAL GOALS SCORED BY EACH PLAYER");
        playerGoals.forEach((player, goals) ->
                System.out.printf("%-25s %3d goals%n", player, goals));
    }

    public static void main(String[] args) {
        TotalGoals tg = new TotalGoals();
        tg.printTotalGoals();
    }
}

