package com.example.webservice_xml;

import org.springframework.web.bind.annotation.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/students")

public class StudentController {


    private static final String XML_FILE_PATH = "students.xml";




    @PostMapping("/addMultipleStudent")
    public List<String> addMultipleStudent(@RequestParam int n, @RequestBody List<student> students) {
        List<String> errors = new ArrayList<>();
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            Document doc;
            Element rootElement;


            // Check if the XML file exists
            File xmlFile = new File(XML_FILE_PATH);
            if (xmlFile.exists()) {
                doc = docBuilder.parse(xmlFile);
                rootElement = doc.getDocumentElement();
            } else {
                doc = docBuilder.newDocument();
                rootElement = doc.createElement("University");
                doc.appendChild(rootElement);
            }

            int flag = 0;
            for (int i = 0; i < n; i++) {
                flag = 0;
                student student = students.get(i);

                // Validate input data
                if (isNullOrEmpty(String.valueOf(student.getId())) || isNullOrEmpty(student.getFirstName()) || isNullOrEmpty(student.getLastName()) ||
                        isNullOrEmpty(student.getGender()) || isNullOrEmpty(student.getAddress()) ||
                        student.getFirstName().matches(".*[^a-z].*") || student.getLastName().matches(".*[^a-z].*")) {
                    errors.add("Invalid input data for Student " + (i + 1));
                    flag = 1;
                    continue;
                }

                double gpa = student.getGpa();
                if (gpa < 0 || gpa > 4) {
                    errors.add("Invalid GPA for Student " + (i + 1) + ". GPA must be between 0 and 4.");
                    flag = 1;
                    continue;
                }

                String id = String.valueOf(student.getId());
                if (isDuplicateId(rootElement, id)) {
                    errors.add("Duplicate ID for Student " + (i + 1) + ". ID must be unique.");
                    flag = 1;
                    continue;
                }

                Element studentElement = doc.createElement("Student");
                studentElement.setAttribute("ID", id);
                rootElement.appendChild(studentElement);

                Element firstNameElement = doc.createElement("FirstName");
                firstNameElement.setTextContent(student.getFirstName());
                studentElement.appendChild(firstNameElement);

                Element lastNameElement = doc.createElement("LastName");
                lastNameElement.setTextContent(student.getLastName());
                studentElement.appendChild(lastNameElement);

                Element genderElement = doc.createElement("Gender");
                genderElement.setTextContent(student.getGender());
                studentElement.appendChild(genderElement);

                Element gpaElement = doc.createElement("GPA");
                gpaElement.setTextContent(String.valueOf(gpa));
                studentElement.appendChild(gpaElement);

                Element levelElement = doc.createElement("Level");
                levelElement.setTextContent(String.valueOf(student.getLevel()));
                studentElement.appendChild(levelElement);

                Element addressElement = doc.createElement("Address");
                addressElement.setTextContent(student.getAddress());
                studentElement.appendChild(addressElement);
                if (flag == 0) {
                    errors.add("Student"+(i+1)+" added successfully!");
                }
            }

            // Write the content into XML file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(XML_FILE_PATH);
            transformer.transform(source, result);



        } catch (ParserConfigurationException | SAXException | IOException | TransformerException e) {
            e.printStackTrace();
            errors.add("Failed to add students.") ;
        }
        return errors;
    }

    private boolean isDuplicateId(Element rootElement, String id) {
        NodeList studentNodes = rootElement.getElementsByTagName("Student");
        for (int i = 0; i < studentNodes.getLength(); i++) {
            Node studentNode = studentNodes.item(i);
            if (studentNode.getNodeType() == Node.ELEMENT_NODE) {
                Element studentElement = (Element) studentNode;
                String existingId = studentElement.getAttribute("ID");
                if (existingId.equals(id)) {
                    return true;
                }
            }
        }
        return false;
    }

    @PostMapping("/addStudent")
    public String addStudent(@RequestBody student student) {
        try {
            // Validate input data


            // Proceed to add the student
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            Document doc;
            Element rootElement;

            // Check if the XML file exists
            File xmlFile = new File(XML_FILE_PATH);
            if (xmlFile.exists()) {
                doc = docBuilder.parse(xmlFile);
                rootElement = doc.getDocumentElement();
            } else {
                doc = docBuilder.newDocument();
                rootElement = doc.createElement("University");
                doc.appendChild(rootElement);
            }
            if (isNullOrEmpty(String.valueOf(student.getId())) || isNullOrEmpty(student.getFirstName())
                    || isNullOrEmpty(student.getLastName()) || isNullOrEmpty(student.getGender())
                    || student.getGpa() < 0 || student.getGpa() > 4
                    || isNullOrEmpty(String.valueOf(student.getLevel())) || isNullOrEmpty(student.getAddress())) {
                return "Invalid input data. Please provide all details and ensure data validity.";
            }

            // Check for duplicate ID
            if (isDuplicateId(rootElement, String.valueOf(student.getId()))) {
                return "Student with the given ID already exists.";
            }

            // Check if names and address contain only characters (a-z)
            if (!isValidString(student.getFirstName()) || !isValidString(student.getLastName())
                    || !isValidString(student.getAddress())) {
                return "Invalid characters in names or address. Only characters (a-z) are allowed.";
            }

            Element studentElement = doc.createElement("Student");
            studentElement.setAttribute("ID", String.valueOf(student.getId()));
            rootElement.appendChild(studentElement);

            Element firstNameElement = doc.createElement("FirstName");
            firstNameElement.setTextContent(student.getFirstName());
            studentElement.appendChild(firstNameElement);

            Element lastNameElement = doc.createElement("LastName");
            lastNameElement.setTextContent(student.getLastName());
            studentElement.appendChild(lastNameElement);

            Element genderElement = doc.createElement("Gender");
            genderElement.setTextContent(student.getGender());
            studentElement.appendChild(genderElement);

            Element gpaElement = doc.createElement("GPA");
            gpaElement.setTextContent(String.valueOf(student.getGpa()));
            studentElement.appendChild(gpaElement);

            Element levelElement = doc.createElement("Level");
            levelElement.setTextContent(String.valueOf(student.getLevel()));
            studentElement.appendChild(levelElement);

            Element addressElement = doc.createElement("Address");
            addressElement.setTextContent(student.getAddress());
            studentElement.appendChild(addressElement);

            // Write the content into the XML file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(XML_FILE_PATH);
            transformer.transform(source, result);

            return "Student added successfully!";
        } catch (ParserConfigurationException | SAXException | IOException | TransformerException e) {
            e.printStackTrace();
            return "Failed to add student.";
        }
    }

    // Helper method to check if a string is null or empty
    private boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    // Helper method to check if a string contains only characters (a-z)
    private boolean isValidString(String str) {
        return str.matches("^[a-zA-Z]+$");
    }

    // Helper method to check if a student ID already exists
    private boolean isDuplicateId(String id) {
        // Implement logic to check if the ID already exists in the XML file
        // You may need to read the XML file and check for duplicate IDs
        // Return true if the ID exists, false otherwise
        return false; // Placeholder, replace with actual implementation
    }// lsa m4 kamla

    @GetMapping("/GetAll")

    public List<student> GetAllStudents() {
        List<student> students = new ArrayList<>();

        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            Document doc = docBuilder.parse(XML_FILE_PATH);

            NodeList studentNodes = doc.getElementsByTagName("Student");

            for (int i = 0; i < studentNodes.getLength(); i++) {
                Node studentNode = studentNodes.item(i);

                if (studentNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element studentElement = (Element) studentNode;
                    students.add(parseStudentFromElement(studentElement));

                }
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }

        return students;
    }



//    @DeleteMapping("/delete")
//    public String deleteStudent(@RequestParam int id) {
//        try {
//            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
//            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
//
//            Document doc = docBuilder.parse(XML_FILE_PATH);
//
//            NodeList studentNodes = doc.getElementsByTagName("Student");
//
//            for (int i = 0; i < studentNodes.getLength(); i++) {
//                Node studentNode = studentNodes.item(i);
//
//                if (studentNode.getNodeType() == Node.ELEMENT_NODE) {
//                    Element studentElement = (Element) studentNode;
//
//                    String studentId = studentElement.getAttribute("ID");
//
//                    if (studentId.equals(id)) {
//                        studentElement.getParentNode().removeChild(studentElement);
//
//                        // Write the updated content into XML file
//                        TransformerFactory transformerFactory = TransformerFactory.newInstance();
//                        Transformer transformer = transformerFactory.newTransformer();
//                        DOMSource source = new DOMSource(doc);
//                        StreamResult result = new StreamResult(XML_FILE_PATH);
//                        transformer.transform(source, result);
//
//                        return "Student deleted successfully!";
//                    }
//                }
//            }
//            return "Failed to delete student.";
//        } catch (ParserConfigurationException | SAXException | IOException | TransformerException e) {
//            e.printStackTrace();
//        }
//
//        return "Failed to delete student.";
//    }
@DeleteMapping("/delete")
public String deleteStudent(@RequestParam int id) {
    try {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        Document doc = docBuilder.parse(XML_FILE_PATH);

        NodeList studentNodes = doc.getElementsByTagName("Student");

        for (int i = 0; i < studentNodes.getLength(); i++) {
            Node studentNode = studentNodes.item(i);

            if (studentNode.getNodeType() == Node.ELEMENT_NODE) {
                Element studentElement = (Element) studentNode;

                String studentId = String.valueOf(studentElement.getAttribute("ID"));

                if (studentId.equals(String.valueOf(id))) {
                    studentElement.getParentNode().removeChild(studentElement);

                    // Write the updated content into XML file
                    TransformerFactory transformerFactory = TransformerFactory.newInstance();
                    Transformer transformer = transformerFactory.newTransformer();
                    DOMSource source = new DOMSource(doc);
                    StreamResult result = new StreamResult(XML_FILE_PATH);
                    transformer.transform(source, result);

                    return "Student deleted successfully!";
                }
            }
        }
        return "Student not found or failed to delete student.";
    } catch (ParserConfigurationException | SAXException | IOException | TransformerException e) {
        e.printStackTrace();
        return "Failed to delete student.";
    }
}


    private student parseStudentFromElement(Element studentElement) {
        student student = new student();

        student.setId(Integer.parseInt(studentElement.getAttribute("ID")));
        student.setFirstName(studentElement.getElementsByTagName("FirstName").item(0).getTextContent());
        student.setLastName(studentElement.getElementsByTagName("LastName").item(0).getTextContent());
        student.setGender(studentElement.getElementsByTagName("Gender").item(0).getTextContent());
        student.setGpa(Double.parseDouble(studentElement.getElementsByTagName("GPA").item(0).getTextContent()));
        student.setLevel(Integer.parseInt(studentElement.getElementsByTagName("Level").item(0).getTextContent()));
        student.setAddress(studentElement.getElementsByTagName("Address").item(0).getTextContent());

        return student;
    }


    @GetMapping("/SearchByFirstName")

    public Map<String, Object> searchStudents_ByFirstName(@RequestParam String firstName) {
        Map<String, Object> result = new HashMap<>();
        List<student> students = new ArrayList<>();

        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            Document doc = docBuilder.parse(XML_FILE_PATH);

            NodeList studentNodes = doc.getElementsByTagName("Student");

            for (int i = 0; i < studentNodes.getLength(); i++) {
                Node studentNode = studentNodes.item(i);

                if (studentNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element studentElement = (Element) studentNode;

                    String studentFirstName = studentElement.getElementsByTagName("FirstName").item(0).getTextContent();

                    if ((firstName == null || studentFirstName.equals(firstName)) ) {
                        students.add(parseStudentFromElement(studentElement));
                    }
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }

        result.put("size", students.size());
        result.put("students", students);

        return result;
    }



    @GetMapping("/searchStudents_ByLastName")
    public Map<String, Object> searchStudents_ByLastName(@RequestParam String LastName) {
        Map<String, Object> result = new HashMap<>();
        List<student> students = new ArrayList<>();

        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            Document doc = docBuilder.parse(XML_FILE_PATH);

            NodeList studentNodes = doc.getElementsByTagName("Student");

            for (int i = 0; i < studentNodes.getLength(); i++) {
                Node studentNode = studentNodes.item(i);

                if (studentNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element studentElement = (Element) studentNode;

                    String studentLastName = studentElement.getElementsByTagName("LastName").item(0).getTextContent();

                    if ((LastName == null || studentLastName.equals(LastName))) {
                        students.add(parseStudentFromElement(studentElement));
                    }
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }

        result.put("size", students.size());
        result.put("students", students);

        return result;
    }




    @GetMapping("/searchStudents_ByLevel")
    public Map<String, Object> searchStudents_ByLevel(@RequestParam String Level) {
        Map<String, Object> result = new HashMap<>();
        List<student> students = new ArrayList<>();

        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            Document doc = docBuilder.parse(XML_FILE_PATH);

            NodeList studentNodes = doc.getElementsByTagName("Student");

            for (int i = 0; i < studentNodes.getLength(); i++) {
                Node studentNode = studentNodes.item(i);

                if (studentNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element studentElement = (Element) studentNode;

                    String studentlevel = studentElement.getElementsByTagName("Level").item(0).getTextContent();

                    if ((Level == null || studentlevel.equals(Level))) {
                        students.add(parseStudentFromElement(studentElement));
                    }
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }

        result.put("size", students.size());
        result.put("students", students);

        return result;
    }




    @GetMapping("/SearchByGpa")
    public Map<String, Object> searchStudents_ByGpa(@RequestParam String gpa) {
        Map<String, Object> result = new HashMap<>();
        List<student> students = new ArrayList<>();

        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            Document doc = docBuilder.parse(XML_FILE_PATH);

            NodeList studentNodes = doc.getElementsByTagName("Student");

            for (int i = 0; i < studentNodes.getLength(); i++) {
                Node studentNode = studentNodes.item(i);

                if (studentNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element studentElement = (Element) studentNode;

                    String studentGpa = studentElement.getElementsByTagName("GPA").item(0).getTextContent();

                    if ((gpa == null || studentGpa.equals(gpa))) {
                        students.add(parseStudentFromElement(studentElement));
                    }
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }

        result.put("size", students.size());
        result.put("students", students);

        return result;
    }

        @GetMapping("/searchStudents_ByGender")

    public Map<String, Object> searchStudents_ByGender(@RequestParam String Gender) {
        Map<String, Object> result = new HashMap<>();
        List<student> students = new ArrayList<>();

        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            Document doc = docBuilder.parse(XML_FILE_PATH);

            NodeList studentNodes = doc.getElementsByTagName("Student");

            for (int i = 0; i < studentNodes.getLength(); i++) {
                Node studentNode = studentNodes.item(i);

                if (studentNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element studentElement = (Element) studentNode;

                    String studentGender = studentElement.getElementsByTagName("Gender").item(0).getTextContent();

                    if ((Gender == null || studentGender.equals(Gender)) ) {
                        students.add(parseStudentFromElement(studentElement));
                    }
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }

        result.put("size", students.size());
        result.put("students", students);

        return result;
    }


    @GetMapping("/searchStudents_ByID")
    public Map<String, Object> searchStudents_ByID(@RequestParam int id){
        Map<String, Object> result = new HashMap<>();
        List<student> students = new ArrayList<>();

        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            Document doc = docBuilder.parse(XML_FILE_PATH);

            NodeList studentNodes = doc.getElementsByTagName("Student");

            for (int i = 0; i < studentNodes.getLength(); i++) {
                Node studentNode = studentNodes.item(i);

                if (studentNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element studentElement = (Element) studentNode;

                    int studentID = Integer.parseInt(studentElement.getAttribute("ID"));

                    if (studentID==id) {
                        students.add(parseStudentFromElement(studentElement));
                    }
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }

        result.put("size", students.size());
        result.put("students", students);

        return result;
    }

    @GetMapping("/searchStudents_ByAddress")

    public Map<String, Object> searchStudents_ByAddress(@RequestParam String Address) {
        Map<String, Object> result = new HashMap<>();
        List<student> students = new ArrayList<>();

        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            Document doc = docBuilder.parse(XML_FILE_PATH);

            NodeList studentNodes = doc.getElementsByTagName("Student");

            for (int i = 0; i < studentNodes.getLength(); i++) {
                Node studentNode = studentNodes.item(i);

                if (studentNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element studentElement = (Element) studentNode;

                    String studentAddress = studentElement.getElementsByTagName("Address").item(0).getTextContent();

                    if ((Address == null || studentAddress.equals(Address)) ) {
                        students.add(parseStudentFromElement(studentElement));
                    }
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        result.put("size", students.size());
        result.put("students", students);

        return result;
    }




    @GetMapping("/sort")
    public List<student> sortStudents(@RequestParam String attribute, @RequestParam String order) {
        List<student> students = new ArrayList<>();

        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            Document doc = docBuilder.parse(XML_FILE_PATH);

            NodeList studentNodes = doc.getElementsByTagName("Student");

            for (int i = 0; i < studentNodes.getLength(); i++) {
                Node studentNode = studentNodes.item(i);

                if (studentNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element studentElement = (Element) studentNode;
                    students.add(parseStudentFromElement(studentElement));
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }


        Comparator<student> comparator = null;
        switch (attribute) {
            case "ID":
                comparator = Comparator.comparing(student::getId);
                break;
            case "FirstName":
                comparator = Comparator.comparing(student::getFirstName);
                break;
            case "LastName":
                comparator = Comparator.comparing(student::getLastName);
                break;
            case "Gender":
                comparator = Comparator.comparing(student::getGender);
                break;
            case "GPA":
                comparator = Comparator.comparing(student::getGpa);
                break;
            case "Level":
                comparator = Comparator.comparing(student::getLevel);
                break;
            case "Address":
                comparator = Comparator.comparing(student::getAddress);
                break;
            default:
                return students;
        }

        if (order.equalsIgnoreCase("desc")) {
            comparator = comparator.reversed();
        }

        students.sort(comparator);
        updateXMLFile( students);

        return students;
    }

    private void updateXMLFile(List<student> students) {
        try {
            ////////////////////////////////////////
            clearXMLFile(XML_FILE_PATH);
            /////////////////////////////////////////
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            Document doc;
            Element rootElement;
            File xmlFile = new File(XML_FILE_PATH);
            if (xmlFile.exists()) {
                doc = docBuilder.parse(xmlFile);
                rootElement = doc.getDocumentElement();
            } else {
                doc = docBuilder.newDocument();
                rootElement = doc.createElement("University");
                doc.appendChild(rootElement);
            }
            for (int i = 0; i < students.size(); i++) {
                student student = students.get(i);

                Element studentElement = doc.createElement("Student");
                studentElement.setAttribute("ID", String.valueOf(student.getId()));
                rootElement.appendChild(studentElement);

                Element firstNameElement = doc.createElement("FirstName");
                firstNameElement.setTextContent(student.getFirstName());
                studentElement.appendChild(firstNameElement);

                Element lastNameElement = doc.createElement("LastName");
                lastNameElement.setTextContent(student.getLastName());
                studentElement.appendChild(lastNameElement);

                Element genderElement = doc.createElement("Gender");
                genderElement.setTextContent(student.getGender());
                studentElement.appendChild(genderElement);

                Element gpaElement = doc.createElement("GPA");
                gpaElement.setTextContent(String.valueOf(student.getGpa()));
                studentElement.appendChild(gpaElement);

                Element levelElement = doc.createElement("Level");
                levelElement.setTextContent(String.valueOf(student.getLevel()));
                studentElement.appendChild(levelElement);

                Element addressElement = doc.createElement("Address");
                addressElement.setTextContent(student.getAddress());
                studentElement.appendChild(addressElement);
            }

            // Write the content into the XML file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(XML_FILE_PATH);
            transformer.transform(source, result);
        } catch (ParserConfigurationException | TransformerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }
    }
    public static void clearXMLFile(String filePath) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // Create an empty XML document
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("University"); // You can customize the root element name
            doc.appendChild(rootElement);

            // Write the empty content to the XML file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(filePath));
            transformer.transform(source, result);

            System.out.println("XML file cleared successfully.");

        } catch (ParserConfigurationException | TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }



    @PutMapping ("/updateStudent")
    public String updateStudent(@RequestParam int id, @RequestBody student updatedStudent) throws ParserConfigurationException, IOException, SAXException, TransformerException {
        if (isNullOrEmpty(String.valueOf(updatedStudent.getId())) || isNullOrEmpty(updatedStudent.getFirstName())
                || isNullOrEmpty(updatedStudent.getLastName()) || isNullOrEmpty(updatedStudent.getGender())
                || updatedStudent.getGpa() < 0 || updatedStudent.getGpa() > 4
                || isNullOrEmpty(String.valueOf(updatedStudent.getLevel())) || isNullOrEmpty(updatedStudent.getAddress())) {
            return "Invalid input data. Please provide all details and ensure data validity.";
        }

        // Check for duplicate ID
        if (isDuplicateId(String.valueOf(updatedStudent.getId()))) {
            return "Student with the given ID already exists.";
        }

        // Check if names and address contain only characters (a-z)
        if (!isValidString(updatedStudent.getFirstName()) || !isValidString(updatedStudent.getLastName())
                || !isValidString(updatedStudent.getAddress())) {
            return "Invalid characters in names or address. Only characters (a-z) are allowed.";
        }
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        Document doc = docBuilder.parse(XML_FILE_PATH);

        NodeList studentNodes = doc.getElementsByTagName("Student");

        for (int i = 0; i < studentNodes.getLength(); i++) {
            Node studentNode = studentNodes.item(i);

            if (studentNode.getNodeType() == Node.ELEMENT_NODE) {
                Element studentElement = (Element) studentNode;

                int studentID = Integer.parseInt(studentElement.getAttribute("ID"));

                if (studentID==id) {
                    // Update student details
                    updateStudentElement(studentElement, updatedStudent);

                    // Write the updated content into XML file
                    TransformerFactory transformerFactory = TransformerFactory.newInstance();
                    Transformer transformer = transformerFactory.newTransformer();
                    DOMSource source = new DOMSource(doc);
                    StreamResult result = new StreamResult(XML_FILE_PATH);
                    transformer.transform(source, result);
                    return "Student updated successfully!";


                }
            }



        }
        return "Failed!";
    }


    private void updateStudentElement (Element studentElement, student updatedStudent){
        // Update only the provided attributes
        if (updatedStudent.getFirstName() != null) {
            studentElement.getElementsByTagName("FirstName").item(0).setTextContent(updatedStudent.getFirstName());
        }
        if (updatedStudent.getLastName() != null) {
            studentElement.getElementsByTagName("LastName").item(0).setTextContent(updatedStudent.getLastName());
        }
        if (updatedStudent.getGender() != null) {
            studentElement.getElementsByTagName("Gender").item(0).setTextContent(updatedStudent.getGender());
        }
        if (updatedStudent.getGpa() >= 0) {
            studentElement.getElementsByTagName("GPA").item(0).setTextContent(String.valueOf(updatedStudent.getGpa()));
        }
        if (updatedStudent.getLevel() >= 0) {
            studentElement.getElementsByTagName("Level").item(0).setTextContent(String.valueOf(updatedStudent.getLevel()));
        }
        if (updatedStudent.getAddress() != null) {
            studentElement.getElementsByTagName("Address").item(0).setTextContent(updatedStudent.getAddress());
        }
    }


}
