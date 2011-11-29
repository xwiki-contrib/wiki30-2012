#!/bin/sh
mvn -o -nsu clean package -Pdev -DskipTests=true -DfailIfNoTests=false
