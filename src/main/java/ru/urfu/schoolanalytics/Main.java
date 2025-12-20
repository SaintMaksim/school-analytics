package ru.urfu.schoolanalytics;

import ru.urfu.schoolanalytics.database.DatabaseManager;
import ru.urfu.schoolanalytics.model.School;
import ru.urfu.schoolanalytics.parser.CsvSchoolParser;
import ru.urfu.schoolanalytics.chart.ChartGenerator;

import java.io.IOException;
import java.sql.SQLException;

public class Main{
    public static void main(String[] args) {
        System.out.println("🚀 Запуск аналитики школ Калифорнии...\n");

        try {
            // 1. Загрузка данных из CSV
            CsvSchoolParser parser = new CsvSchoolParser();
            var schools = parser.parse();
            System.out.println("✅ Загружено " + schools.size() + " школ из CSV");

            // 2. Сохранение в базу данных
            DatabaseManager db = new DatabaseManager();
            db.saveSchools(schools);
            System.out.println("✅ Все данные сохранены в SQLite (schools.db)\n");

            // 3. Запрос 1: Среднее число студентов по 10 округам
            System.out.println("📈 Запрос 1: Среднее число студентов по 10 округам");
            var topCounties = db.getAverageStudentsByCounty(10);
            for (var c : topCounties) {
                System.out.printf("  %-25s → %.1f студентов%n", c.county(), c.avgStudents());
            }

            // 4. Запрос 2: Средние расходы в указанных округах
            System.out.println("\n💰 Запрос 2: Средние расходы в округах Fresno, Contra Costa, El Dorado, Glenn");
            // Проверим, есть ли такие округа в данных
            Double avgExpenditure = db.getAverageExpenditureInCounties("Fresno", "Contra Costa", "El Dorado", "Glenn");
            if (avgExpenditure != null && !avgExpenditure.isNaN()) {
                System.out.printf("  Средние расходы: $%.2f на студента%n", avgExpenditure);
            } else {
                System.out.println("  ⚠️ Нет данных по указанным округам. Проверьте наличие в CSV.");
                // Альтернатива: используем реально существующие округа
                avgExpenditure = db.getAverageExpenditureInCounties("Los Angeles", "San Diego", "Orange", "Sacramento");
                if (avgExpenditure != null && !avgExpenditure.isNaN()) {
                    System.out.printf("  (Для примера: средние расходы в LA, San Diego, Orange, Sacramento: $%.2f)%n", avgExpenditure);
                }
            }

            // 5. Запрос 3: Лучшая школа по математике в заданных диапазонах студентов
            System.out.println("\n🎯 Запрос 3: Лучшая школа по математике (5000–7500 или 10000–11000 студентов)");
            School topSchool = db.getTopMathSchoolInStudentRanges();
            if (topSchool != null) {
                System.out.printf("  Название: %s%n", topSchool.schoolName());
                System.out.printf("  Округ: %s%n", topSchool.county());
                System.out.printf("  Студентов: %d%n", topSchool.students());
                System.out.printf("  Балл по математике: %.1f%n", topSchool.math());
            } else {
                System.out.println("  ⚠️ Не найдено школ с числом студентов в диапазонах 5000–7500 или 10000–11000.");
            }

            // 6. Генерация диаграммы
            System.out.println("\n📊 Генерация диаграммы...");
            ChartGenerator.generateAvgStudentsByCountyChart(
                    db,
                    "visualizations/avg_students_by_county.png"
            );

            // 7. Завершение
            db.close();
            System.out.println("\n✅ Проект завершён. Диаграмма сохранена в папке 'visualizations'.");

        } catch (IOException e) {
            System.err.println("❌ Ошибка ввода-вывода: " + e.getMessage());
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("❌ Ошибка базы данных: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("❌ Неожиданная ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }
}