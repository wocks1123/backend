FROM amazoncorretto:17

WORKDIR /usr/app

COPY ./build/libs/demo-0.0.1-SNAPSHOT.jar /usr/app/app.jar

CMD ["java", "-jar", "app.jar"]