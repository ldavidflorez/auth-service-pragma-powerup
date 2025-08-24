FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
COPY deployment/AuthService.jar AuthService.jar
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=70 -Djava.security.egd=file:/dev/./urandom"

ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar AuthService.jar" ]
