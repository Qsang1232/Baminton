# Build stage
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Run stage
# --- SỬA DÒNG NÀY ---
# Thay openjdk:17-jdk-slim bằng eclipse-temurin:17-jdk-alpine
FROM eclipse-temurin:17-jdk-alpine
# --------------------

WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]