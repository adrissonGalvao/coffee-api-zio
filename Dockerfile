FROM adoptopenjdk/openjdk11:alpine-jre

COPY  ./app/target/scala-2.12/app-assembly-0.1.0-SNAPSHOT.jar /opt/app/app.jar

EXPOSE 8080

CMD ["java", "-jar", "/opt/app/app.jar"]