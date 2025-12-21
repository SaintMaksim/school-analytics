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
        CsvSchoolParser parser = new CsvSchoolParser(csvFile.toString()); // передаём путь
        List<School> schools = parser.parse();

        assertEquals(1, schools.size());
        School school = schools.get(0);
        assertEquals(75119, school.district());
        assertEquals("Test School", school.schoolName());
        assertEquals("Alameda", school.county());
        assertEquals(195, school.students());
        assertEquals(690.0, school.math(), 0.01);
    }
}