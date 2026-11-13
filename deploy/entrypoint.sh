#!/bin/bash
# Container entrypoint: renders o2server config from environment variables, then starts
# the server in the foreground (web, center and application on one port; data and
# storage servers off; PostgreSQL and S3-compatible storage are external).
#
# Required: O2_DB_URL O2_DB_USER O2_DB_PASSWORD O2_ADMIN_PASSWORD O2_TOKEN_KEY
#           O2_S3_ENDPOINT (e.g. http://10.0.0.20:3900) O2_S3_ACCESS_KEY O2_S3_SECRET_KEY O2_S3_BUCKET
# Optional: PORT (8080) O2_PUBLIC_HOST O2_PUBLIC_PORT (443) O2_PUBLIC_PROTOCOL (https)
#           JAVA_HEAP (3g) TZ_ID (Asia/Seoul)
set -euo pipefail
cd "$(dirname "$0")"

for v in O2_DB_URL O2_DB_USER O2_DB_PASSWORD O2_ADMIN_PASSWORD O2_TOKEN_KEY \
         O2_S3_ENDPOINT O2_S3_ACCESS_KEY O2_S3_SECRET_KEY O2_S3_BUCKET; do
  [ -n "${!v:-}" ] || { echo "missing environment variable $v" >&2; exit 1; }
done
port="${PORT:-8080}"
public_host="${O2_PUBLIC_HOST:-}"
public_port="${O2_PUBLIC_PORT:-443}"
public_protocol="${O2_PUBLIC_PROTOCOL:-https}"

# JSON string literal (escapes backslash, quote and control characters).
js() { printf '"%s"' "$(printf '%s' "$1" | sed -e 's/\\/\\\\/g' -e 's/"/\\"/g' -e 's/\t/\\t/g' | tr -d '\r\n')"; }

mkdir -p config local
cat > config/node_127.0.0.1.json <<EOF
{
  "enable": true, "autoStart": true, "nodeAgentEnable": false, "selfHealthCheckEnable": false,
  "web": {"enable": true, "port": $port, "sslEnable": false, "proxyHost": $(js "$public_host"), "proxyPort": $public_port,
          "proxyCenterEnable": true, "proxyApplicationEnable": true, "proxyTimeOut": 300},
  "center": {"enable": true, "order": 0, "port": $port, "sslEnable": false, "httpProtocol": $(js "$public_protocol"),
             "proxyHost": $(js "$public_host"), "proxyPort": $public_port},
  "application": {"enable": true, "port": $port, "sslEnable": false, "proxyHost": $(js "$public_host"),
                  "proxyPort": $public_port, "includes": [], "excludes": []},
  "data": {"enable": false},
  "storage": {"enable": false},
  "dumpData": {"enable": false},
  "restoreData": {"enable": false}
}
EOF

cat > config/externalDataSources.json <<EOF
[{"enable": true, "url": $(js "$O2_DB_URL"), "username": $(js "$O2_DB_USER"), "password": $(js "$O2_DB_PASSWORD"),
  "driverClassName": "", "dictionary": "", "maxTotal": 50, "maxIdle": 0, "statEnable": true, "statFilter": "mergeStat",
  "includes": [], "excludes": [], "logLevel": "ERROR", "transactionIsolation": "read-committed", "autoCommit": false,
  "schema": "", "slowSqlEnable": true, "slowSqlThreshold": 3000}]
EOF

storage_entry() {
  printf '[{"protocol": "min", "username": %s, "password": %s, "host": %s, "port": 0, "name": %s, "prefix": %s, "enable": true, "weight": 100, "deepPath": false, "store": ""}]' \
    "$(js "$O2_S3_ACCESS_KEY")" "$(js "$O2_S3_SECRET_KEY")" "$(js "$O2_S3_ENDPOINT")" "$(js "$O2_S3_BUCKET")" "$(js "$1")"
}
{
  printf '{"enable": true'
  for m in file processPlatform mind meeting calendar cms bbs teamwork structure im general custom; do
    printf ', "%s": %s' "$m" "$(storage_entry "$m")"
  done
  printf '}\n'
} > config/externalStorageSources.json

cat > config/token.json <<EOF
{"password": $(js "$O2_ADMIN_PASSWORD"), "key": $(js "$O2_TOKEN_KEY"), "ssos": [], "oauths": [], "oauthClients": []}
EOF

cat > config/web.json <<EOF
{"defaultSkin": "indigo", "scaleConfig": false}
EOF

cat > config/general.json <<EOF
{"supportedLanguages": {"ko": "한국어", "zh-CN": "중국어(간체)", "en": "English"}}
EOF

export MALLOC_ARENA_MAX=1
exec java -javaagent:./console.jar -server -Djava.awt.headless=true \
  -Xms"${JAVA_HEAP:-3g}" -Xmx"${JAVA_HEAP:-3g}" -Duser.timezone="${TZ_ID:-Asia/Seoul}" \
  -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/tmp \
  -XX:+UnlockExperimentalVMOptions -XX:+EnableJVMCI \
  --module-path=./commons/module_java11 \
  --upgrade-module-path=./commons/module_java11/compiler.jar:./commons/module_java11/compiler-management.jar \
  -jar ./console.jar
