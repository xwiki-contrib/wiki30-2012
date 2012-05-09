#!/bin/sh

# DB location for the HSQL database. Uncomment to use it
DB_LOCATION=$HOME/xwiki_database

# If a parameter is specified on the command line, use it as the DB location
if [ "$1" != "" ]; then
  DB_LOCATION=$1
fi

#Temporary hack until we fix the xwiki-platform-gwt-user dependency right
dir=`pwd`
cd xwiki-platform-wiki30/xwiki-platform-core/xwiki-platform-gwt/xwiki-platform-gwt-user
mvn -nsu clean install -DskipTests=true -DfailIfNoTests=false
cd $dir

#To effectively work with no snapshot updates, please update to maven 3.0.4
mvn -nsu clean install -Pdev -DskipTests=true -DfailIfNoTests=false -Dlicense.skip=true

echo ">>> Changing directory to: wiki30-distribution-zip"
cd wiki30-distribution/wiki30-distribution-zip/target

echo ">>> Unzipping archive"
unzip -q wiki30-distribution-zip-1.0-SNAPSHOT.zip
cd wiki30-distribution-zip-1.0-SNAPSHOT

if [ "$DB_LOCATION" != "" ]; then
  echo ">>> Setting the DB location"
  mkdir -p $DB_LOCATION
  sed -i "s,jdbc:hsqldb:file:database/xwiki_db,jdbc:hsqldb:file:${DB_LOCATION}/xwiki_db," webapps/xwiki/WEB-INF/hibernate.cfg.xml 
fi

echo ">>> Starting XWiki"
./start_xwiki.sh
