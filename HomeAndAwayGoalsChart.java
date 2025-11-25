// Comparative bar chart to show the total number of goals scored by each team as home team vs as an away team.
// Team C performed significantly better as an away team than a home team.
package org.example;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class HomeAndAwayGoalsChart extends JPanel {

    private ChartPanel chartPanel;

    public HomeAndAwayGoalsChart() {
        setLayout(new BorderLayout());

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        String sql = """
            WITH home_goals AS (
                SELECT m.home_team AS team,
                       COALESCE(SUM(ps.goals), 0) AS total_home_goals
                FROM matches m
                JOIN player_stats ps ON m.match_id = ps.match_id
                JOIN players p ON ps.player_id = p.player_id
                WHERE p.team = m.home_team
                GROUP BY m.home_team
            ),
            away_goals AS (
                SELECT m.away_team AS team,
                       COALESCE(SUM(ps.goals), 0) AS total_away_goals
                FROM matches m
                JOIN player_stats ps ON m.match_id = ps.match_id
                JOIN players p ON ps.player_id = p.player_id
                WHERE p.team = m.away_team
                GROUP BY m.away_team
            )
            SELECT h.team,
                   h.total_home_goals,
                   COALESCE(a.total_away_goals, 0) AS total_away_goals
            FROM home_goals h
            LEFT JOIN away_goals a ON h.team = a.team

            UNION

            SELECT a.team,
                   COALESCE(h.total_home_goals, 0) AS total_home_goals,
                   a.total_away_goals
            FROM away_goals a
            LEFT JOIN home_goals h ON a.team = h.team

            ORDER BY team;
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                String team = rs.getString("team");
                int homeGoals = rs.getInt("total_home_goals");
                int awayGoals = rs.getInt("total_away_goals");
                dataset.addValue(homeGoals, "Home Goals", team);
                dataset.addValue(awayGoals, "Away Goals", team);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Home vs Away Goals",
                "Team",
                "Goals",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false
        );

        chartPanel = new ChartPanel(chart);
        add(chartPanel, BorderLayout.CENTER);
    }

    public ChartPanel getChartPanel() {
        return chartPanel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Home vs Away Goals");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(900, 600);
            frame.add(new HomeAndAwayGoalsChart());
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}




