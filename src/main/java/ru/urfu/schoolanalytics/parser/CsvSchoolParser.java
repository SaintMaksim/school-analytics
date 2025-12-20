package ru.urfu.schoolanalytics.parser;

import ru.urfu.schoolanalytics.model.School;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class CsvSchoolParser {

    private static final String CSV_FILE_NAME = "schools.csv";

    public List<School> parse() throws IOException {
        List<School> schools = new ArrayList<>();

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(CSV_FILE_NAME);
             InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {

            if (inputStream == null) {
                throw new IOException("Файл " + CSV_FILE_NAME + " не найден в src/main/resources/");
            }

            // 🔑 КЛЮЧЕВАЯ НАСТРОЙКА: TSV + кавычки
            CSVParser parser = new CSVParserBuilder()
                    .withSeparator('\t')          // табуляция
                    .withQuoteChar('"')           // кавычки как quote-символ
                    .withStrictQuotes(false)      // разрешить смешанные (не все в кавычках)
                    .build();

            CSVReader csvReader = new CSVReaderBuilder(reader)
                    .withCSVParser(parser)
                    .withSkipLines(1) // пропустить заголовок
                    .build();

            String[] line;
            while ((line = csvReader.readNext()) != null) {
                // Ваши данные: 15 колонок (из-за пустого ID в начале)
                if (line.length != 15) {
                    System.err.println("⚠️ Некорректная строка: ожидалось 15 колонок, получено " + line.length);
                    continue;
                }

                try {
                    School school = new School(
                            Integer.parseInt(line[1]),  // district
                            line[2],                    // school
                            line[3],                    // county
                            line[4],                    // grades
                            Integer.parseInt(line[5]),  // students
                            Double.parseDouble(line[6]),// teachers
                            Double.parseDouble(line[7]),// calworks
                            Double.parseDouble(line[8]),// lunch
                            Integer.parseInt(line[9]),  // computer
                            Double.parseDouble(line[10]),// expenditure
                            Double.parseDouble(line[11]),// income
                            Double.parseDouble(line[12]),// english
                            Double.parseDouble(line[13]),// read
                            Double.parseDouble(line[14]) // math
                    );
                    schools.add(school);
                } catch (NumberFormatException e) {
                    System.err.println("❌ Ошибка числа в строке: " + String.join("|", line));
                }
            }
        } catch (Exception e) {
            throw new IOException("Ошибка при чтении CSV", e);
        }

        return schools;
    }
}