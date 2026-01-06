package ru.urfu.schoolanalytics.view;

import ru.urfu.schoolanalytics.model.*;
import ru.urfu.schoolanalytics.presenter.AnalyticsPresenter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class MainGUI extends JFrame implements AnalyticsView {
    private AnalyticsPresenter presenter;

    private JTextArea resultsArea;
    private JTable countyTable;
    private JPanel chartPanel;
    private JProgressBar progressBar;
    private JButton loadCsvBtn;
    private JButton loadDbBtn;
    private JButton showResultsBtn;

    public MainGUI() {
        initializeUI();
        initializePresenter();
        setupEventHandlers();
    }

    private void initializeUI() {
        setTitle("Аналитика школ Калифорнии - MVP");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel buttonPanel = new JPanel();
        loadCsvBtn = new JButton("Загрузить CSV");
        loadDbBtn = new JButton("Подключиться к БД");
        showResultsBtn = new JButton("Показать результаты");

        progressBar = new JProgressBar();
        progressBar.setVisible(false);

        buttonPanel.add(loadCsvBtn);
        buttonPanel.add(loadDbBtn);
        buttonPanel.add(showResultsBtn);
        buttonPanel.add(progressBar);

        resultsArea = new JTextArea(20, 50);
        resultsArea.setEditable(false);
        resultsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(resultsArea);

        countyTable = new JTable();
        countyTable.setFont(new Font("Arial", Font.PLAIN, 12));
        JScrollPane tableScrollPane = new JScrollPane(countyTable);

        chartPanel = new JPanel(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Результаты", scrollPane);
        tabbedPane.addTab("Топ округов", tableScrollPane);
        tabbedPane.addTab("Диаграмма", chartPanel);

        add(buttonPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);

        setLocationRelativeTo(null);
    }

    private void initializePresenter() {
        try {
            DatabaseManager db = new DatabaseManager();
            SchoolRepository repository = new SqliteSchoolRepository(db);
            CsvSchoolParser parser = new CsvSchoolParser();

            presenter = new AnalyticsPresenter(repository, parser, this);

        } catch (SQLException e) {
            showError("Ошибка инициализации базы данных: " + e.getMessage());
        }
    }

    private void setupEventHandlers() {
        loadCsvBtn.addActionListener(e -> presenter.onLoadCsvButtonClicked());
        loadDbBtn.addActionListener(e -> presenter.onConnectToDatabaseButtonClicked());
        showResultsBtn.addActionListener(e -> presenter.onShowAllResultsButtonClicked());
    }

    @Override
    public void updateAverageStudents(List<DatabaseManager.CountyAvg> data) {
        SwingUtilities.invokeLater(() -> {
            DefaultTableModel model = new DefaultTableModel(
                    new String[]{"Округ", "Среднее число студентов"}, 0
            );

            for (var c : data) {
                model.addRow(new Object[]{c.county(), c.avgStudents()});
            }
            countyTable.setModel(model);

            chartPanel.removeAll();
            chartPanel.add(ChartGenerator.generateAvgStudentsByCountyChart(data));
            chartPanel.revalidate();
            chartPanel.repaint();

            resultsArea.append("Среднее число студентов по 10 округам:\n");
            for (var c : data) {
                resultsArea.append(String.format("  %-25s → %.1f%n", c.county(), c.avgStudents()));
            }
            resultsArea.append("\n");
        });
    }

    @Override
    public void updateAverageExpenditure(Double value) {
        SwingUtilities.invokeLater(() -> {
            if (value != null && !value.isNaN()) {
                resultsArea.append(String.format("Средние расходы в округах Fresno, Contra Costa, El Dorado, Glenn: $%.2f%n", value));
            } else {
                resultsArea.append("Нет данных по указанным округам.\n");
            }
            resultsArea.append("\n");
        });
    }

    @Override
    public void updateTopSchool(School school) {
        SwingUtilities.invokeLater(() -> {
            if (school != null) {
                resultsArea.append("Лучшая школа по математике:\n");
                resultsArea.append(String.format("  Название: %s%n", school.schoolName()));
                resultsArea.append(String.format("  Округ: %s%n", school.county()));
                resultsArea.append(String.format("  Студентов: %d%n", school.students()));
                resultsArea.append(String.format("  Математика: %.1f%n", school.math()));
            } else {
                resultsArea.append("Нет школ в заданных диапазонах.\n");
            }
        });
    }

    @Override
    public void showMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this, message, "Информация",
                    JOptionPane.INFORMATION_MESSAGE);
        });
    }

    @Override
    public void showError(String error) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this, error, "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
        });
    }

    @Override
    public void showLoading(boolean isLoading) {
        SwingUtilities.invokeLater(() -> {
            progressBar.setVisible(isLoading);
            loadCsvBtn.setEnabled(!isLoading);
            loadDbBtn.setEnabled(!isLoading);
            showResultsBtn.setEnabled(!isLoading);

            if (isLoading) {
                progressBar.setIndeterminate(true);
            } else {
                progressBar.setIndeterminate(false);
            }
        });
    }

    @Override
    public void clearResults() {
        SwingUtilities.invokeLater(() -> {
            resultsArea.setText("");
        });
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