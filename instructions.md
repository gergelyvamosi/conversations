# Software Engineer - Home Assignment
Create a Spring Boot web application that facilitates 1on1 discussions between two
employees. 
We provided a [Spring Initializr configuration](https://start.spring.io/#!type=gradle-project&language=kotlin&platformVersion=3.4.0&packaging=jar&jvmVersion=16&groupId=com.getbridge&artifactId=homework&name=homework&description=&packageName=com.getbridge.homework&dependencies=web,devtools,data-jpa)
for your convenience. You can adjust the settings to your liking and select your preferred DB technology.
* Please work in this repository that we provided for you.
* Write as many tests as you feel are required for production-ready software, paying special attention to the business layer.
* Please document how to start the application in a README file in the project root.

In case the requirements are not specific enough feel free to go with a solution that makes most
sense for you. When in doubt, reach out to us.
## TASK A
Create a RESTful HTTP API where users can access the basic CRUD functionalities of the 1on1s.
1on1s have the following attributes: title, participants, planned date, description, location.
## TASK B
Extend your solution so that discussions can be concluded. A concluded discussion is read-only, so no further updates can be made to it.
## TASK C
Provide a search endpoint where 1on1s can be filtered by the follwing attributes: title (contains string),
planned date (between date range), state (closed or not).
## TASK D
Add authorization to the API endpoints so that users can only access their own 1on1s. We can
assume the authenticated user ID is available in the **X-AUTHENTICATED-USER** request header.
