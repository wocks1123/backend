FROM amazoncorretto:17

WORKDIR /usr/app

COPY ./build/libs/backend-*.jar /usr/app/app.jar

CMD ["java", "-jar", "-Dspring.profiles.active=deploy",  "app.jar"]
