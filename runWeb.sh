#!/bin/sh

# Default DB location for the HSQL database
DEFAULT_DB_LOCATION=$HOME/xwiki_database

if [ "$1" != "" ]; then
  DB_LOCATION=$1
else
  DB_LOCATION=$DEFAULT_DB_LOCATION
fi

mvn -o -nsu clean install -Pdev -DskipTests=true -DfailIfNoTests=false

echo ">>> Changing directory to: wiki30-distribution-zip"
cd wiki30-distribution/wiki30-distribution-zip/target

echo ">>> Unzipping archive"
unzip wiki30-distribution-zip-1.0-SNAPSHOT.zip
cd wiki30-distribution-zip-1.0-SNAPSHOT

echo ">>> Setting the DB location"
mkdir -p $DB_LOCATION
sed -i "s,jdbc:hsqldb:file:database/xwiki_db,jdbc:hsqldb:file:${DB_LOCATION}/xwiki_db," webapps/xwiki/WEB-INF/hibernate.cfg.xml 

echo ">>> Starting XWiki"
./start_xwiki.sh
