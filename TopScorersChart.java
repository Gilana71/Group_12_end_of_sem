// Creates a bar chart showing the top 10 scorers overall
// The top scorer is Ruth Ouma with 52 goals.
package org.example;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class TopScorersChart extends JPanel {

    private ChartPanel chartPanel;

    public TopScorersChart() {
        setLayout(new BorderLayout());

        TopScorers ts = new TopScorers();
        Map<String, Integer> scorers = ts.getTopScorers(10);

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        scorers.forEach((player, goals) -> dataset.addValue(goals, "Goals", player));

        JFreeChart chart = ChartFactory.createBarChart(
                "Top 10 Goal Scorers",
                "Player",
                "Goals",
                dataset,
                PlotOrientation.VERTICAL,
                false, true, false
        );

        chartPanel = new ChartPanel(chart);
        add(chartPanel, BorderLayout.CENTER);
    }

    public ChartPanel getChartPanel() {
        return chartPanel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Top 10 Goal Scorers");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            frame.add(new TopScorersChart());
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}



