export JAVA_HOME=/opt/jdk-17.0.6+10/


#./mvnw clean

./mvnw package

$JAVA_HOME/bin/java -jar target/conference_management-0.0.1-SNAPSHOT.jar


# install before mysql server create user/pass conference_management/conference_management1 and run :) enjoy!
#
#
# mysql --user=root -p
#
# mysql> CREATE USER 'conference_management'@'localhost' IDENTIFIED BY 'conference_management1';
#
# mysql> GRANT ALL ON conference_management.* TO 'conference_management'@'localhost';
#
# mysql> FLUSH PRIVILEGES;
