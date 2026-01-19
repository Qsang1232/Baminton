# Build stage
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Run stage (Dùng Jammy/Ubuntu để fix lỗi DNS)
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
# Giới hạn RAM 350MB để không bị sập trên Render Free
ENTRYPOINT ["java", "-Xms350m", "-Xmx350m", "-jar", "app.jar"]