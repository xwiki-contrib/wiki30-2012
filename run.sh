#!/bin/sh
mvn clean package
cd realtime-editor/rt-distribution
mvn jetty:run-war
