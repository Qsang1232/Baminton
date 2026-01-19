# --- Stage 1: Build (Biên dịch code) ---
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# --- Stage 2: Run (Chạy ứng dụng) ---
# Dùng bản Jammy (Ubuntu) để khắc phục lỗi không tìm thấy Database (DNS error)
FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

# Chạy ứng dụng với giới hạn RAM 350MB (Rất quan trọng cho gói Free của Render)
# Giúp tránh lỗi "Out of Memory" vì gói Free chỉ có 512MB RAM
ENTRYPOINT ["java", "-Xms350m", "-Xmx350m", "-jar", "app.jar"]