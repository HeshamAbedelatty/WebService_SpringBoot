public class student {


    Next, create the following files:

            1. `Student.java` - A Java class representing the student entity.
            2. `StudentController.java` - A Spring MVC controller that handles the HTTP requests.
3. `index.html` - An HTML file for the main page.
4. `main.js` - A JavaScript file for handling user interactions and making requests to the server.
5. `style.css` - A CSS file for styling the HTML elements.

    Here's an example implementation:

            **Student.java**
            ```java
    public class Student {
        private String id;
        private String firstName;
        private String lastName;
        private String gender;
        private double gpa;
        private int level;
        private String address;

        // Getters and setters
        // ...
    }
```

        **StudentController.java**
            ```java
import org.springframework.web.bind.annotation.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

    @RestController
    @RequestMapping("/students")
    public class StudentController {

        private static final String XML_FILE_PATH = "students.xml";

        @PostMapping("/")
        public String addStudent(@RequestBody Student student) {
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

                Element studentElement = doc.createElement("Student");
                studentElement.setAttribute("ID", student.getId());
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

                // Write the content into XML file
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

        @GetMapping("/")
        public List<Student> searchStudents(@RequestParam(required = false) String gpa, @RequestParam(required = false) String firstName) {
            List<Student> students = new ArrayList<>();

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
                        String studentFirstName = studentElement.getElementsByTagName("FirstName").item(0).getTextContent();

                        if ((gpa == null || studentGpa.equals(gpa)) && (firstName == null || studentFirstName.equals(firstName))) {
                            students.add(parseStudentFromElement(studentElement));
                        }
                    }
                }
            } catch (ParserConfigurationException | SAXException | IOException e) {
                e.printStackTrace();
            }

            return students;
        }

        @DeleteMapping("/{id}")
        public String deleteStudent(@PathVariable String id) {
            try {
                DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

                Document doc = docBuilder.parse(XML_FILE_PATH);

                NodeList studentNodes = doc.getElementsByTagName("Student");

                for (int i = 0; i < studentNodes.getLength(); i++) {
                    Node studentNode = studentNodes.item(i);

                    if (studentNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element studentElement = (Element) studentNode;

                        String studentId = studentElement.getAttribute("ID");

                        if (studentId.equals(id)) {
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
            } catch (ParserConfigurationException | SAXException | IOException | TransformerException e) {
                e.printStackTrace();
            }

            return "Failed to delete student.";
        }

        private Student parseStudentFromElement(Element studentElement) {
            Student student = new Student();

            student.setId(studentElement.getAttribute("ID"));
            student.setFirstName(studentElement.getElementsByTagName("FirstName").item(0).getTextContent());
            student.setLastName(studentElement.getElementsByTagName("LastName").item(0).getTextContent());
            student.setGender(studentElement.getElementsByTagName("Gender").item(0).getTextContent());
            student.setGpa(Double.parseDouble(studentElement.getElementsByTagName("GPA").item(0).getTextContent()));
            student.setLevel(Integer.parseInt(studentElement.getElementsByTagName("Level").item(0).getTextContent()));
            student.setAddress(studentElement.getElementsByTagName("Address").item(0).getTextContent());

            return student;
        }
    }
```

        **index.html**
            ```html
            <!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Student Management</title>
    <link rel="stylesheet" type="text/css" href="style.css">
</head>
<body>
    <h1>Student Management</h1>
    <div>
        <label for="gpa">Search by GPA:</label>
        <input type="number" id="gpa" step="0.01">
        <button onclick="searchByGpa()">Search</button>
    </div>
    <div>
        <label for="firstName">Search by First Name:</label>
        <input type="text" id="firstName">
        <button onclick="searchByFirstName()">Search</button>
    </div>
    <div>
        <label for="deleteId">Enter ID to delete:</label>
        <input type="text" id="deleteId">
        <button onclick="deleteStudent()">Delete</button>
    </div>
    <div id="result"></div>
    <script src="main.js"></script>
</body>
</html>
            ```

            **main.js**
            ```javascript
    function searchByGpa() {
    const gpa = document.getElementById("gpa").value;
        fetch(`/students/?gpa=${gpa}`)
        .then(response => response.json())
        .then(students => {
                displayStudents(students);
        })
        .catch(error => {
                console.error("Error:", error);
        });
    }

    function searchByFirstName() {
    const firstName = document.getElementById("firstName").value;
        fetch(`/students/?firstName=${firstName}`)
        .then(response => response.json())
        .then(students => {
                displayStudents(students);
        })
        .catch(error => {
                console.error("Error:", error);
        });
    }

    function deleteStudent() {
    const id = document.getElementById("deleteId").value;
        fetch(`/students/${id}`, { method: 'DELETE' })
        .then(response => response.text())
        .then(message => {
                console.log(message);
        })
        .catch(error => {
                console.error("Error:", error);
        });
    }

    function displayStudents(students) {
    const resultDiv = document.getElementById("result");
        resultDiv.innerHTML = "";

        if (students.length === 0) {
            resultDiv.innerHTML = "No students found.";
            return;
        }

    const table = document.createElement("table");
    const headerRow = document.createElement("tr");
    const idHeader = document.createElement("th");
        idHeader.textContent = "ID";
    const firstNameHeader = document.createElement("th");
        firstNameHeader.textContent = "First Name";
    const lastNameHeader = document.createElement("th");
        lastNameHeader.textContent = "Last Name";
    const genderHeader = document.createElement("th");
}
