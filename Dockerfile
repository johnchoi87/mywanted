FROM adoptopenjdk/openjdk11
#FROM openjdk:8-jdk-alpine
CMD ["./mvnw", "clean", "package"]
ARG JAR_FILE_PATH=target/*.jar
COPY ${JAR_FILE_PATH} app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
