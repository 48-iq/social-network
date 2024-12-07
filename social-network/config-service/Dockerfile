FROM maven:3.9.9-eclipse-temurin-21-jammy AS build
WORKDIR /app
COPY pom.xml ./
RUN mvn dependency:resolve

COPY /src ./src
RUN mvn package -DskipTests

FROM eclipse-temurin:21-jre-jammy AS prodaction
COPY --from=build /app/target/config-service-*.jar /config-service.jar
EXPOSE 8888
CMD ["java", "-jar", "config-service.jar"]