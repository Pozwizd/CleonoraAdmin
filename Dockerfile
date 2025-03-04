
FROM mcr.microsoft.com/devcontainers/java:dev-17

WORKDIR /app

COPY build/libs/*.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
