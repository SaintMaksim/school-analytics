package ru.urfu.schoolanalytics.view;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.sql.SQLException;
import java.util.List;

import ru.urfu.schoolanalytics.model.DatabaseManager;
import ru.urfu.schoolanalytics.model.School;

import static org.junit.jupiter.api.Assertions.*;

class ChartGeneratorTest {

    @TempDir
    Path tempDir;

    private DatabaseManager db;
    private Path dbFile;

    @BeforeEach
    void setUp() throws SQLException {
        dbFile = tempDir.resolve("test.db");
        System.setProperty("jdbc.url", "jdbc:sqlite:" + dbFile.toAbsolutePath());
        db = new DatabaseManager();
    }

    @Test
    void testGenerateAvgStudentsByCountyChart() throws SQLException {
        School school = new School(1, "Test School", "Test County", "KK-08", 200, 5.0, 0.1, 0.2, 10, 5000.0, 6000.0, 0.5, 0.6, 0.7);
        db.saveSchools(List.of(school));

        var chartPanel = ChartGenerator.generateAvgStudentsByCountyChart(db);
        assertNotNull(chartPanel);
    }

    @Test
    void testGenerateAvgStudentsByCountyChartWithNoData() throws SQLException {
        var chartPanel = ChartGenerator.generateAvgStudentsByCountyChart(db);
        assertNotNull(chartPanel);
    }

    @Test
    void testGenerateAvgStudentsByCountyChartWithMultipleCounties() throws SQLException {
        for (int i = 1; i <= 12; i++) { // 12 округов
            School school = new School(i, "School " + i, "County " + i, "KK-08", 100 + i * 10, 5.0, 0.1, 0.2, 10, 5000.0, 6000.0, 0.5, 0.6, 0.7);
            db.saveSchools(List.of(school));
        }

        var chartPanel = ChartGenerator.generateAvgStudentsByCountyChart(db);
        assertNotNull(chartPanel);
    }

    @Test
    void testGenerateAvgStudentsByCountyChartWithLongCountyName() throws SQLException {
        School school = new School(1, "School", "VeryLongCountyNameThatShouldBeTruncatedInChart", "KK-08", 200, 5.0, 0.1, 0.2, 10, 5000.0, 6000.0, 0.5, 0.6, 0.7);
        db.saveSchools(List.of(school));

        var chartPanel = ChartGenerator.generateAvgStudentsByCountyChart(db);
        assertNotNull(chartPanel);
    }

    @Test
    void testGenerateAvgStudentsByCountyChartHandlesSqlException() {
        assertDoesNotThrow(() -> ChartGenerator.generateAvgStudentsByCountyChart(db));
    }

    @Test
    void testGenerateAvgStudentsByCountyChartWithDuplicateCountyNames() throws SQLException {
        School school1 = new School(1, "School 1", "Duplicate County", "KK-08", 200, 5.0, 0.1, 0.2, 10, 5000.0, 6000.0, 0.5, 0.6, 0.7);
        School school2 = new School(2, "School 2", "Duplicate County", "KK-08", 300, 5.0, 0.1, 0.2, 10, 5000.0, 6000.0, 0.5, 0.6, 0.7);
        db.saveSchools(List.of(school1, school2));

        var chartPanel = ChartGenerator.generateAvgStudentsByCountyChart(db);
        assertNotNull(chartPanel);
        List<DatabaseManager.CountyAvg> result = db.getAverageStudentsByCounty(10);
        assertEquals(1, result.size());
        assertEquals(250.0, result.get(0).avgStudents(), 0.01);
    }
}