package org.example;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Main {
    private static String generateNationalInsuranceNumber(List<String> nationalInsuranceValues){
        List<String> nationalInsuranceNumber = new ArrayList<>();
            nationalInsuranceValues.add(nationalInsuranceValues.size()-1 , String.valueOf(Math.round(Math.random() * (9999 - 1000) + 1000)));
            nationalInsuranceValues.add(2, " ");
            nationalInsuranceValues.add(4, " ");
            nationalInsuranceValues.add(nationalInsuranceValues.size()-1, " ");
            nationalInsuranceNumber.add(String.join("", nationalInsuranceValues));
        return nationalInsuranceNumber.stream().findFirst().get();
    }
    private static List<List<String>> determineNationalInsuranceNumber(List<List<String>> dataset) {
        List<String> nationalInsuranceValues = new ArrayList<>();
        String nationalInsuranceNumber;
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
    private static List<List<String>> formatFile(List<List<String>> dataset) {

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
    private static List<List<String>> readDatasetFile(String path) throws IOException {
        List<List<String>> records = new ArrayList<>();
        try (CSVReader csvReader = new CSVReader(new FileReader(path))) {
            String[] values;
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
    private static HashMap<String, Integer> nationalInsurancePerCountry(List<List<String>> data) {
        HashMap<String,Integer> result = new HashMap<>();
        for (List<String> users: data) {
            for (int i = 0; i < users.size(); i++) {
                if(i == 4) {
                    String countryCode = String.valueOf(users.get(i).charAt(users.get(i).length()-1));
                    if(countryCode.equalsIgnoreCase("w")) {
                        if(result.containsKey("Wales")){
                            result.put("Wales", result.get("Wales") + 1);
                        } else {
                            result.putIfAbsent("Wales", 1);
                        }
                    } else if(countryCode.equalsIgnoreCase("e")) {
                        if(result.containsKey("England")){
                            result.put("England", result.get("England") + 1);
                        } else {
                            result.putIfAbsent("England", 1);
                        }
                    } else if(countryCode.equalsIgnoreCase("s")) {
                        if(result.containsKey("Scotland")){
                            result.put("Scotland", result.get("Scotland") + 1);
                        } else {
                            result.putIfAbsent("Scotland", 1);
                        }
                    } else if(countryCode.equalsIgnoreCase("n")) {
                        if(result.containsKey("Northern Ireland")){
                            result.put("Northern Ireland", result.get("Northern Ireland") + 1);
                        } else {
                            result.putIfAbsent("Northern Ireland", 1);
                        }
                    } else {
                        if(result.containsKey("Non-UK")){
                            result.put("Non-UK", result.get("Non-UK") + 1);
                        } else {
                            result.putIfAbsent("Non-UK", 1);
                        }
                    }
                }
            }
        }
        return result;
    }

    public static void displayNationalInsurancePerCountry(HashMap<String, Integer> nationalInsurancePerCountryTable) {
        System.out.println("Part-2");
        for (Map.Entry<String, Integer> result: nationalInsurancePerCountryTable.entrySet()) {
            System.out.println("Key: " + result.getKey() + " Value: " + result.getValue());
        }
    }

    public static void main(String[] args) throws IOException {
        //region Part 1 of the HEO Puzzle
        Path partOneFilePath = Paths.get("src/test/resources/Part_1_Puzzle.csv");
        boolean nationalInsuranceNumberFileGenerated;
        if(partOneFilePath.toFile().exists()) {
            Files.delete(partOneFilePath);
        }
        nationalInsuranceNumberFileGenerated = writeFileToCSV(
                determineNationalInsuranceNumber(
                        formatFile(readDatasetFile("src/test/resources/310157 HEO SDET - people data set.csv"))
                )
        );
        if(nationalInsuranceNumberFileGenerated) {
            System.out.println("File has been generated for Part-1 of the puzzle at " + partOneFilePath.toAbsolutePath());
        }
        //endregion
        //region Part 2 of the HEO Puzzle
        displayNationalInsurancePerCountry(
                        (nationalInsurancePerCountry(
                        readDatasetFile(partOneFilePath.toAbsolutePath().toString())))
        );
        //endregion
    }
}