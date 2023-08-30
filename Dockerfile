FROM gradle:7-jdk17 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle buildFatJar --no-daemon

FROM openjdk:17
EXPOSE 8080:8080
RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/*.jar /app/inventy-backend.jar
COPY --from=build /home/gradle/src/src/main/resources/application.yaml /app/application.yaml
COPY --from=build /home/gradle/src/src/main/resources/application-test.yaml /app/application-test.yaml
ENTRYPOINT ["java","-jar","/app/inventy-backend.jar","-config=/app/application.yaml", "-config=/app/application-test.yaml"]