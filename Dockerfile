# --- Giai đoạn 1: Build code dùng Maven ---
# Đổi sang image Maven chạy trên Eclipse Temurin cho ổn định
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# --- Giai đoạn 2: Chạy ứng dụng ---
# Đổi sang image Eclipse Temurin JRE (nhẹ và nhanh hơn)
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copy file .jar từ giai đoạn build sang
COPY --from=build /app/target/*.jar app.jar

# Mở cổng 8080
EXPOSE 8080

# Chạy ứng dụng
ENTRYPOINT ["java", "-jar", "app.jar"]