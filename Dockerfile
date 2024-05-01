FROM amazoncorretto:17

WORKDIR /usr/app

COPY ./build/libs/backend-0.0.1.jar /usr/app/app.jar

CMD ["java", "-jar", "app.jar"]