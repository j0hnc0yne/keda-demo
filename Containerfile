FROM amazoncorretto:25-alpine

# Metadata as a label
LABEL maintainer="john.coyne@gmail.com" version="1.0" description="Demo app for KEDA"

# Copy the application JAR into the container
COPY build/libs/*-boot.jar /app/app.jar

# Expose the application port
EXPOSE 8080

# Command to run the application
CMD ["java", "-jar", "/app/app.jar"]
