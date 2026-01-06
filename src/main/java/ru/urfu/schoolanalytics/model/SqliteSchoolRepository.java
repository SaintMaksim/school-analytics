package ru.urfu.schoolanalytics.model;

import java.sql.SQLException;
import java.util.List;

public class SqliteSchoolRepository implements SchoolRepository {

    private final DatabaseManager db;

    public SqliteSchoolRepository(DatabaseManager db) {
        this.db = db;
    }

    @Override
    public List<DatabaseManager.CountyAvg> getAverageStudentsByCounty(int limit) throws SQLException {
        return db.getAverageStudentsByCounty(limit);
    }

    @Override
    public Double getAverageExpenditureInCounties(String... counties) throws SQLException {
        return db.getAverageExpenditureInCounties(counties);
    }

    @Override
    public School getTopMathSchoolInStudentRanges() throws SQLException {
        return db.getTopMathSchoolInStudentRanges();
    }

    @Override
    public void saveSchools(List<School> schools) throws SQLException {
        db.saveSchools(schools);
    }
}