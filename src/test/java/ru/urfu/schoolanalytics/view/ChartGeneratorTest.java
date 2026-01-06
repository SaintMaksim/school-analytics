package ru.urfu.schoolanalytics.view;

import org.junit.jupiter.api.Test;
import ru.urfu.schoolanalytics.model.DatabaseManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ChartGeneratorTest {

    @Test
    void testGenerateAvgStudentsByCountyChartWithData() {
        List<DatabaseManager.CountyAvg> data = List.of(
                new DatabaseManager.CountyAvg("County A", 150.5),
                new DatabaseManager.CountyAvg("County B", 200.0),
                new DatabaseManager.CountyAvg("County C", 180.3)
        );

        var chartPanel = ChartGenerator.generateAvgStudentsByCountyChart(data);

        assertNotNull(chartPanel);
        assertNotNull(chartPanel.getChart());
        assertEquals("Среднее число студентов по 10 округам",
                chartPanel.getChart().getTitle().getText());
    }

    @Test
    void testGenerateAvgStudentsByCountyChartWithEmptyData() {
        List<DatabaseManager.CountyAvg> emptyData = List.of();

        var chartPanel = ChartGenerator.generateAvgStudentsByCountyChart(emptyData);

        assertNotNull(chartPanel);
        assertNotNull(chartPanel.getChart());
    }

    @Test
    void testGenerateAvgStudentsByCountyChartWithLongCountyNames() {
        List<DatabaseManager.CountyAvg> data = List.of(
                new DatabaseManager.CountyAvg("Very Long County Name That Should Be Truncated", 150.5),
                new DatabaseManager.CountyAvg("Short", 200.0)
        );

        var chartPanel = ChartGenerator.generateAvgStudentsByCountyChart(data);

        assertNotNull(chartPanel);
        assertNotNull(chartPanel.getChart());
    }

    @Test
    void testGenerateAvgStudentsByCountyChartWithSingleCounty() {
        List<DatabaseManager.CountyAvg> data = List.of(
                new DatabaseManager.CountyAvg("Single County", 100.0)
        );

        var chartPanel = ChartGenerator.generateAvgStudentsByCountyChart(data);

        assertNotNull(chartPanel);
        assertNotNull(chartPanel.getChart());
    }

    @Test
    void testGenerateAvgStudentsByCountyChartWithNullData() {
        assertThrows(NullPointerException.class, () -> {
            ChartGenerator.generateAvgStudentsByCountyChart(null);
        });
    }
}