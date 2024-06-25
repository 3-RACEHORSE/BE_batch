FROM bellsoft/liberica-openjdk-alpine:17 as build
WORKDIR /workspace/app

# Copy the built JAR file
COPY build/libs/*.jar .

ENTRYPOINT ["java","org.springframework.boot.loader.JarLauncher"]