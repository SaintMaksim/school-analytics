package ru.urfu.schoolanalytics.model;

public record School(
        int district,
        String schoolName,
        String county,
        String grades,
        int students,
        double teachers,
        double calworks,
        double lunch,
        int computer,
        double expenditure,
        double income,
        double english,
        double read,
        double math
) {}
