FROM maven:3.9.9-amazoncorretto

WORKDIR /app

COPY . .

RUN mvn clean package

FROM openjdk:21-rc-oracle

WORKDIR /app

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} /app/billetterie.jar

ENTRYPOINT ["java", "-jar", "/app/billetterie.jar"]