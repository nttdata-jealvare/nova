FROM openjdk:15
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} todolist.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/todolist.jar"]dir