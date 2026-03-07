FROM amazoncorretto:11

ENV TZ=Asia/Seoul

# 항상 동일한 이름의 JAR만 COPY
COPY server/lifebookshelf.jar lifebookshelf.jar
COPY deploy/server/promtail-config.yml /etc/promtail-config.yml

CMD ["java", "-jar", "-Dspring.profiles.active=prod", "lifebookshelf.jar"]