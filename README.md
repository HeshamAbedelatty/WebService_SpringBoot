# WebService_SpringBoot
write a web application that do the following: For the following student structure, build an XML document and then retrieve data from it.
1- Build an XML document. 
2- Ask the user to enter the number of students s/he wants to store data about. 
3- Take student data from the user. 
4- Store the data in the XML file using the previous structure. 
5- Give the user the ability to search for a specific GPA or FirstName and retrieve the search result from the XML file. 
6- Choose any record to be deleted.
1- Add a Student 2 grades (all attributes id, FirstName …etc) [don’t generate any attribute like ID the user must provide all the details] and validate the entered data before saving it to the file
a. All attributes can’t be null / empty.
b. ID must not exist before (NO DUBLICATES).
c. Student name (first name and last name), address is characters (a-z) only.
d. GPA must be from 0 to 4.
2- Update Student details 2 grades
a. User can update some or all the desired student attributes.
b. ID field can’t be updated.
c. Any details the user will not provide will be preserved as is
i. For example, if the user changed only the student’s first Name it means all the attributes from the student are still the same except the student’s first Name
3- Give the user the ability to search for a student using any of the attributes 2 grades
a. He can search with any of the fields so all filters must be implemented
b. Number of found students must be displayed
c. All matching students are retrieved and displayed from the xml file.
4- Sort the file using any of the student attributes like (ID, FirstName …etc) in ascending or descending order based on the received input from the user and display the content 3 grades
5- Save the sorted file replacing the old file content.
