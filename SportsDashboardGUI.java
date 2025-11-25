package org.example;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class SportsDashboardGUI extends JFrame {

    private JPanel outputPanel;

    public SportsDashboardGUI() {
        setTitle("Sports Dashboard");
        setSize(1200, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Top panel with initial dashboard button
        JPanel topPanel = new JPanel();
        JButton dashboardButton = new JButton("SportsDashboard");
        dashboardButton.setFont(new Font("Segoe UI", Font.BOLD, 24));
        dashboardButton.setPreferredSize(new Dimension(300, 80));
        topPanel.add(dashboardButton);
        add(topPanel, BorderLayout.NORTH);

        // Panel with buttons for each feature, initially hidden
        JPanel buttonPanel = new JPanel(new GridLayout(0, 2, 20, 20));
        buttonPanel.setVisible(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(buttonPanel, BorderLayout.WEST);

        // Output panel
        outputPanel = new JPanel(new BorderLayout());
        add(outputPanel, BorderLayout.CENTER);

        // Show buttons when dashboard clicked
        dashboardButton.addActionListener(e -> buttonPanel.setVisible(true));

        // Helper to add feature buttons
        addFeatureButton(buttonPanel, "Top 10 Scorers", () -> {
            TopScorers ts = new TopScorers();
            JTable table = createTable(ts.getTopScorers(10), "Player", "Goals");
            showOutput(new JScrollPane(table));
        });

        addFeatureButton(buttonPanel, "Total Goals", () -> {
            TotalGoals tg = new TotalGoals();
            JTable table = createTable(tg.getTotalGoals(), "Player", "Goals");
            showOutput(new JScrollPane(table));
        });

        addFeatureButton(buttonPanel, "Player Averages", () -> {
            PlayerAverages pa = new PlayerAverages();
            Map<String, Double> averages = pa.getPlayerAveragesMap();
            JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
            panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            panel.add(new JLabel("Average Goals:"));
            panel.add(new JLabel(String.valueOf(averages.get("avg_goals"))));
            panel.add(new JLabel("Average Assists:"));
            panel.add(new JLabel(String.valueOf(averages.get("avg_assists"))));
            panel.add(new JLabel("Average Fouls:"));
            panel.add(new JLabel(String.valueOf(averages.get("avg_fouls"))));
            showOutput(panel);
        });

        addFeatureButton(buttonPanel, "Team Win Rates", () -> {
            TeamWinRates twr = new TeamWinRates();
            JTable table = createTable(twr.getTeamWinRates(), "Team", "Win Rate (%)");
            showOutput(new JScrollPane(table));
        });

        addFeatureButton(buttonPanel, "Home Team Goals", () -> {
            HomeTeamGoals hg = new HomeTeamGoals();
            JTable table = createTable(hg.printHomeTeamGoals(), "Team", "Home Goals");
            showOutput(new JScrollPane(table));
        });

        addFeatureButton(buttonPanel, "Away Team Goals", () -> {
            AwayTeamGoals ag = new AwayTeamGoals();
            JTable table = createTable(ag.printAwayTeamGoals(), "Team", "Away Goals");
            showOutput(new JScrollPane(table));
        });

        addFeatureButton(buttonPanel, "Home vs Away Goals Chart", () -> {
            HomeAndAwayGoalsChart chart = new HomeAndAwayGoalsChart();
            showOutput(chart.getChartPanel());
        });

        addFeatureButton(buttonPanel, "Top Scorers Chart", () -> {
            TopScorersChart chart = new TopScorersChart();
            showOutput(chart.getChartPanel());
        });

        addFeatureButton(buttonPanel, "Team Goals Chart", () -> {
            TeamGoalsChart chart = new TeamGoalsChart();
            showOutput(chart.getChartPanel());
        });

        addFeatureButton(buttonPanel, "Player Contributions Heatmap", () -> {
            PlayerContributionsHeatmap chart = new PlayerContributionsHeatmap();
            showOutput(chart.getChartPanel());
        });

        // Center the buttons panel
        JPanel wrapperPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        wrapperPanel.add(buttonPanel);
        add(wrapperPanel, BorderLayout.WEST);
    }

    private void addFeatureButton(JPanel panel, String title, Runnable action) {
        JButton button = new JButton(title);
        button.setPreferredSize(new Dimension(200, 60));
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.addActionListener(e -> action.run());
        panel.add(button);
    }

    private void showOutput(Component comp) {
        outputPanel.removeAll();
        outputPanel.add(comp, BorderLayout.CENTER);
        outputPanel.revalidate();
        outputPanel.repaint();
    }

    private JTable createTable(Map<String, ?> data, String col1, String col2) {
        String[][] tableData = new String[data.size()][2];
        int i = 0;
        for (var entry : data.entrySet()) {
            tableData[i][0] = entry.getKey();
            tableData[i][1] = entry.getValue().toString();
            i++;
        }
        return new JTable(tableData, new String[]{col1, col2});
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SportsDashboardGUI gui = new SportsDashboardGUI();
            gui.setVisible(true);
        });
    }
}





