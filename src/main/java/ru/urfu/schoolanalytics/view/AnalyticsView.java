package ru.urfu.schoolanalytics.view;

import ru.urfu.schoolanalytics.model.DatabaseManager;
import ru.urfu.schoolanalytics.model.School;

import java.util.List;

public interface AnalyticsView {
    void updateAverageStudents(List<DatabaseManager.CountyAvg> data);
    void updateAverageExpenditure(Double value);
    void updateTopSchool(School school);
    void showMessage(String message);
    void showError(String error);
    void showLoading(boolean isLoading);
    void clearResults();
}