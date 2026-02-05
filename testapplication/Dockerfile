# Stage 1: Build the application using the Gradle Wrapper
FROM eclipse-temurin:20-jdk AS build
COPY . /home/gradle/src
WORKDIR /home/gradle/src
RUN ./gradlew build --no-daemon

# Stage 2: Run the built artifact
FROM eclipse-temurin:20-jre

EXPOSE 8080
RUN mkdir /app

COPY --from=build /home/gradle/src/build/libs/*.jar /app/book-loan-app-1.0.0.jar

ENTRYPOINT ["java", "-jar", "/app/book-loan-app-1.0.0.jar"]
