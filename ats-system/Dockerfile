FROM eclipse-temurin:17

WORKDIR /app

COPY . .

RUN chmod +x mvnw

RUN ./mvnw package -DskipTests

EXPOSE 8080

CMD ["sh", "-c", "java -jar target/*.jar"]