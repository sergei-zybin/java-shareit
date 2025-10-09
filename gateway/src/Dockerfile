FROM eclipse-temurin:21-jre-jammy

RUN apt-get update && apt-get install -y curl netcat && rm -rf /var/lib/apt/lists/*

VOLUME /tmp
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

RUN echo '#!/bin/bash\n\
until nc -z server 9090; do\n\
  echo "Waiting for server:9090..."\n\
  sleep 5\n\
done\n\
echo "Server is ready!"\n\
exec java ${JAVA_OPTS} -jar /app.jar' > /wait-and-run.sh

RUN chmod +x /wait-and-run.sh

ENTRYPOINT ["/wait-and-run.sh"]