# Sử dụng image JDK 17 làm base
FROM openjdk:17-jdk-slim

# Thiết lập thư mục làm việc trong container
WORKDIR /app

# Copy file jar của ứng dụng vào container
COPY target/BookShop-0.0.1-SNAPSHOT.jar /app/myapp.jar

# Chạy ứng dụng
ENTRYPOINT ["java", "-jar", "/app/myapp.jar"]
