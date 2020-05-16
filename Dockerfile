FROM amazoncorretto:11
EXPOSE 8080
ADD /build/libs/demo-0.0.1-SNAPSHOT.jar demo.jar
ENTRYPOINT ["java", "-jar", "demo.jar"]