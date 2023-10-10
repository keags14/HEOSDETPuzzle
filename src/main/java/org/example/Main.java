package org.example;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class Main {
    public static String generateNationalInsuranceNumber(List<String> nationalInsuranceValues){
        List<String> nationalInsuranceNumber = new ArrayList<>();
            nationalInsuranceValues.add(nationalInsuranceValues.size()-1 , String.valueOf(Math.round(Math.random() * (9999 - 1000) + 1000)));
            nationalInsuranceValues.add(2, " ");
            nationalInsuranceValues.add(4, " ");
            nationalInsuranceValues.add(nationalInsuranceValues.size()-1, " ");
            nationalInsuranceNumber.add(String.join("", nationalInsuranceValues));
        return nationalInsuranceNumber.stream().findFirst().get();
    }
    public static List<List<String>> determineNationalInsuranceNumber(List<List<String>> dataset) {
        List<String> nationalInsuranceValues = new ArrayList<>();
        String nationalInsuranceNumber = null;
        List<List<String>> nationalInsuranceValuesList = new ArrayList<>();
        List<String> headers = Arrays.asList("First names","Last name","Date of Birth","Country of Birth");
        for (List<String> user: dataset) {
            if(!new HashSet<>(user).containsAll(headers)){
                for (String column: user) {
                    boolean isSpecificValue = column.equals("Wales") || column.equals("England") || column.equals("Scotland") || column.equals("Northern Ireland");
                    if(Character.isAlphabetic(column.charAt(0))) {
                        if(!isSpecificValue && user.indexOf(column) == 3){
                            nationalInsuranceValues.add("O");
                        } else {
                            nationalInsuranceValues.add(String.valueOf(column.charAt(0)));
                        }
                    } else if(Character.isDigit(column.charAt(0))){
                        String date = LocalDate.parse(column).format(DateTimeFormatter.ofPattern("yy"));
                        nationalInsuranceValues.add(date);
                    }
                }
                nationalInsuranceNumber = generateNationalInsuranceNumber(nationalInsuranceValues);
                user.add(nationalInsuranceNumber);
                nationalInsuranceValuesList.add(user);
                nationalInsuranceValues.clear();
            }
        }
        return nationalInsuranceValuesList;
    }
    public static List<List<String>> formatFile(List<List<String>> dataset) {

        List<List<String>> formattedList = new ArrayList<>();

        for (List<String> users : dataset) {
            List<String> requiredHeaders = new ArrayList<>();
            for (int j = 0; j < users.size(); j++) {
                if (j == 0 || j == 1 || j == 4 || j == 5) {
                    requiredHeaders.add(users.get(j));
                }
            }
            formattedList.add(new ArrayList<>(requiredHeaders));
            requiredHeaders.clear();
        }

        return formattedList;
    }
    private static List<List<String>> readDatasetFile() throws IOException {
        List<List<String>> records = new ArrayList<>();
        try (CSVReader csvReader = new CSVReader(new FileReader("src/test/resources/310157 HEO SDET - people data set.csv"));) {
            String[] values = null;
            while ((values = csvReader.readNext()) != null) {
                records.add(Arrays.asList(values));
            }
        } catch (CsvValidationException e) {
            throw new RuntimeException(e);
        }
        return records;
    }
    private static Boolean writeFileToCSV(List<List<String>> data) throws IOException {
        String csvFilePath = "D:\\Test\\HEOSDETPuzzle\\src\\test\\resources\\Part_1_Puzzle.csv";

        try (CSVWriter writer = new CSVWriter(new FileWriter(csvFilePath))) {
            writer.writeAll(formatData(data));
        }
        return true;
    }

    private static List<String[]> formatData(List<List<String>> data) {
        List<String[]> formattedData = new ArrayList<>();
        for (List<String> users : data) {
            String[] array = users.toArray(new String[0]);
            formattedData.add(array);
        }
        return formattedData;
    }
    public static void main(String[] args) throws IOException {
        Boolean nationalInsuranceNumber = null;
        File partOneFile = new File("D:\\Test\\HEOSDETPuzzle\\src\\test\\resources\\Part_1_Puzzle.csv");
        if(partOneFile.exists()) {
            Files.delete(partOneFile.toPath());
        }
        nationalInsuranceNumber = writeFileToCSV(
                determineNationalInsuranceNumber(
                formatFile(readDatasetFile())
                )
        );
    }
}