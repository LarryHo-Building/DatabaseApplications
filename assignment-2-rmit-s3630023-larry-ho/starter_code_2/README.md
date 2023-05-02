# DBA  Starter Code
Starter code for the DBA Project. This is a simple layout connected to a GitHub Classroom that serves as the basis for implementing the DBA project. 

This example program provides:

* A Java class for the Index page (index.html)
* 6x Java classes for 6 pages. Additional pages can be added by adding additional classes
* MongoDBConnection Java class, that uses the AirBnB Database.
    * You will need to update the DATABASE_URL string in MongoDBConnection to your Atlas Mongodb Compass String
    *  format: mongodb+srv://username:password@atlas.server.address/database
    *  Goto https://cloud.mongodb.com/ 
    *  click connect > connect your application > Driver Java > Version 4.3 or later
    *  copy connection string
    *  if you have time out issues (due to firewalls) choose verion 3.3 or earlier instead
    * 
    * You should replace the example method in MongoDBConnection with the required MongoDB Queries to support your web page
* App Java class to configure and setup the Javalin web server. 
    * You will need to uncomment the appropriate lines in ```configureRoutes()``` for any webpages that you need to power through a web form.
* Examples CSS (common.css) file in the resources directory
* Example image (logo.png) file in the resources directory with where to locate any images you want on your website

Classes backing Web pages:
```bash
├── Index                         - Homepages/index page. Provides a directory to all other pages
├── Page1                         - Returns a list of all movies.
├── Page2                         - Returns movies by specific user entered type - using basic HTML
├── Page3                         - Returns movies by specific user entered type - using Thymeleaf
├── Page4                         - Same as page 1
├── Page5                         - Same as page 1                       
└── Page6                         - Same as page 1
```

Other Classes:
```bash
├── App                           - Main Application entrypoint for Javalin
└── MongoDBConnection             - Example MongoDB Connection class 
```

Folders:
```bash
├── /src/main                     - Location of all files as required by MAVEN build
│         ├── java                - Java Source location
│         │    └── app            - package location for all Java files
│         └── resources           - Web resources (html templates / style sheets)
│               ├── css           - CSS Style-sheets. Base example style sheet (common.css) provided
│               └── images        - Image files. Base example image (RMIT Logo) provided
│ 
├── /target                       - build directory (DO NOT MODIFY)
├── pom.xml                       - Configure Build (DO NOT MODIFY)
└── README.md                     - This file ;)
```

Current Libraries
* javalin (lightweight java webserver)
* thymeleaf (HTML template) - https://www.thymeleaf.org/doc/tutorials/3.0/usingthymeleaf.html
* mongodb

Libraries required as dependencies
* By javalin
   * slf4j-simple (lightweight logging)

# Building & Running the code
1. Open this project within VSCode
2. Allow VSCode to read the Maven pom.xml file
 - Allow the popups to run and "say yes" to VSCode configuring Maven
 - Allow VSCode to download the required Java libraries
 - Edit MongoDBConnection.java to update the "DATABASE_URL" variable to match your atlas mongodb connection string
3. To Build & Run
 - Open the src/main/java/app/App.java source file, and select "Run" from the pop-up above the main function
4. Go to: http://localhost:7000
5. Press ctrl-c in console to stop server

# Authors
* Dr. Timothy Wiley, School of Computing Technologies, STEM College, RMIT University.
* Prof. Santha Sumanasekara, School of Computing Technologies, STEM College, RMIT University.
* Dr. Halil Ali, School of Computing Technologies, STEM College, RMIT University.

Copyright RMIT University (c) 2021

