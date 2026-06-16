# Stage 1: Build mã nguồn mã Java bằng Maven
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
# Download trước các dependency để cache, tăng tốc build lần sau
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Tạo image chạy ứng dụng (chỉ giữ lại file .jar)
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Tạo sẵn thư mục chứa file crawl bên trong container
RUN mkdir -p /app/documents

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
