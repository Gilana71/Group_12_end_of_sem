// Creates a heatmap showing player contributions over the season(goals, assists and fouls).
package org.example;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.SymbolAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.LookupPaintScale;
import org.jfree.chart.renderer.xy.XYBlockRenderer;
import org.jfree.chart.title.PaintScaleLegend;
import org.jfree.chart.ui.RectangleAnchor;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.data.xy.DefaultXYZDataset;
import org.jfree.data.xy.XYZDataset;
import org.jfree.chart.axis.AxisLocation;

import javax.swing.*;
import java.awt.Color;
import java.awt.Font;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List; // explicitly import java.util.List

public class PlayerContributionsHeatmap extends JPanel {

    private ChartPanel chartPanel;
    private String[] statsLabels;
    private String[] playerLabels;

    public PlayerContributionsHeatmap() {
        setLayout(new java.awt.BorderLayout());

        XYZDataset dataset = createDataset();
        JFreeChart chart = createHeatmap(dataset);

        chartPanel = new ChartPanel(chart);
        add(chartPanel, java.awt.BorderLayout.CENTER);
    }

    private XYZDataset createDataset() {
        DefaultXYZDataset dataset = new DefaultXYZDataset();

        List<String> stats = Arrays.asList("Goals", "Assists", "Fouls");
        List<String> players = new ArrayList<>();
        Map<String, double[]> playerStats = new LinkedHashMap<>();

        String sql = """
            SELECT CONCAT(p.first_name, ' ', p.last_name) AS player_name,
                   COALESCE(SUM(ps.goals), 0) AS goals,
                   COALESCE(SUM(ps.assists), 0) AS assists,
                   COALESCE(SUM(ps.fouls), 0) AS fouls
            FROM players p
            LEFT JOIN player_stats ps ON p.player_id = ps.player_id
            GROUP BY p.player_id, player_name
            ORDER BY goals DESC
            LIMIT 15;
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                String player = rs.getString("player_name");
                players.add(player);
                double goals = rs.getDouble("goals");
                double assists = rs.getDouble("assists");
                double fouls = rs.getDouble("fouls");
                playerStats.put(player, new double[]{goals, assists, fouls});
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        int n = players.size() * stats.size();
        double[] x = new double[n];
        double[] y = new double[n];
        double[] z = new double[n];
        int index = 0;
        for (int i = 0; i < players.size(); i++) {
            for (int j = 0; j < stats.size(); j++) {
                x[index] = j;
                y[index] = i;
                z[index] = playerStats.get(players.get(i))[j];
                index++;
            }
        }

        dataset.addSeries("Contributions", new double[][]{x, y, z});
        this.statsLabels = stats.toArray(new String[0]);
        this.playerLabels = players.toArray(new String[0]);

        return dataset;
    }

    private JFreeChart createHeatmap(XYZDataset dataset) {
        SymbolAxis xAxis = new SymbolAxis("Statistic", statsLabels);
        xAxis.setTickLabelFont(new Font("Segoe UI", Font.BOLD, 14));
        xAxis.setLowerMargin(0.0);
        xAxis.setUpperMargin(0.0);

        SymbolAxis yAxis = new SymbolAxis("Player", playerLabels);
        yAxis.setTickLabelFont(new Font("Segoe UI", Font.PLAIN, 13));
        yAxis.setLowerMargin(0.0);
        yAxis.setUpperMargin(0.0);
        yAxis.setVerticalTickLabels(true);

        LookupPaintScale paintScale = new LookupPaintScale(0, 20, Color.BLUE);
        paintScale.add(5, new Color(0, 180, 255));
        paintScale.add(10, new Color(0, 255, 150));
        paintScale.add(15, new Color(255, 230, 50));
        paintScale.add(20, new Color(255, 100, 0));

        XYBlockRenderer renderer = new XYBlockRenderer();
        renderer.setPaintScale(paintScale);
        renderer.setBlockWidth(1.0);
        renderer.setBlockHeight(1.0);
        renderer.setBlockAnchor(RectangleAnchor.BOTTOM_LEFT);

        XYPlot plot = new XYPlot(dataset, xAxis, yAxis, renderer);
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);
        plot.setDomainGridlinesVisible(false);
        plot.setRangeGridlinesVisible(false);
        plot.setAxisOffset(new RectangleInsets(5, 5, 5, 5));

        JFreeChart chart = new JFreeChart(
                "Player Contributions Heatmap (Goals, Assists, Fouls)",
                new Font("Segoe UI", Font.BOLD, 20),
                plot,
                false
        );

        chart.setBackgroundPaint(Color.WHITE);

        NumberAxis scaleAxis = new NumberAxis("Intensity");
        scaleAxis.setTickLabelFont(new Font("Segoe UI", Font.PLAIN, 12));
        PaintScaleLegend legend = new PaintScaleLegend(paintScale, scaleAxis);
        legend.setAxisLocation(AxisLocation.BOTTOM_OR_LEFT);
        legend.setPosition(RectangleEdge.RIGHT);
        legend.setMargin(5, 10, 40, 10);
        legend.setSubdivisionCount(5);
        chart.addSubtitle(legend);

        return chart;
    }

    public ChartPanel getChartPanel() {
        return chartPanel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Player Contributions Heatmap");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1100, 750);
            frame.add(new PlayerContributionsHeatmap());
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
