# SmartHealthManagementSystem
The Smart Health Management System is a web-based application designed to streamline and automate healthcare operations. The system allows for efficient management of patient health records, doctor appointments, feedback, and medical records, ensuring that all parties (patients, doctors, nurses, and administrators) can access and manage information in a user-friendly environment.

Features:
User Authentication & Authorization: Secure login and access management based on roles (Admin, Doctor, Patient, Nurse).
Doctor Management: Admin can manage doctor profiles, assign specialties, and allow doctors to view and update patient information.
Patient Management: Admin can manage patient profiles, including personal information and medical records.
Appointment Management: Patients can book appointments with doctors, and doctors can view and update patient appointments.
Feedback Management: Patients can provide feedback on their consultations with doctors.
Medical Records Management: Manage medical records, including diagnosis, treatment, and notes from doctors.
Secure API Endpoints: All endpoints are secured with JWT authentication for authorized access.
Technologies Used
Java: Backend programming language.
Spring Boot: Framework for building RESTful APIs.
Spring Security: For securing endpoints and implementing role-based access control.
JWT (JSON Web Tokens): For token-based authentication.
JPA (Java Persistence API): To interact with the database and manage entity relationships.
MySQL: Relational database for data storage.
JUnit & Mockito: For unit testing and mocking dependencies.
Getting Started
To run the project locally, follow these steps:

Prerequisites
Java 8 or higher.
Maven.
MySQL or any other relational database of your choice.
Steps to Run Locally
Clone the repository:

bash
Copy code
git clone https://github.com/your-username/smart-health-management-system.git
cd smart-health-management-system
Set up the MySQL Database:

Create a new database named smart_health_management_db or modify the application.properties to match your database settings.
Import the provided SQL script for the initial schema setup (if available).
Configure application.properties:

Set up your database connection by modifying src/main/resources/application.properties:
properties
Copy code
spring.datasource.url=jdbc:mysql://localhost:3306/smart_health_management_db
spring.datasource.username=root
spring.datasource.password=yourpassword
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
Build the Project:

Use Maven to build the project:
bash
Copy code
mvn clean install
Run the Application:

Run the application:
bash
Copy code
mvn spring-boot:run
The application should now be running on http://localhost:8080.
API End Points
UserController
To Register a user Endpoint
localhost:8008/api/auth/register

Login Endpoint 
localhost:8008/api/auth/login

Logout Endpoint
localhost:8008/api/auth/logout


DoctorController
http://localhost:8008/api/auth/login/doctors/getalldoctors  
http://localhost:8008/api/auth/login/doctors/2 
http://localhost:8008/api/auth/login/doctors/1/patients/1 
http://localhost:8008/api/auth/login/doctors/doctor/1   

PatientController
http://localhost:8008/api/auth/login/Patients/getAllPatients  
http://localhost:8008/api/auth/login/Patients/1     
http://localhost:8008/api/auth/login/Patients/addmedicalreport 
http://localhost:8008/api/auth/login/Patients/feedback/doctor/1  
http://localhost:8008/api/auth/login/Patients/feedback/doctor/1 
