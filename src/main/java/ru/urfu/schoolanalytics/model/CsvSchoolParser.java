package ru.urfu.schoolanalytics.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class CsvSchoolParser {

    private static final String CSV_FILE_NAME = "schools.csv";
    private final String csvFileName;

    public CsvSchoolParser() {
        this.csvFileName = CSV_FILE_NAME;
    }

    // Конструктор для тестов
    public CsvSchoolParser(String csvFileName) {
        this.csvFileName = csvFileName;
    }

    public List<School> parse() throws IOException {
        List<School> schools = new ArrayList<>();

        // Проверяем, является ли csvFileName путем к файлу
        if (java.nio.file.Files.exists(java.nio.file.Paths.get(csvFileName))) {
            // Это путь к файлу (для тестов)
            try (var reader = new BufferedReader(new java.io.FileReader(csvFileName))) {
                String line;
                boolean isHeader = true;

                while ((line = reader.readLine()) != null) {
                    if (isHeader) {
                        isHeader = false;
                        continue;
                    }

                    String[] fields = splitCsvLine(line);

                    if (fields.length != 15) {
                        continue;
                    }

                    try {
                        String districtStr = unquote(fields[1]);
                        String schoolName = unquote(fields[2]);
                        String county = unquote(fields[3]);
                        String grades = unquote(fields[4]);
                        String studentsStr = unquote(fields[5]);

                        School school = new School(
                                Integer.parseInt(districtStr),
                                schoolName,
                                county,
                                grades,
                                Integer.parseInt(studentsStr),
                                Double.parseDouble(unquote(fields[6])),
                                Double.parseDouble(unquote(fields[7])),
                                Double.parseDouble(unquote(fields[8])),
                                Integer.parseInt(unquote(fields[9])),
                                Double.parseDouble(unquote(fields[10])),
                                Double.parseDouble(unquote(fields[11])),
                                Double.parseDouble(unquote(fields[12])),
                                Double.parseDouble(unquote(fields[13])),
                                Double.parseDouble(unquote(fields[14]))
                        );
                        schools.add(school);
                    } catch (NumberFormatException e) {
                    }
                }
            }
        } else {
            // Это ресурс (по умолчанию)
            try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(csvFileName);
                 InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                 BufferedReader reader = new BufferedReader(streamReader)) {

                if (inputStream == null) {
                    throw new IOException("Файл " + csvFileName + " не найден в resources/");
                }

                String line;
                boolean isHeader = true;

                while ((line = reader.readLine()) != null) {
                    if (isHeader) {
                        isHeader = false;
                        continue;
                    }

                    String[] fields = splitCsvLine(line);

                    if (fields.length != 15) {
                        continue;
                    }

                    try {
                        String districtStr = unquote(fields[1]);
                        String schoolName = unquote(fields[2]);
                        String county = unquote(fields[3]);
                        String grades = unquote(fields[4]);
                        String studentsStr = unquote(fields[5]);

                        School school = new School(
                                Integer.parseInt(districtStr),
                                schoolName,
                                county,
                                grades,
                                Integer.parseInt(studentsStr),
                                Double.parseDouble(unquote(fields[6])),
                                Double.parseDouble(unquote(fields[7])),
                                Double.parseDouble(unquote(fields[8])),
                                Integer.parseInt(unquote(fields[9])),
                                Double.parseDouble(unquote(fields[10])),
                                Double.parseDouble(unquote(fields[11])),
                                Double.parseDouble(unquote(fields[12])),
                                Double.parseDouble(unquote(fields[13])),
                                Double.parseDouble(unquote(fields[14]))
                        );
                        schools.add(school);
                    } catch (NumberFormatException e) {
                    }
                }
            }
        }

        return schools;
    }

    private String[] splitCsvLine(String line) {
        List<String> result = new ArrayList<>();
        boolean insideQuotes = false;
        StringBuilder currentField = new StringBuilder();
        for (char c : line.toCharArray()) {
            if (c == '"') {
                insideQuotes = !insideQuotes;
            } else if (c == ',' && !insideQuotes) {
                result.add(currentField.toString());
                currentField.setLength(0);
            } else {
                currentField.append(c);
            }
        }
        result.add(currentField.toString());
        return result.toArray(new String[0]);
    }

    private String unquote(String s) {
        if (s.startsWith("\"") && s.endsWith("\"") && s.length() >= 2) {
            return s.substring(1, s.length() - 1);
        }
        return s;
    }
}