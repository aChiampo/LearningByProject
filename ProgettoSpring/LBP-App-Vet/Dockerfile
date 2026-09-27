FROM eclipse-temurin:21.0.12_8-jdk-alpine-3.24@sha256:cd87715a8d45cfaa42419207c64680234f62785c49055cccd20437b5c9018380 AS builder

WORKDIR /workspace

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

RUN chmod 0755 mvnw \
    && ./mvnw -B -ntp -DskipTests dependency:go-offline

COPY src/ src/

RUN ./mvnw -B -ntp -DskipTests clean package

FROM eclipse-temurin:21.0.12_8-jre-alpine-3.24@sha256:1a29e1fe337eb28b5bec30f0ee8ed29f0ff80ab6f75dcf9313efe82911065a52 AS runtime

RUN addgroup -S -g 10001 app \
    && adduser -S -D -H -u 10001 -G app app \
    && mkdir -p /app/storage/fatture /app/storage/ricette \
    && chown -R app:app /app

WORKDIR /app

COPY --from=builder --chown=app:app /workspace/target/App-*.jar /app/application.jar

USER 10001:10001

EXPOSE 9020

HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
    CMD wget -q -T 3 -O /dev/null http://127.0.0.1:9020/actuator/health/readiness || exit 1

ENTRYPOINT ["java", "-jar", "/app/application.jar"]
