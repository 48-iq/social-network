FROM maven:3.9.9-eclipse-temurin-21-jammy AS build
WORKDIR ./app
COPY pom.xml ./
RUN mvn dependency:resolve

COPY /src ./src
RUN mvn package

FROM eclipse-temurin:21-jre-jammy AS prodaction
COPY --from=build /app/target/discovery-service-*.jar /discovery-service.jar
EXPOSE 8080
CMD ["java", "-jar", "discovery-service.jar"]