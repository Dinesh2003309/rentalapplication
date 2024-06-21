FROM maven:3.8.4-openjdk-17

WORKDIR /app

COPY . /app

RUN mvn clean install

CMD ["java", "-jar", "target/rental_service-0.0.1-SNAPSHOT.jar"]