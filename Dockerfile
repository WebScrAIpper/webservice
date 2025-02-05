# Utilisez une image de base appropriée
FROM alpine/java:21-jdk

# Définissez le répertoire de travail
WORKDIR .

COPY . .

RUN echo $(pwd)

# Exécutez la commande Gradle pour construire le JAR
RUN chmod +x ./gradlew && ./gradlew clean bootJar

# Copiez le JAR généré dans le répertoire de travail
COPY build/libs/*.jar app.jar

# Définissez la commande à exécuter lorsque le conteneur démarre
ENTRYPOINT ["java", "-jar", "app.jar"]

#COPY src/main/resources/static/pageExample.html src/main/resources/static/pageExample.html
#COPY src/.venv src/.venv
#COPY src/scripts/get_transcript.py src/scripts/get_transcript.py
#COPY src/scripts/get_yt_infos.py src/scripts/get_yt_infos.py