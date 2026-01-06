package ru.urfu.schoolanalytics.presenter;

import ru.urfu.schoolanalytics.model.*;
import ru.urfu.schoolanalytics.view.AnalyticsView;

import javax.swing.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class AnalyticsPresenter {
    private final SchoolRepository repository;
    private final CsvSchoolParser parser;
    private final AnalyticsView view;

    public AnalyticsPresenter(SchoolRepository repository,
                              CsvSchoolParser parser,
                              AnalyticsView view) {
        this.repository = repository;
        this.parser = parser;
        this.view = view;
    }

    public void onLoadCsvButtonClicked() {
        new Thread(() -> {
            try {
                view.showLoading(true);

                List<School> schools = parser.parse();
                showMessageInUIThread(String.format("Загружено %d школ из CSV", schools.size()));

                repository.saveSchools(schools);
                showMessageInUIThread("Данные успешно сохранены в БД");

            } catch (IOException e) {
                showErrorInUIThread("Ошибка чтения CSV файла: " + e.getMessage());
            } catch (SQLException e) {
                showErrorInUIThread("Ошибка работы с базой данных: " + e.getMessage());
            } catch (Exception e) {
                showErrorInUIThread("Неизвестная ошибка: " + e.getMessage());
            } finally {
                view.showLoading(false);
            }
        }).start();
    }

    public void onConnectToDatabaseButtonClicked() {
        new Thread(() -> {
            try {
                view.showLoading(true);
                view.showMessage("Подключение к базе данных...");
                showMessageInUIThread("Подключено к базе данных");
            } catch (Exception e) {
                showErrorInUIThread("Ошибка подключения: " + e.getMessage());
            } finally {
                view.showLoading(false);
            }
        }).start();
    }

    public void onShowAllResultsButtonClicked() {
        new Thread(() -> {
            try {
                view.showLoading(true);
                view.clearResults();

                List<DatabaseManager.CountyAvg> avgStudents =
                        repository.getAverageStudentsByCounty(10);
                view.updateAverageStudents(avgStudents);

                Double avgExpenditure = repository.getAverageExpenditureInCounties(
                        "Fresno", "Contra Costa", "El Dorado", "Glenn"
                );
                view.updateAverageExpenditure(avgExpenditure);

                School topSchool = repository.getTopMathSchoolInStudentRanges();
                view.updateTopSchool(topSchool);

                showMessageInUIThread("Все аналитические задачи выполнены");

            } catch (SQLException e) {
                showErrorInUIThread("Ошибка выполнения SQL-запросов: " + e.getMessage());
            } catch (Exception e) {
                showErrorInUIThread("Ошибка при выполнении аналитики: " + e.getMessage());
            } finally {
                view.showLoading(false);
            }
        }).start();
    }

    private void showMessageInUIThread(String message) {
        SwingUtilities.invokeLater(() -> view.showMessage(message));
    }

    private void showErrorInUIThread(String error) {
        SwingUtilities.invokeLater(() -> view.showError(error));
    }
}