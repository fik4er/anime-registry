# Dockerfile
FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

# Копируем JAR файл из target в образ
COPY target/anime-registry-app-0.0.1-SNAPSHOT.jar app.jar

# Указываем команду запуска
ENTRYPOINT ["java", "-jar", "app.jar"]