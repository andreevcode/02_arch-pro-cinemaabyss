# --- Stage 1: Build the app using Gradle ---
FROM gradle:8.4-jdk17 AS builder

WORKDIR /app

# Кэшируем зависимости отдельно
COPY build.gradle.kts settings.gradle.kts ./
COPY gradle ./gradle
RUN gradle dependencies --no-daemon || true

# Копируем остальной код и собираем jar
COPY . .
RUN ./gradlew bootJar --no-daemon

# --- Stage 2: Run the built jar with lightweight image ---
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Копируем jar из builder-стейджа
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]