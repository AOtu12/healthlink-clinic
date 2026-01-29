✅ HEALTHLINK CLINIC SYSTEM – RUNNING INSTRUCTIONS

Welcome!
Follow the steps below to run the HealthLink Clinic System on your computer.
No programming experience is needed.

1️⃣ Download Required Software

Please install these **three free tools:

1. MySQL Server 8.0

Download:
[https://dev.mysql.com/downloads/mysql/](https://dev.mysql.com/downloads/mysql/)

During installation:

* Set username: `root`
* Set password: `root` (or any password you remember)

2. MySQL Workbench

Download:
[https://dev.mysql.com/downloads/workbench/](https://dev.mysql.com/downloads/workbench/)

3. Java JDK 17

Download:
[https://www.oracle.com/java/technologies/javase-jdk17-downloads.html](https://www.oracle.com/java/technologies/javase-jdk17-downloads.html)

4. IntelliJ IDEA Community (or Eclipse)

Download:
[https://www.jetbrains.com/idea/download/](https://www.jetbrains.com/idea/download/)

You will use IntelliJ to open and run the project.

 2️⃣ Import the Project

1. Unzip the project folder sent to you
2. Open IntelliJ IDEA
3. Click File → Open → Select the project folder
4. IntelliJ will auto-detect it as a Spring Boot / Maven project
5. Let Maven finish downloading dependencies (may take 1–2 minutes)

3️⃣ Create the Database

The project needs a MySQL database called **`healthlink_clinic`.
We already provide the full SQL script.

You only need to copy and paste the script into MySQL Workbench.

Step-by-Step:

1. Open MySQL Workbench
2. Connect to your server
   (Click "Local Instance MySQL")
3. In the toolbar click:
   File → New Query Tab
4. Open the provided file → `healthlink_clinic.sql`
5. Select everything (Ctrl + A → Ctrl + C)
6. Paste it into the query tab in Workbench
7. Click the ⚡ Lightning Bolt(Execute)

✔ This will automatically create:

* all tables
* all sample data
* all user accounts
* foreign keys
* constraints
* doctor data
* patient data
* appointments
* preferences

Nothing else is required.

 4️⃣ Verify the Database Was Created

In Workbench:

On the left, right-click Schemas
 Click Refresh
 You should now see:
 healthlink_clinic

Click it and expand the tables:

* appointments
* doctors
* patients
* users
* user_preferences

5️⃣ Configure the Application

Inside the project folder, open:

src/main/resources/application.properties


Make sure these match your MySQL setup:

spring.datasource.url=jdbc:mysql://localhost:3306/healthlink_clinic
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD


Replace `YOUR_PASSWORD` with your actual MySQL password.

 6️⃣ Run the Application**

In IntelliJ:

1. Open the file:


src/main/java/com/healthlink/clinicsystem/HealthlinkClinicApplication.java


2. Click the green ▶ Run button

The system will start at:

👉 [http://localhost:8080](http://localhost:8080)
Go to http://localhost:8080/login in browser.

 7️⃣ Login Credentials

Your project includes sample accounts from the SQL script:

*ADMIN

username: admin
password: admin123
role: ADMIN

*RECEPTIONIST

username: reception
password: password
role: RECEPTIONIST

*DOCTOR

username: doctor
password:password
role: DOCTOR

(You may update these in the script if needed.)

 8️⃣ Project Features Provided

 JAVA FEATURES
 1. jwt based secure authentication
 2. Doctor and Patient CRUD mgt
 3. Dynamic Appointment booking
 4. Appointment lifecycle mgt(rescheduling and cancelling)
 5. Admin Dashboard Data Aggregation
 6. Admin user Management Interface
 7. Database Persistence with Spring Data JPA & MySQL
 Bonus 8. Automated API Documentation with Swagger/OpenAPI
 Integration: Offers the full interface contract of all REST controllers which is
 the refined end result of the backend development.

 http://localhost:8080/swagger-ui/index.html for Swagger

 Software Design & Development Features
 1. Role-Based Dashboard Views
 ▪ Receptionist: patient/booking administration.
 ▪ Doctor: view daily schedule, make mark for completed/no
 show
 ▪ Admin: control physicians, statistics.
 2. Advanced Search and Filtering System
 3. The Preferences of User & Accessibility Settings
 4. Responsive UI with the Bootstrap framework.
    o Purpose: Be available on desktops, tablets and phones.
 5.  Doctor Online/Offline Status Badges
 6. Data Export Functionality
 7. Doctor Joke Widget

9️⃣ If Something Goes Wrong

❗ ERROR: "Cannot connect to database"

Check:

application.properties

Ensure username/password are correct.

 ❗ ERROR: "healthlink_clinic does not exist"

You did NOT run the SQL script.
Return to Step 3 and run it.

❗ ERROR: Port 8080 already in use

Close:

* VS Code Live Server
* Another Spring Boot app
* Skype or Teams (sometimes)

Then try again.

🔟 Done!

