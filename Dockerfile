FROM openjdk:17
LABEL authors="zaz"
VOLUME /tmp
ADD target/auth-service.jar auth-service.jar
ENTRYPOINT [ "java","-jar","/auth-service.jar" ]