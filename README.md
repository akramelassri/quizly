# quizly

A real-time quiz interactions web platform.

## 🚀 Technologies Used
This project is built using modern Enterprise Java technologies:
* **Jakarta EE 10** (Servlets 6.1, CDI 4.1, Faces 4.1)
* **Hibernate 7.0** (JPA implementation)
* **PostgreSQL** (Relational Database)
* **Maven** (Build and Project Management)
* **WildFly** (Application Server)

---

## 📁 Project Structure
The application follows a standard Maven directory layout for a Jakarta EE Web Application (WAR):

```text
quizly/
├── pom.xml                                   # Maven dependencies and build configuration
└── src/
    └── main/
        ├── java/com/example/quizly/          # Java source code (Servlets, Utils, Entities, etc.)
        ├── resources/
        │   └── META-INF/
        │       └── persistence.xml           # Database/JPA connection settings
        └── webapp/
            ├── index.jsp                     # Frontend views
            └── WEB-INF/
                ├── web.xml                   # Web application deployment descriptor
                └── faces-config.xml          # JSF configuration
```

---

## ⚙️ How to Configure

Before running the application, you need to configure your database connection. 

### 1. Database Setup
Ensure you have **PostgreSQL** installed and running on your local machine. You need to create a database for the application to connect to. In your terminal or database client (e.g. pgAdmin, DBeaver), run:
```sql
CREATE DATABASE quizly_db;
```

### 2. Update `persistence.xml`
Open the `src/main/resources/META-INF/persistence.xml` file and update the following properties to match your PostgreSQL setup:

* **JDBC URL:** Adjust the port and database name if different.
  ```xml
  <property name="jakarta.persistence.jdbc.url" value="jdbc:postgresql://localhost:5432/quizly_db" />
  ```
* **Database Username:** Replace `username` with your local postgres user (default is usually `postgres`).
  ```xml
  <property name="jakarta.persistence.jdbc.user" value="username" />
  ```
* **Database Password:** Replace `your_password` with your local database password.
  ```xml
  <property name="jakarta.persistence.jdbc.password" value="your_password" />
  ```

---

## 🏃 Running the Application locally

With your database configured, you can build and run the application locally.

1. Ensure your terminal has **Java 21** active. 
   *(Note: WildFly might fail to boot if using Java 24 or 25 due to Security Manager restrictions).*
   ```bash
   export JAVA_HOME=/usr/lib/jvm/java-21-openjdk
   export PATH=$JAVA_HOME/bin:$PATH
   ```

2. Clean and compile the project:
   ```bash
   mvn clean install -U
   ```

3. Run the application using the WildFly Plugin:
   ```bash
   mvn compile wildfly:run
   ```

4. Once the server starts, access the application at:
   - Root page: `http://localhost:8080/quizly-1.0-SNAPSHOT/`
   - Test Servlet: `http://localhost:8080/quizly-1.0-SNAPSHOT/hello-servlet`
