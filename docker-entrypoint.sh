#!/bin/sh

echo "Starting entrypoint script..."
echo "JAVA_OPTS = ${JAVA_OPTS}"

exec java ${JAVA_OPTS} -jar /doublewords-all.jar
