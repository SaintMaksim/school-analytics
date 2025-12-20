package ru.urfu.schoolanalytics.chart;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartPanel;
import org.jfree.data.category.DefaultCategoryDataset;
import ru.urfu.schoolanalytics.database.DatabaseManager;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class ChartGenerator {

    public static void generateAvgStudentsByCountyChart(DatabaseManager db, String outputPath)
            throws SQLException, IOException {

        var data = db.getAverageStudentsByCounty(10);

        // Создаём набор данных
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (var entry : data) {
            // Обрезаем длинные названия округов для читаемости
            String countyLabel = entry.county().length() > 15
                    ? entry.county().substring(0, 14) + "…"
                    : entry.county();
            dataset.addValue(entry.avgStudents(), "Студенты", countyLabel);
        }

        // Создаём график
        JFreeChart barChart = ChartFactory.createBarChart(
                "Среднее число студентов по 10 округам",
                "Округ",
                "Среднее число студентов",
                dataset
        );

        // Настройка шрифта (опционально, для поддержки кириллицы)
        barChart.getTitle().setFont(new Font("Arial", Font.BOLD, 16));
        barChart.getCategoryPlot().getDomainAxis().setLabelFont(new Font("Arial", Font.PLAIN, 12));
        barChart.getCategoryPlot().getRangeAxis().setLabelFont(new Font("Arial", Font.PLAIN, 12));

        // Создаём изображение
        int width = 1000;
        int height = 600;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, width, height);

        ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setSize(width, height);
        chartPanel.setOpaque(false);
        barChart.draw(g2, new Rectangle(width, height));

        g2.dispose();

        // Создаём папку, если её нет
        new File("visualizations").mkdirs();

        // Сохраняем как PNG
        File outputFile = new File(outputPath);
        ImageIO.write(image, "png", outputFile);

        System.out.println("📊 Диаграмма сохранена: " + outputPath);
    }
}