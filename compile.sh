#!/bin/sh
mvn -o -nsu clean install -Pdev -DskipTests=true -DfailIfNoTests=false
