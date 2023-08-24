FROM gradle:7-jdk11 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle buildFatJar --no-daemon

FROM openjdk:17
EXPOSE 8080:8080
RUN mkdir /app
ENV EMBEDDED true
COPY --from=build /home/gradle/src/build/libs/*.jar /app/inventy-backend.jar
ENTRYPOINT ["java","-jar","/app/inventy-backend.jar"]