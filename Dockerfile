FROM openjdk:19
ARG JAR_FILE=/target/*.jar
COPY ${JAR_FILE} SocialMedia.jar
ENTRYPOINT ["java","-jar","/SocialMedia.jar"]