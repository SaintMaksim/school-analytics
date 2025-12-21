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
}