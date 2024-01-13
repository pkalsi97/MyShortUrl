FROM openjdk:17-jdk-alpine

WORKDIR /usr/src/myapp

COPY target/MyShortUrl-0.0.1-SNAPSHOT.jar /usr/src/myapp

EXPOSE 8080

CMD ["java", "-jar", "MyShortUrl-0.0.1-SNAPSHOT.jar"]