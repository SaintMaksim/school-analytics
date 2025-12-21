package ru.urfu.schoolanalytics.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CsvSchoolParserTest {

    @TempDir
    Path tempDir;

    private Path csvFile;

    @BeforeEach
    void setUp() throws IOException {
        csvFile = tempDir.resolve("schools.csv");
        try (FileWriter writer = new FileWriter(csvFile.toFile())) {
            writer.write("\"\",\"district\",\"school\",\"county\",\"grades\",\"students\",\"teachers\",\"calworks\",\"lunch\",\"computer\",\"expenditure\",\"income\",\"english\",\"read\",\"math\"\n");
            writer.write("\"1\",\"75119\",\"Test School\",\"Alameda\",\"KK-08\",195,10.8999996185303,0.510200023651123,2.04080009460449,67,6384.9111328125,22.6900005340576,0,691.599975585938,690\n");
        }
    }

    @Test
    void testParseReturnsCorrectSchool() throws IOException {
        CsvSchoolParser parser = new CsvSchoolParser(csvFile.toString());
        List<School> schools = parser.parse();

        assertEquals(1, schools.size());
        School school = schools.get(0);
        assertEquals(75119, school.district());
        assertEquals("Test School", school.schoolName());
        assertEquals("Alameda", school.county());
        assertEquals(195, school.students());
        assertEquals(690.0, school.math(), 0.01);
    }

    @Test
    void testParseWithMultipleValidRows() throws IOException {
        try (FileWriter writer = new FileWriter(csvFile.toFile(), true)) {
            writer.write("\"2\",\"12345\",\"Another School\",\"Orange\",\"06-12\",500,25.0,0.1,0.2,100,5000.0,60000.0,0.5,0.6,700.0\n");
        }
        CsvSchoolParser parser = new CsvSchoolParser(csvFile.toString());
        List<School> schools = parser.parse();

        assertEquals(2, schools.size());
        assertEquals("Another School", schools.get(1).schoolName());
    }

    @Test
    void testParseHandlesEmptyRow() throws IOException {
        try (FileWriter writer = new FileWriter(csvFile.toFile(), true)) {
            writer.write("\n");
        }
        CsvSchoolParser parser = new CsvSchoolParser(csvFile.toString());
        List<School> schools = parser.parse();

        assertEquals(1, schools.size());
    }

    @Test
    void testParseHandlesRowWithIncorrectColumnCount() throws IOException {
        try (FileWriter writer = new FileWriter(csvFile.toFile(), true)) {
            writer.write("\"3\",\"12345\",\"Bad Row\",\"County\"\n");
        }
        CsvSchoolParser parser = new CsvSchoolParser(csvFile.toString());
        List<School> schools = parser.parse();

        assertEquals(1, schools.size());
    }

    @Test
    void testParseHandlesQuotedFieldsCorrectly() throws IOException {
        try (FileWriter writer = new FileWriter(csvFile.toFile(), true)) {
            writer.write("\"4\",\"12346\",\"\"\"Quoted\"\" School\",\"Kern\",\"KK-08\",300,15.0,0.05,0.1,50,4500.0,55000.0,0.55,0.65,710.0\n");
        }
        CsvSchoolParser parser = new CsvSchoolParser(csvFile.toString());
        List<School> schools = parser.parse();

        assertEquals(2, schools.size());
        assertEquals("Quoted School", schools.get(1).schoolName());
    }

    @Test
    void testParseHandlesInvalidNumberInNumericField() throws IOException {
        try (FileWriter writer = new FileWriter(csvFile.toFile(), true)) {
            writer.write("\"5\",\"abc\",\"Invalid Number\",\"County\",\"KK-08\",300,15.0,0.05,0.1,50,4500.0,55000.0,0.55,0.65,720.0\n"); // district не число
        }
        CsvSchoolParser parser = new CsvSchoolParser(csvFile.toString());
        List<School> schools = parser.parse();

        assertEquals(1, schools.size());
    }
}