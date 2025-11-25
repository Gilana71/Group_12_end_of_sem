// Creates a line chart showing the total number of team goals scored over the season.
// Team B had the most goals scored over the whole season.
package org.example;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class TeamGoalsChart extends JPanel {

    private ChartPanel chartPanel;

    public TeamGoalsChart() {
        setLayout(new BorderLayout());

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        String sql = """
            SELECT m.date, p.team, SUM(ps.goals) AS goals
            FROM matches m
            JOIN player_stats ps ON m.match_id = ps.match_id
            JOIN players p ON ps.player_id = p.player_id
            GROUP BY m.date, p.team
            ORDER BY m.date;
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                dataset.addValue(rs.getInt("goals"), rs.getString("team"), rs.getString("date"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        JFreeChart chart = ChartFactory.createLineChart(
                "Team Goals Over the Season",
                "Match Date",
                "Goals Scored",
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
            JFrame frame = new JFrame("Team Goals Over Season");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            frame.add(new TeamGoalsChart());
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}


