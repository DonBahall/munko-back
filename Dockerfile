FROM maven:3.9.2 AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package

FROM openjdk:18
COPY --from=build /home/app/target/munko-back-0.0.1-SNAPSHOT.jar /usr/local/lib/store.jar
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/usr/local/lib/store.jar"]