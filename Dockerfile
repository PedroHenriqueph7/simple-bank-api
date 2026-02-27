FROM eclipse-temurin:21
LABEL maintainer=${MAINTAINER}
WORKDIR /app
COPY target/simple-bank-api-0.0.1-SNAPSHOT.jar /app/simple-bank-api.jar
ENTRYPOINT ["java", "-jar", "simple-bank-api.jar"]