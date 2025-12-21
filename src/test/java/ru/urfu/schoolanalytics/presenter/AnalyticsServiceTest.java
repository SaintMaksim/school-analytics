package ru.urfu.schoolanalytics.presenter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.io.TempDir;
import ru.urfu.schoolanalytics.model.DatabaseManager;
import ru.urfu.schoolanalytics.model.School;

import java.nio.file.Path;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AnalyticsServiceTest {

    @TempDir
    Path tempDir;

    private AnalyticsService service;
    private Path dbFile;

    @BeforeEach
    void setUp() throws SQLException {
        service = new AnalyticsService();
        dbFile = tempDir.resolve("test.db");
        System.setProperty("jdbc.url", "jdbc:sqlite:" + dbFile.toAbsolutePath());
        service.connectToDatabase();
    }

    @AfterEach
    void tearDown() throws SQLException {
        if (service != null) {
            service.closeDatabase();
        }
    }

    @Test
    void testGetAverageStudentsByCounty() throws SQLException {
        // Добавим тестовые данные в БД
        var db = service.getDatabase();
        var school = new School(1, "Test School", "Test County", "KK-08", 200, 5.0, 0.1, 0.2, 10, 5000.0, 6000.0, 0.5, 0.6, 0.7);
        db.saveSchools(List.of(school));

        List<DatabaseManager.CountyAvg> result = service.getAverageStudentsByCounty(10);
        assertEquals(1, result.size());
        assertEquals("Test County", result.get(0).county());
        assertEquals(200.0, result.get(0).avgStudents(), 0.01);
    }

    @Test
    void testGetAverageExpenditureInCounties() throws SQLException {
        var db = service.getDatabase();
        var school = new School(1, "Test School", "Fresno", "KK-08", 100, 5.0, 0.1, 0.2, 10, 6000.0, 6000.0, 0.5, 0.6, 0.7);
        db.saveSchools(List.of(school));

        Double result = service.getAverageExpenditureInCounties("Fresno");
        assertNotNull(result);
        assertEquals(6000.0, result, 0.01);
    }
}