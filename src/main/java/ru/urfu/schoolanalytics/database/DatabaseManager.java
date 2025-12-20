package ru.urfu.schoolanalytics.database;

import ru.urfu.schoolanalytics.model.School;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:schools.db";
    private Connection connection;

    public DatabaseManager() throws SQLException {
        this.connection = DriverManager.getConnection(DB_URL);
        createTables();
    }

    private void createTables() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS schools (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                district INTEGER NOT NULL,
                school_name TEXT NOT NULL,
                county TEXT NOT NULL,
                grades TEXT,
                students INTEGER NOT NULL,
                teachers REAL,
                calworks REAL,
                lunch REAL,
                computer INTEGER,
                expenditure REAL,
                income REAL,
                english REAL,
                read REAL,
                math REAL
            );
            """;
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }

    public void saveSchools(List<School> schools) throws SQLException {
        String sql = """
            INSERT INTO schools (
                district, school_name, county, grades, students,
                teachers, calworks, lunch, computer, expenditure,
                income, english, read, math
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            connection.setAutoCommit(false);
            for (School school : schools) {
                pstmt.setInt(1, school.district());
                pstmt.setString(2, school.schoolName());
                pstmt.setString(3, school.county());
                pstmt.setString(4, school.grades());
                pstmt.setInt(5, school.students());
                pstmt.setDouble(6, school.teachers());
                pstmt.setDouble(7, school.calworks());
                pstmt.setDouble(8, school.lunch());
                pstmt.setInt(9, school.computer());
                pstmt.setDouble(10, school.expenditure());
                pstmt.setDouble(11, school.income());
                pstmt.setDouble(12, school.english());
                pstmt.setDouble(13, school.read());
                pstmt.setDouble(14, school.math());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            connection.commit();
            connection.setAutoCommit(true);
        }
    }

    // ============ ЗАПРОС 1: Среднее число студентов по 10 округам ============
    public List<CountyAvg> getAverageStudentsByCounty(int limit) throws SQLException {
        String sql = """
            SELECT county, AVG(students) as avg_students
            FROM schools
            GROUP BY county
            ORDER BY avg_students DESC
            LIMIT ?
            """;
        List<CountyAvg> result = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                result.add(new CountyAvg(rs.getString("county"), rs.getDouble("avg_students")));
            }
        }
        return result;
    }

    // ============ ЗАПРОС 2: Средние расходы в указанных округах ============
    public Double getAverageExpenditureInCounties(String... counties) throws SQLException {
        if (counties.length == 0) return null;

        StringBuilder inClause = new StringBuilder();
        for (int i = 0; i < counties.length; i++) {
            inClause.append("?");
            if (i < counties.length - 1) inClause.append(",");
        }

        String sql = """
            SELECT AVG(expenditure)
            FROM schools
            WHERE county IN (%s)
              AND expenditure > 10
            """.formatted(inClause);

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            for (int i = 0; i < counties.length; i++) {
                stmt.setString(i + 1, counties[i]);
            }
            ResultSet rs = stmt.executeQuery();
            return rs.getDouble(1);
        }
    }

    // ============ ЗАПРОС 3: Лучшая школа по математике в диапазонах студентов ============
    public School getTopMathSchoolInStudentRanges() throws SQLException {
        String sql = """
            SELECT district, school_name, county, grades, students,
                   teachers, calworks, lunch, computer, expenditure,
                   income, english, read, math
            FROM schools
            WHERE (students BETWEEN 5000 AND 7500)
               OR (students BETWEEN 10000 AND 11000)
            ORDER BY math DESC
            LIMIT 1
            """;
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return new School(
                        rs.getInt("district"),
                        rs.getString("school_name"),
                        rs.getString("county"),
                        rs.getString("grades"),
                        rs.getInt("students"),
                        rs.getDouble("teachers"),
                        rs.getDouble("calworks"),
                        rs.getDouble("lunch"),
                        rs.getInt("computer"),
                        rs.getDouble("expenditure"),
                        rs.getDouble("income"),
                        rs.getDouble("english"),
                        rs.getDouble("read"),
                        rs.getDouble("math")
                );
            }
        }
        return null; // не найдено
    }

    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    // Вспомогательный record для запроса 1
    public record CountyAvg(String county, double avgStudents) {}
}