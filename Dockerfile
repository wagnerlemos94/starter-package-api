FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY . .

ARG GITHUB_TOKEN
ENV GITHUB_TOKEN=${GITHUB_TOKEN}

RUN mvn clean package --settings settings.xml -DskipTests

FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar


ENTRYPOINT ["java","-jar","app.jar"]