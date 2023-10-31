#!/bin/bash

mvn clean package -Dmaven.test.skip=true
AGENT_FILE=opentelemetry-javaagent-all.jar
if [ ! -f "${AGENT_FILE}" ]; then
  curl -L https://github.com/aws-observability/aws-otel-java-instrumentation/releases/download/v1.28.1/aws-opentelemetry-agent.jar --output ${AGENT_FILE}
fi

export OTEL_TRACES_EXPORTER=logging
export OTEL_METRICS_EXPORTER=logging
java -javaagent:./${AGENT_FILE} -jar target/hello-app-1.0.jar
