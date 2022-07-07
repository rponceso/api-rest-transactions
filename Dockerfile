FROM openjdk:11.0.15-slim-buster

WORKDIR /app

COPY ./target/api-rest-transactions-0.0.1-SNAPSHOT.jar .

EXPOSE 8085

ENTRYPOINT ["java","-jar","api-rest-transactions-0.0.1-SNAPSHOT.jar"]

