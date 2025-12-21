package ru.urfu.schoolanalytics.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.sql.SQLException;
import java.util.ArrayList;
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

    @Test
    void testSaveSchoolsClearsPreviousData() throws SQLException {
        School oldSchool = new School(1, "Old School", "Old County", "KK-08", 50, 2.0, 0.0, 0.0, 5, 2000.0, 3000.0, 0.1, 0.2, 50.0);
        db.saveSchools(List.of(oldSchool));

        List<DatabaseManager.CountyAvg> before = db.getAverageStudentsByCounty(10);
        assertEquals(1, before.size());

        School newSchool = new School(2, "New School", "New County", "06-12", 200, 10.0, 0.1, 0.2, 20, 4000.0, 5000.0, 0.3, 0.4, 80.0);
        db.saveSchools(List.of(newSchool)); // перезапись

        List<DatabaseManager.CountyAvg> after = db.getAverageStudentsByCounty(10);
        assertEquals(1, after.size());
        assertEquals("New County", after.get(0).county());
    }

    @Test
    void testGetAverageStudentsByCountyLimit() throws SQLException {
        List<School> allSchools = new ArrayList<>();
        for (int i = 1; i <= 15; i++) {
            School school = new School(i, "School " + i, "County " + i, "KK-08", 100, 5.0, 0.1, 0.2, 10, 5000.0, 6000.0, 0.5, 0.6, 0.7);
            allSchools.add(school);
        }
        db.saveSchools(allSchools);

        List<DatabaseManager.CountyAvg> result = db.getAverageStudentsByCounty(5);
        assertEquals(5, result.size());
        assertEquals("County 9", result.get(0).county());
    }

    @Test
    void testGetAverageExpenditureInCountiesWithNonExistentCounty() throws SQLException {
        School school = new School(1, "Test School", "Existing County", "KK-08", 100, 5.0, 0.1, 0.2, 10, 6000.0, 6000.0, 0.5, 0.6, 0.7);
        db.saveSchools(List.of(school));

        Double result = db.getAverageExpenditureInCounties("NonExistentCounty", "AnotherNonExistent");
        assertNull(result);
    }

    @Test
    void testGetAverageExpenditureInCountiesWithZeroMatching() throws SQLException {
        School school = new School(1, "Test School", "Fresno", "KK-08", 100, 5.0, 0.1, 0.2, 10, 5.0, 6000.0, 0.5, 0.6, 0.7); // expenditure < 10
        db.saveSchools(List.of(school));

        Double result = db.getAverageExpenditureInCounties("Fresno");
        assertTrue(result == null || result.isNaN());
    }

    @Test
    void testGetTopMathSchoolInStudentRangesNoMatches() throws SQLException {
        School school1 = new School(1, "Small School", "Test County", "KK-08", 1000, 5.0, 0.1, 0.2, 10, 5000.0, 6000.0, 0.5, 0.6, 95.0); // вне диапазонов
        School school2 = new School(2, "Large School", "Test County", "KK-08", 15000, 5.0, 0.1, 0.2, 10, 5000.0, 6000.0, 0.5, 0.6, 98.0); // вне диапазонов
        db.saveSchools(List.of(school1, school2));

        School result = db.getTopMathSchoolInStudentRanges();
        assertNull(result);
    }
}