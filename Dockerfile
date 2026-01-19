# --- Giai đoạn 1: Build code ---
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# --- Giai đoạn 2: Chạy ứng dụng ---
FROM openjdk:17-jdk-slim
WORKDIR /app
# Copy file .jar từ giai đoạn build sang giai đoạn chạy
COPY --from=build /app/target/*.jar app.jar

# Mở cổng 8080 (Render sẽ ánh xạ cổng này)
EXPOSE 8080

# Lệnh chạyyyyyy
ENTRYPOINT ["java", "-jar", "app.jar"]