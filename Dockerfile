FROM maven:3.6.3-jdk-11-slim AS MAVEN_BUILD

ARG SPRING_ACTIVE_PROFILE

MAINTAINER Ben

COPY pom.xml /build/

COPY src /build/src/

WORKDIR /build/

RUN mvn clean package -B

FROM openjdk:11-slim

WORKDIR /tmp

COPY --from=MAVEN_BUILD /build/target/*.jar sample-1.0.1-SNAPSHOT.jar

ENTRYPOINT ["java", "-jar", "sample-1.0.1-SNAPSHOT.jar"]
