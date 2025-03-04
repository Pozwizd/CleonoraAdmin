
FROM mcr.microsoft.com/openjdk/jdk:17-windowsservercore-ltsc2022

WORKDIR /app

COPY build/libs/*.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
