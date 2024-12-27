FROM openjdk:21-slim
RUN addgroup khabar && adduser --ingroup khabar khabar
USER khabar:khabar
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]