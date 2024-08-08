Cavany Project

Overview
Cavany is a Java-based application that leverages the Spring Boot framework. The project integrates with the Spoonacular API to provide meal preparation services. Additionally, it uses Neo4j as its database for storing and managing recipe and meal plan data. The application is structured as a Maven project and includes various features and modules to manage recipes, meal plans, and other related functionalities.

Project Structure
    - src/: Contains the source code of the application.
    - main/java/: Java source files.
    - main/resources/: Configuration files and templates.
    - test/java/: This directory would normally contain test cases, but no tests are currently included in this project.
    - spoonacular/: Contains code and resources related to the Spoonacular API integration.
    - pom.xml: Maven project descriptor file. It includes dependencies, plugins, and other build configurations.
    - target/: Maven's output directory where compiled classes, jars, and other build results are stored.
    - .gitignore: Specifies files and directories that should be ignored by Git.
    - mvnw, mvnw.cmd: Maven wrapper scripts that allow building the project without requiring Maven to be installed.

Prerequisites
    - Java 11 or later
    - Maven 3.6.3 or later
    - Git for version control
    - Neo4j Database (version 4.x or later)

Setup and Installation
    1.  Clone the Repository: git clone https://github.com/yourusername/cavany.git
    
    2.  Configure Neo4j: Ensure that you have a running instance of Neo4j. You can download and install it from the official website. Once installed, update the following properties in the application.properties file located in the src/main/resources directory:
        spring.neo4j.uri=bolt://localhost:7687
        spring.neo4j.authentication.username=neo4j
        spring.neo4j.authentication.password=your_password
        (Replace your_password with the password for your Neo4j instance.)

    3.  Build the Project: Use the Maven wrapper to build the project: ./mvnw clean install
    Even though no tests are present in the project, the build will complete successfully.

    4. Run the Application: You can run the application using the Spring Boot Maven plugin: ./mvnw spring-boot:run

Configuration
The application requires configuration for accessing the Spoonacular API and connecting to the Neo4j database. Make sure to set the API key in the application properties as well as the Neo4j connection details.

Features
    - Recipe Management: Add, update, delete, and search recipes.
    - Meal Planning: Create and manage meal plans.
    - API Integration: Fetch data from the Spoonacular API.
    - Neo4j Integration: Store and manage recipe and meal plan data using Neo4j.

Contributing
Contributions are welcome! Please fork the repository and submit a pull request.