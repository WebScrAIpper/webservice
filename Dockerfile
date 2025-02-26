# Build
FROM eclipse-temurin:21-jdk AS build

WORKDIR .
COPY . .

RUN ./gradlew clean bootJar

# Exec
FROM eclipse-temurin:21-jre

WORKDIR .

RUN apt-get update && apt-get install -y python3 python3-venv python3-pip

# Copy .jar from build
COPY --from=build /build/libs/*.jar app.jar

COPY /src/main/python/scripts /src/main/python/scripts

RUN python3 -m venv /src/main/python/.venv && \
    . /src/main/python/.venv/bin/activate && \
    pip install --upgrade pip setuptools && \
    pip install youtube_transcript_api yt-dlp

ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]