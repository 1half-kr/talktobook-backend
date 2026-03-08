FROM amazoncorretto:11

ENV TZ=Asia/Seoul

# 항상 동일한 이름의 JAR만 COPY
COPY lifebookshelf.jar lifebookshelf.jar

ENTRYPOINT ["java"]
CMD ["-Dspring.profiles.active=prod", "-jar", "lifebookshelf.jar"]