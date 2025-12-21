package ru.urfu.schoolanalytics.presenter;

import ru.urfu.schoolanalytics.model.DatabaseManager;
import ru.urfu.schoolanalytics.model.School;
import ru.urfu.schoolanalytics.model.CsvSchoolParser;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class AnalyticsService {

    private final CsvSchoolParser parser = new CsvSchoolParser(); // теперь всё ок
    private DatabaseManager db;

    public void connectToDatabase() throws SQLException {
        this.db = new DatabaseManager();
    }

    public void loadCsvToDatabase() throws IOException, SQLException {
        List<School> schools = parser.parse();
        db.saveSchools(schools);
    }

    public List<DatabaseManager.CountyAvg> getAverageStudentsByCounty(int limit) throws SQLException {
        return db.getAverageStudentsByCounty(limit);
    }

    public Double getAverageExpenditureInCounties(String... counties) throws SQLException {
        return db.getAverageExpenditureInCounties(counties);
    }

    public School getTopMathSchoolInStudentRanges() throws SQLException {
        return db.getTopMathSchoolInStudentRanges();
    }

    public DatabaseManager getDatabase() {
        return db;
    }

    public void closeDatabase() throws SQLException {
        if (db != null) db.close();
    }
}