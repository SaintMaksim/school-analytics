package ru.urfu.schoolanalytics.view;

import ru.urfu.schoolanalytics.model.DatabaseManager;
import ru.urfu.schoolanalytics.model.School;

import java.util.ArrayList;
import java.util.List;

public class TestView implements AnalyticsView {
    public List<DatabaseManager.CountyAvg> averageStudentsData = new ArrayList<>();
    public Double averageExpenditure;
    public School topSchool;
    public String message;
    public String error;
    public Boolean loadingState;
    public int clearResultsCallCount = 0;

    @Override
    public void updateAverageStudents(List<DatabaseManager.CountyAvg> data) {
        this.averageStudentsData = new ArrayList<>(data);
    }

    @Override
    public void updateAverageExpenditure(Double value) {
        this.averageExpenditure = value;
    }

    @Override
    public void updateTopSchool(School school) {
        this.topSchool = school;
    }

    @Override
    public void showMessage(String message) {
        this.message = message;
    }

    @Override
    public void showError(String error) {
        this.error = error;
    }

    @Override
    public void showLoading(boolean isLoading) {
        this.loadingState = isLoading;
    }

    @Override
    public void clearResults() {
        this.clearResultsCallCount++;
        this.averageStudentsData.clear();
        this.averageExpenditure = null;
        this.topSchool = null;
        this.message = null;
        this.error = null;
    }

    public void reset() {
        clearResults();
        this.loadingState = null;
        this.clearResultsCallCount = 0;
    }
}