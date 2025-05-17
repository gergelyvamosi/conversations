A Spring Boot web application that facilitates 1on1 discussions between two employees.

Cheers Fellows,


to run:

1) checkout github repo in a local folder
2) to build:
  export JAVA_HOME=/opt/jdk-17.0.6+10/
  ./mvnw package
3) to run:
  $JAVA_HOME/bin/java -jar target/conference_management-0.0.1-SNAPSHOT.jar


to test from cmd line (e.g.):

  curl --header "X-Authenticated-User: root" http://localhost:8080/users
  curl --header "X-Authenticated-User: root" -X POST -H "Content-Type: text/plain" -d '' http://localhost:8080/conferences/add/1/users/2
  curl --header "X-Authenticated-User: root" -X GET -H "Content-Type: application/json" http://localhost:8080/conversations/filter?title=a
  curl --header "X-Authenticated-User: root" -X POST -H "Content-Type: application/json" -d '{"title":"a"}' http://localhost:8080/conversations/filter

to use the webapp from browser:

  http://localhost:8080/index.html

you can also test with two different users in a firefox and a chrome browser on one machine. just chat 1on1s on one computer.


KNOWN bugs

  data refresh (lists) goes on client side not all the time. the implementation of the functionality the original specification is at 97%.


to complete the task was totally 4 days 32hrs,
  1st day considerations / concept of the development 4 hrs,
  2nd day implementation spring boot JAVA 17 / business layer / mysql db 10 hrs,
  3rd day implementation Angular 19 client 11hrs
  4th day implementation Angular 19 client, misc, debug, fine tune 7hrs
