package com.example.webservice_xml;

public class student {
        private  int id;
        private  String FirstName;
        private  String lastName;
        private  String gender;
        private  double gpa;
        private  int level;
        private  String address;





        public int getId() {
                return id;
        }

        public  String getFirstName() {
                return FirstName;
        }

        public  String getLastName() {
                return lastName;
        }

        public  String getGender() {
                return gender;
        }

        public double getGpa() {
                // Return GPA as a double
                return Double.parseDouble(String.valueOf(gpa));
        }

        public int getLevel() {
                // Return Level as an int
                return Integer.parseInt(String.valueOf(level));
        }

//        public String getLevel() {
//                return String.valueOf(level);
//        }

        public  String getAddress() {
                return address;
        }

        public void setId(int id) {
                this.id = id;
        }

        public  String setFirstName(String firstName) {
                FirstName = firstName;
                return FirstName;
        }

        public void setLastName(String lastName) {
                this.lastName = lastName;
        }

        public void setGender(String gender) {
                this.gender = gender;
        }

        public void setGpa(double gpa) {
                this.gpa = gpa;
        }

        public void setLevel(int level) {
                this.level = level;
        }

        public void setAddress(String address) {
                this.address = address;
        }
}