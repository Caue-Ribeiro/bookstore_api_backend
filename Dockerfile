FROM eclipse-temurin:25-jdk
WORKDIR /app
COPY . .
RUN chmod +x ./mvnw
CMD ["./mvnw", "spring-boot:run"]
