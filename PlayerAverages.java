// Computes player averages per match for goals, assists and fouls.
package org.example;

import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class PlayerAverages {

    public Map<String, Double> getPlayerAveragesMap() {
        Map<String, Double> averages = new LinkedHashMap<>();

        String sql = """
            SELECT ROUND(AVG(goals), 2) AS avg_goals,
                   ROUND(AVG(assists), 2) AS avg_assists,
                   ROUND(AVG(fouls), 2) AS avg_fouls
            FROM player_stats;
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            if (rs.next()) {
                averages.put("avg_goals", rs.getDouble("avg_goals"));
                averages.put("avg_assists", rs.getDouble("avg_assists"));
                averages.put("avg_fouls", rs.getDouble("avg_fouls"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return averages;
    }

    // You can keep the old main method for testing
    public static void main(String[] args) {
        PlayerAverages pa = new PlayerAverages();
        System.out.println(pa.getPlayerAveragesMap());
    }
}



