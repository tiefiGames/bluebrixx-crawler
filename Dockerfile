FROM adoptopenjdk/openjdk11
COPY target/bluebrixx-crawler-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "/app.jar"]
