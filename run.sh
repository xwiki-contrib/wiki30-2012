#!/bin/sh
mvn clean package
cd realtime-editor/rt-distribution
mvn jetty:run-war | tee results-$(date +%F-%T).log
