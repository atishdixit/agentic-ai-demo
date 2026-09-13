FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn -q -o -DskipTests package || mvn -q -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/agentic-ai-demo-backend-*.jar app.jar

EXPOSE 8000
CMD ["java", "-jar", "app.jar"]
