package ru.netology;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        String fileName = "data.csv";

        // exercise 1
        createCSV(fileName, "1,John,Smith,USA,25", "2,Ivan,Petrov,RU,23");
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        List<Employee> listCSV = parseCSV(columnMapping, fileName);
        writeString(listToJson(listCSV), "dataCSV.json");

        // exercise 2
        fileName = "data.xml";
        List<Employee> listXML = parseXML(fileName);
        writeString(listToJson(listXML), "dataXML.json");

        // exercise 3
        String json = readString("dataXML.json");
        List<Employee> list = jsonToList(json);
        for (Employee employee : list) {
            System.out.println(employee);
        }
    }

    private static List<Employee> jsonToList(String json) {
        List<Employee> result = new ArrayList<>();
        JsonParser parser = new JsonParser();
        JsonArray array = (JsonArray) parser.parse(json);

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        for (JsonElement jsonElement : array) {
            result.add(gson.fromJson(jsonElement.toString(), Employee.class));
        }
        return result;
    }

    private static String readString(String fileName) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))){
            String s;
            while ((s = br.readLine()) != null){
                sb.append(s);
            }
        } catch (IOException e){
            System.out.println(e.getMessage());
        }
        return sb.toString();
    }

    private static List<Employee> parseXML(String fileName) throws ParserConfigurationException, IOException, SAXException {
        List<Employee> list = new ArrayList<>();
        Long id = 0L;
        String firstName = null;
        String lastName = null;
        String country = null;
        int age = 0;

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(fileName));

        NodeList employeeNodeList = doc.getElementsByTagName("employee");
        for (int i = 0; i < employeeNodeList.getLength(); i++) {
            if (employeeNodeList.item(i).getNodeType() == Node.ELEMENT_NODE){
                Element employeeElement = (Element) employeeNodeList.item(i);

                NodeList childNodes = employeeElement.getChildNodes();
                for (int j=0; j<childNodes.getLength(); j++){
                    if (childNodes.item(j).getNodeType() == Node.ELEMENT_NODE){
                        Element childElement = (Element) childNodes.item(j);
                        switch (childElement.getNodeName()){
                            case "id": {
                                id = Long.parseLong(childElement.getTextContent());
                                break;
                            }
                            case "firstName": {
                                firstName = childElement.getTextContent();
                                break;
                            }
                            case "lastName": {
                                lastName = childElement.getTextContent();
                                break;
                            }
                            case "country": {
                                country = childElement.getTextContent();
                                break;
                            }
                            case "age": {
                                age = Integer.parseInt(childElement.getTextContent());
                                break;
                            }
                        }
                    }
                }
                list.add(new Employee(id,firstName,lastName,country,age));
            }
        }
        return list;
    }


    private static void writeString(String json, String fileName) {
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(json);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private static String listToJson(List<Employee> list) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        return gson.toJson(list, listType);
    }

    private static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        List<Employee> result = null;
        try (CSVReader reader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(reader)
                    .withMappingStrategy(strategy)
                    .build();
            result = csv.parse();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return result;
    }

    public static void createCSV(String fileName, String... data) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(fileName))) {
            for (String s : data) {
                String[] tmp = s.split(",");
                writer.writeNext(tmp);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}