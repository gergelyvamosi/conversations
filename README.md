# A Spring Boot web application that facilitates 1on1 discussions between two employees.

2025-05-17 approx 14h

Client in Angular version 19.2.12 build with npm 10.9.2 and node-v22.15.0-linux-x64

Server Spring Boot version 3.2.3

## Cheers Fellows,


## to run:

0) install/use a mysql server and create user and credentials (user/pass = conference_management/conference_management1)

  CREATE USER 'conference_management'@'localhost' IDENTIFIED BY 'conference_management1';

  GRANT ALL ON conference_management.* TO 'conference_management'@'localhost';

  FLUSH PRIVILEGES;

1) checkout github repo in a local folder

2) to build:

  export JAVA_HOME=/opt/jdk-17.0.6+10/

  ./mvnw package

3) to run:

  $JAVA_HOME/bin/java -jar target/conference_management-0.0.1-SNAPSHOT.jar


## to test from cmd line (e.g.):

  curl --header "X-Authenticated-User: root" http://localhost:8080/users

  curl --header "X-Authenticated-User: root" -X POST -H "Content-Type: text/plain" -d '' http://localhost:8080/conferences/add/1/users/2
  
  curl --header "X-Authenticated-User: root" -X GET -H "Content-Type: application/json" http://localhost:8080/conversations/filter?title=a
  
  #curl --header "X-Authenticated-User: root" -X POST -H "Content-Type: application/json" -d '{"title":"a"}' http://localhost:8080/conversations/filter


## to use the webapp from browser:

  http://localhost:8080/

  you can also test with two different users in a firefox and a chrome browser on one machine. just chat 1on1s on one computer.



## KNOWN bugs

* data refresh (lists) goes on client side not all the time - mostly on second click works :) (possibly using effects is to dőlve). the implementation of the functionality the original specification is at 97%. completion is at almost 100%. some bugs still in.
* cookie JSESSIONID gets stuck on tomcat - no logout implemented - clear cookies in browser if use a different user : !! UPDATE 2025-05-17 19:30 logout() added !!


## to complete the task was totally 4 days 33hrs,

* 1st day considerations / concept of the development 4 hrs,

* 2nd day implementation spring boot JAVA 17 / business layer / mysql db 10 hrs,

* 3rd day implementation Angular 19 client 11hrs

* 4th day implementation Angular 19 client, misc, debug, fine tune 7hrs + 1hr logout
