FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

COPY .mvn/ .mvn
COPY mvnw pom.xml ./
COPY src ./src

# Add executable permissions to mvnw
RUN chmod +x mvnw

# Package the application
RUN ./mvnw package -DskipTests

CMD ["java", "-jar", "target/social_media_be-0.0.1-SNAPSHOT.jar"]
