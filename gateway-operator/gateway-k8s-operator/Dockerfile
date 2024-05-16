FROM openjdk:17-jdk-slim
VOLUME /tmp

ARG JAR_FILE
ADD ${JAR_FILE} app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-Dfile.encoding=UTF-8", "--add-opens=java.base/java.lang=ALL-UNNAMED", "--add-opens=java.base/java.util=ALL-UNNAMED", "--add-opens=java.base/java.nio=ALL-UNNAMED", "--add-opens=java.base/sun.nio.ch=ALL-UNNAMED", "-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
