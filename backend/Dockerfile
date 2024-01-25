FROM openjdk:17

ARG JAR_FILE=drwa-0.0.1-SNAPSHOT.jar

COPY build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]