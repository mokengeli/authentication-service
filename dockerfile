# Étape de build avec extraction dynamique de la version
FROM maven:3.9.6-eclipse-temurin-21-jammy AS build
WORKDIR /app

# Copie du pom.xml et téléchargement des dépendances
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copie du code source et compilation
COPY src ./src
RUN mvn package -DskipTests

# Extraction dynamique de la version et renommage du JAR
RUN APP_VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout) \
    && echo "Version extraite: $APP_VERSION" \
    && cp target/authentication-service-$APP_VERSION.jar target/app.jar

# Étape d'exécution
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Création d'un utilisateur non-root pour la sécurité
RUN addgroup --system appuser && adduser --system --ingroup appuser appuser
USER appuser:appuser

# Copie du JAR depuis l'étape de build
COPY --from=build /app/target/app.jar ./app.jar

# Copie de tous les fichiers de configuration pour tous les environnements
COPY src/main/resources/application*.yml ./config/

# Exposer le port (même si dynamique, utile pour la documentation)
EXPOSE 8080

# Variables d'environnement REQUISES (sans valeurs par défaut)
ENV SERVER_PORT="" \
    JWT_SECRET="" \
    POSTGRES_URL="" \
    POSTGRES_USER="" \
    POSTGRES_PASSWORD="" \
    EUREKA_SERVER_URL="" \
    ALLOWED_ORIGINS="" \
    JWT_EXPIRATION="" \
    IS_SSL=""

# Variables d'environnement OPTIONNELLES (avec valeurs par défaut)
ENV TIME_ZONE="GMT+01:00" \
    SPRING_PROFILES_ACTIVE="dev" \
    POSTGRES_SCHEMA="user_schema" \
    USER_SERVICE_ID="user-service"

# Point d'entrée sans spécifier le profil (sera fourni au runtime)
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=${SPRING_PROFILES_ACTIVE}",\
            "--spring.config.location=file:./config/"]

# Healthcheck - vérifie si l'application répond
HEALTHCHECK --interval=30s --timeout=10s --retries=3 \
    CMD wget -q --spider http://localhost:${SERVER_PORT:-8080}/actuator/health || exit 1