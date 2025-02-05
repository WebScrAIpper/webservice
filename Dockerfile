# Build
FROM eclipse-temurin:21-jdk AS build

WORKDIR .
COPY . .

RUN chmod +x gradlew
RUN ./gradlew clean bootJar

# Exec
FROM eclipse-temurin:21-jdk

WORKDIR .
COPY . .

RUN apt-get update && apt-get install -y python3 python3-venv python3-pip

# Copy JAR generated from build
COPY --from=build /build/libs/*.jar app.jar

RUN python3 -m venv /src/.venv && \
    . /src/.venv/bin/activate && \
    pip install --upgrade pip setuptools && \
    pip install youtube_transcript_api yt-dlp

ENTRYPOINT ["java", "-jar", "app.jar"]