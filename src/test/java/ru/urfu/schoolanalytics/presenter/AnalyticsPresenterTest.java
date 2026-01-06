package ru.urfu.schoolanalytics.presenter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.io.TempDir;

import ru.urfu.schoolanalytics.model.*;
import ru.urfu.schoolanalytics.view.TestView;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AnalyticsPresenterTest {

    @TempDir
    Path tempDir;

    private DatabaseManager db;
    private SchoolRepository repository;
    private TestView testView;
    private AnalyticsPresenter presenter;

    @BeforeEach
    void setUp() throws SQLException {
        db = new DatabaseManager("jdbc:sqlite::memory:");
        repository = new SqliteSchoolRepository(db);
        testView = new TestView();
    }

    @AfterEach
    void tearDown() throws SQLException {
        if (db != null) {
            db.close();
        }
    }

    @Test
    void testPresenterInitialization() {
        CsvSchoolParser parser = new CsvSchoolParser();
        presenter = new AnalyticsPresenter(repository, parser, testView);
        assertNotNull(presenter);
    }

    @Test
    void testOnLoadCsvButtonClickedSuccess() throws Exception {
        CsvSchoolParser testParser = new CsvSchoolParser("test.csv") {
            @Override
            public List<School> parse() throws IOException {
                return List.of(
                        new School(1, "Test School", "Test County", "KK-08",
                                200, 5.0, 0.1, 0.2, 10, 5000.0, 6000.0, 0.5, 0.6, 70.0)
                );
            }
        };

        presenter = new AnalyticsPresenter(repository, testParser, testView);
        presenter.onLoadCsvButtonClicked();
        Thread.sleep(500);

        List<DatabaseManager.CountyAvg> result = repository.getAverageStudentsByCounty(10);
        assertEquals(1, result.size());
    }

    @Test
    void testOnShowAllResultsButtonClicked() throws Exception {
        List<School> schools = List.of(
                new School(1, "School 1", "County A", "KK-08", 100, 5.0, 0.1, 0.2, 10, 6000.0, 6000.0, 0.5, 0.6, 70.0),
                new School(2, "School 2", "County B", "KK-08", 200, 5.0, 0.1, 0.2, 10, 7000.0, 6000.0, 0.5, 0.6, 80.0),
                new School(3, "Best Math School", "Fresno", "KK-08", 6500, 5.0, 0.1, 0.2, 10, 8000.0, 6000.0, 0.5, 0.6, 95.0),
                new School(4, "Another School", "Contra Costa", "KK-08", 300, 5.0, 0.1, 0.2, 10, 9000.0, 6000.0, 0.5, 0.6, 75.0)
        );

        repository.saveSchools(schools);

        CsvSchoolParser parser = new CsvSchoolParser();
        presenter = new AnalyticsPresenter(repository, parser, testView);
        presenter.onShowAllResultsButtonClicked();
        Thread.sleep(1000);

        assertTrue(testView.clearResultsCallCount > 0);
        assertNotNull(testView.averageStudentsData);
        assertEquals(4, testView.averageStudentsData.size());
        assertNotNull(testView.averageExpenditure);
        assertEquals(8500.0, testView.averageExpenditure, 0.01);
        assertNotNull(testView.topSchool);
        assertEquals("Best Math School", testView.topSchool.schoolName());
    }

    @Test
    void testOnLoadCsvButtonClickedWithError() throws Exception {
        CsvSchoolParser failingParser = new CsvSchoolParser() {
            @Override
            public List<School> parse() throws IOException {
                throw new IOException("Test IO Error");
            }
        };

        presenter = new AnalyticsPresenter(repository, failingParser, testView);
        presenter.onLoadCsvButtonClicked();
        Thread.sleep(500);

        assertNotNull(testView.error);
        assertTrue(testView.error.contains("Test IO Error"));
    }

}