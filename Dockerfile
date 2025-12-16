FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY build/libs/global-dashboard-db-0.1-all.jar application.jar
EXPOSE 8081
# Using a non-root user for security (best practice)
RUN useradd -u 1000 -m micronaut
USER micronaut
CMD ["java", "-jar", "application.jar"]
