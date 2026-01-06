package ru.urfu.schoolanalytics.view;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import ru.urfu.schoolanalytics.model.DatabaseManager;

import java.awt.Font;
import java.util.List;

public class ChartGenerator {

    public static ChartPanel generateAvgStudentsByCountyChart(List<DatabaseManager.CountyAvg> data) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (var entry : data) {
            String countyLabel = entry.county().length() > 15
                    ? entry.county().substring(0, 14) + "…"
                    : entry.county();
            dataset.addValue(entry.avgStudents(), "Студенты", countyLabel);
        }

        JFreeChart barChart = ChartFactory.createBarChart(
                "Среднее число студентов по 10 округам",
                "Округ",
                "Среднее число студентов",
                dataset
        );

        barChart.getTitle().setFont(new Font("Arial", Font.BOLD, 16));
        barChart.getCategoryPlot().getDomainAxis().setLabelFont(new Font("Arial", Font.PLAIN, 12));
        barChart.getCategoryPlot().getRangeAxis().setLabelFont(new Font("Arial", Font.PLAIN, 12));

        return new ChartPanel(barChart);
    }
}