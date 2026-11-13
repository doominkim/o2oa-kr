# Runtime image for the o2oa-kr groupware.
# Build context: the directory produced by deploy/build-dist.sh (contains o2server/).
#   docker build -f deploy/Dockerfile -t o2oa-kr <dist dir>
FROM eclipse-temurin:11-jdk-jammy

RUN apt-get update \
 && apt-get install -y --no-install-recommends fontconfig fonts-noto-cjk \
 && rm -rf /var/lib/apt/lists/* \
 && useradd --uid 1000 --create-home --shell /usr/sbin/nologin o2

COPY --chown=o2:o2 o2server /opt/o2server
USER o2
WORKDIR /opt/o2server
EXPOSE 8080
ENTRYPOINT ["/opt/o2server/entrypoint.sh"]
