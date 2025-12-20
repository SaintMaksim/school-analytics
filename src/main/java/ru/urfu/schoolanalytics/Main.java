package ru.urfu.schoolanalytics;

import ru.urfu.schoolanalytics.database.DatabaseManager;
import ru.urfu.schoolanalytics.model.School;
import ru.urfu.schoolanalytics.parser.CsvSchoolParser;

import java.io.IOException;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        try {
            // 1. Парсим CSV
            CsvSchoolParser parser = new CsvSchoolParser();
            var schools = parser.parse();
            System.out.println("Загружено " + schools.size() + " школ из CSV");

            // 2. Сохраняем в БД
            DatabaseManager db = new DatabaseManager();
            db.saveSchools(schools);
            System.out.println(" Все школы сохранены в SQLite");

            // 3. Выполняем запросы
            // Запрос 1
            var topCounties = db.getAverageStudentsByCounty(10);
            System.out.println("\n📈 Топ-10 округов по среднему числу студентов:");
            topCounties.forEach(c -> System.out.printf("%-25s: %.1f%n", c.county(), c.avgStudents()));

            // Запрос 2
            Double avgExp = db.getAverageExpenditureInCounties("Fresno", "Contra Costa", "El Dorado", "Glenn");
            if (avgExp != null && !avgExp.isNaN()) {
                System.out.printf("%n Средние расходы в указанных округах: %.2f%n", avgExp);
            } else {
                System.out.println("%n️ Нет данных по указанным округам — проверьте наличие в CSV");
            }

            // Запрос 3
            School topMathSchool = db.getTopMathSchoolInStudentRanges();
            if (topMathSchool != null) {
                System.out.printf("%n Лучшая школа по математике в диапазонах студентов:%n" +
                                "   %s (округ: %s, студентов: %d, math: %.1f)%n",
                        topMathSchool.schoolName(),
                        topMathSchool.county(),
                        topMathSchool.students(),
                        topMathSchool.math());
            } else {
                System.out.println("Не найдено школ с числом студентов в диапазонах 5000–7500 или 10000–11000");
            }

            db.close();

        } catch (IOException | SQLException e) {
            System.err.println("Ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }
}