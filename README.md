# verify links
This is a program that verifies that all links in the .csv file are active by performing a GET request for each link and expecting a 200 OK response.

### How to run
#### Prerequisite:
- [JRE 11](https://www.oracle.com/cis/java/technologies/javase/jdk11-archive-downloads.html)

#### Steps:
1. `./mvnw clean package`
2. `java -jar target/verifylinks-jar-with-dependencies.jar test-data/links.csv`