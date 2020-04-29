FROM openjdk:8-jre-slim
ADD /target/raad-image-processing-0.0.1-SNAPSHOT.jar app.jar
RUN mkdir /tmp/tomcat static
ENTRYPOINT ["java","-jar","app.jar"]
EXPOSE 9096
