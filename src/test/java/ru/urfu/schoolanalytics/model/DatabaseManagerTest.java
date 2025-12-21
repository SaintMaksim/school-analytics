package ru.urfu.schoolanalytics.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseManagerTest {

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

    @AfterEach
    void tearDown() throws SQLException {
        if (db != null) {
            db.close();
        }
    }

    @Test
    void testSaveAndRetrieveSchools() throws SQLException {
        School testSchool = new School(1, "Test School", "Test County", "KK-08", 100, 5.0, 0.1, 0.2, 10, 5000.0, 6000.0, 0.5, 0.6, 0.7);

        db.saveSchools(List.of(testSchool));

        List<DatabaseManager.CountyAvg> result = db.getAverageStudentsByCounty(10);
        assertEquals(1, result.size());
        assertEquals("Test County", result.get(0).county());
        assertEquals(100.0, result.get(0).avgStudents(), 0.01);
    }

    @Test
    void testGetAverageExpenditureInCounties() throws SQLException {
        School school = new School(1, "Test School", "Fresno", "KK-08", 100, 5.0, 0.1, 0.2, 10, 6000.0, 6000.0, 0.5, 0.6, 0.7);
        db.saveSchools(List.of(school));

        Double result = db.getAverageExpenditureInCounties("Fresno");
        assertNotNull(result);
        assertEquals(6000.0, result, 0.01);
    }

    @Test
    void testGetTopMathSchoolInStudentRanges() throws SQLException {
        School school = new School(1, "Test School", "Test County", "KK-08", 6000, 5.0, 0.1, 0.2, 10, 5000.0, 6000.0, 0.5, 0.6, 95.0);
        db.saveSchools(List.of(school));

        School result = db.getTopMathSchoolInStudentRanges();
        assertNotNull(result);
        assertEquals(95.0, result.math(), 0.01);
        assertEquals(6000, result.students());
    }
}