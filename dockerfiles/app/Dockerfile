FROM openjdk:21-jdk-slim
WORKDIR /app
COPY ./target/*.jar application.jar
ENTRYPOINT ["java", "-jar", "application.jar"]
