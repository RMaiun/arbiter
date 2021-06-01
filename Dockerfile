FROM openjdk:16-slim
MAINTAINER @RMaiun
RUN apt-get update; apt-get install -y fontconfig libfreetype6
VOLUME /tmp
COPY server/build/libs/server.jar arbiter.jar
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /arbiter.jar ${@}"]