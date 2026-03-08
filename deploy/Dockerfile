FROM amazoncorretto:11

ENV TZ=Asia/Seoul

# 항상 동일한 이름의 JAR만 COPY
COPY lifebookshelf.jar lifebookshelf.jar

CMD ["java", "-jar", "-Dspring.profiles.active=prod", "lifebookshelf.jar"]