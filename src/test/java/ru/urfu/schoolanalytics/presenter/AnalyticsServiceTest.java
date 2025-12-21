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


    @Test
    void testLoadCsvToDatabase() throws Exception {
        assertDoesNotThrow(() -> service.loadCsvToDatabase());
    }

    @Test
    void testGetTopMathSchoolInStudentRanges() throws SQLException {
        var db = service.getDatabase();
        School school1 = new School(1, "InRange1", "Test County", "KK-08", 6000, 5.0, 0.1, 0.2, 10, 5000.0, 6000.0, 0.5, 0.6, 80.0);
        School school2 = new School(2, "InRange2", "Test County", "KK-08", 10500, 5.0, 0.1, 0.2, 10, 5000.0, 6000.0, 0.5, 0.6, 90.0);
        School school3 = new School(3, "OutOfRange", "Test County", "KK-08", 4000, 5.0, 0.1, 0.2, 10, 5000.0, 6000.0, 0.5, 0.6, 95.0);
        db.saveSchools(List.of(school1, school2, school3));

        School result = service.getTopMathSchoolInStudentRanges();
        assertNotNull(result);
        assertEquals("InRange2", result.schoolName());
        assertEquals(90.0, result.math(), 0.01);
    }

    @Test
    void testGetAverageExpenditureInCountiesMultipleCounties() throws SQLException {
        var db = service.getDatabase();
        School school1 = new School(1, "Test School 1", "Fresno", "KK-08", 100, 5.0, 0.1, 0.2, 10, 6000.0, 6000.0, 0.5, 0.6, 0.7);
        School school2 = new School(2, "Test School 2", "Contra Costa", "KK-08", 150, 5.0, 0.1, 0.2, 10, 7000.0, 6000.0, 0.5, 0.6, 0.7);
        db.saveSchools(List.of(school1, school2));

        Double result = service.getAverageExpenditureInCounties("Fresno", "Contra Costa");
        assertNotNull(result);
        assertEquals((6000.0 + 7000.0) / 2, result, 0.01);
    }

    @Test
    void testGetAverageExpenditureInCountiesWithNoMatchingData() throws SQLException {
        Double result = service.getAverageExpenditureInCounties("NonExistentCounty");
        assertTrue(result == null || result.isNaN());
    }

}