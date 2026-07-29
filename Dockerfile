FROM maven:3.9.11-eclipse-temurin-25 AS build

WORKDIR /app

COPY . .

RUN mvn clean package -DskipTests


FROM eclipse-temurin:25-jdk-jammy

WORKDIR /app




RUN apt-get update && apt-get install -y \
    fontconfig \
    libfreetype6 \
    && rm -rf /var/lib/apt/lists/*

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]