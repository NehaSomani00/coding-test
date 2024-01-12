FROM nexusprod.corp.intranet:4567/jsl-docker-standard-images/jdk1_11_agent:latest
WORKDIR /app
ARG JAR_FILE=target/fastivr-backend-service.jar
COPY target/fastivr-backend-service-*.jar /app/fastivr-backend-service.jar
RUN ls -l /app
RUN jar tvf /app/fastivr-backend-service.jar
ENTRYPOINT [ "java", "-jar", "/app/fastivr-backend-service.jar" ]