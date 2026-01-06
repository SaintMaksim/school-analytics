package ru.urfu.schoolanalytics.model;

import java.sql.SQLException;
import java.util.List;

public interface SchoolRepository {
    List<DatabaseManager.CountyAvg> getAverageStudentsByCounty(int limit) throws SQLException;
    Double getAverageExpenditureInCounties(String... counties) throws SQLException;
    School getTopMathSchoolInStudentRanges() throws SQLException;
    void saveSchools(List<School> schools) throws SQLException;
}