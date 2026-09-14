FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /build
COPY backend/ ./
RUN mvn -q install -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /build/target/chat-memory-app.jar app.jar

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
