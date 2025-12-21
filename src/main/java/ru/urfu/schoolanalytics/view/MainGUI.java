package ru.urfu.schoolanalytics.view;

import ru.urfu.schoolanalytics.model.DatabaseManager;
import ru.urfu.schoolanalytics.model.School;
import ru.urfu.schoolanalytics.presenter.AnalyticsService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class MainGUI extends JFrame {

    private AnalyticsService service = new AnalyticsService();
    private JTextArea resultsArea = new JTextArea(20, 50);
    private JTable countyTable;
    private org.jfree.chart.ChartPanel chartPanel;

    public MainGUI() {
        setTitle("Аналитика школ Калифорнии");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel buttonPanel = new JPanel();
        JButton loadCsvBtn = new JButton("Загрузить CSV");
        JButton loadDbBtn = new JButton("Загрузить из БД");
        JButton showResultsBtn = new JButton("Показать результаты");

        buttonPanel.add(loadCsvBtn);
        buttonPanel.add(loadDbBtn);
        buttonPanel.add(showResultsBtn);

        resultsArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultsArea);

        countyTable = new JTable();
        JScrollPane tableScrollPane = new JScrollPane(countyTable);

        chartPanel = new org.jfree.chart.ChartPanel(null);
        chartPanel.setPreferredSize(new Dimension(800, 400));

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Результаты", scrollPane);
        tabbedPane.addTab("Топ округов", tableScrollPane);
        tabbedPane.addTab("Диаграмма", chartPanel);

        add(buttonPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);

        loadCsvBtn.addActionListener(e -> loadCsv());
        loadDbBtn.addActionListener(e -> loadFromDb());
        showResultsBtn.addActionListener(e -> showResults());
    }

    private void loadCsv() {
        try {
            service.connectToDatabase();
            service.loadCsvToDatabase();
            JOptionPane.showMessageDialog(this, "CSV загружен в БД");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ошибка: " + ex.getMessage());
        }
    }

    private void loadFromDb() {
        try {
            service.connectToDatabase();
            JOptionPane.showMessageDialog(this, "Подключено к БД");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Ошибка подключения: " + ex.getMessage());
        }
    }

    private void showResults() {
        try {
            List<DatabaseManager.CountyAvg> topCounties = service.getAverageStudentsByCounty(10);

            StringBuilder result = new StringBuilder();
            result.append("📈 Среднее число студентов по 10 округам:\n");
            for (var c : topCounties) {
                result.append(String.format("  %-25s → %.1f%n", c.county(), c.avgStudents()));
            }
            result.append("\n");

            Double avgExp = service.getAverageExpenditureInCounties("Fresno", "Contra Costa", "El Dorado", "Glenn");
            if (avgExp != null && !avgExp.isNaN()) {
                result.append(String.format("Средние расходы в округах: $%.2f%n", avgExp));
            } else {
                result.append("Нет данных по указанным округам.\n");
            }
            result.append("\n");

            School topSchool = service.getTopMathSchoolInStudentRanges();
            if (topSchool != null) {
                result.append("🎯 Лучшая школа по математике:\n");
                result.append(String.format("  Название: %s%n", topSchool.schoolName()));
                result.append(String.format("  Округ: %s%n", topSchool.county()));
                result.append(String.format("  Студентов: %d%n", topSchool.students()));
                result.append(String.format("  Математика: %.1f%n", topSchool.math()));
            } else {
                result.append("Нет школ в заданных диапазонах.\n");
            }

            resultsArea.setText(result.toString());

            DefaultTableModel model = new DefaultTableModel(new String[]{"Округ", "Среднее число студентов"}, 0);
            for (var c : topCounties) {
                model.addRow(new Object[]{c.county(), c.avgStudents()});
            }
            countyTable.setModel(model);

            chartPanel.setChart(ChartGenerator.generateAvgStudentsByCountyChart(service.getDatabase()).getChart());

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Ошибка запроса: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new MainGUI().setVisible(true);
        });
    }
}