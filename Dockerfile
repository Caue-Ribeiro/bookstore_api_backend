FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY . .
RUN chmod +x ./mvnw
EXPOSE 8080 5005
CMD ["./mvnw", "spring-boot:run", "-Dspring-boot.run.jvmArguments=-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"]