#!/bin/sh
version=3.4-SNAPSHOT
curDir=`pwd`
jettyDir=$curDir/wiki30-distribution/wiki30-distribution-zip/target/wiki30-distribution-zip-1.0-SNAPSHOT/webapps/xwiki/resources/js/xwiki
libDir=$curDir/wiki30-distribution/wiki30-distribution-zip/target/wiki30-distribution-zip-1.0-SNAPSHOT/webapps/xwiki/WEB-INF/lib

#Realtime core

#cd wiki30-realtime/rt-gwt-client
#mvn -o -nsu clean install -DskipTests=true -DfailIfNoTests=false
#rm -rf $jettyDir/rte
#cp -r  target/rt-gwt-client-1.0-SNAPSHOT/resources/js/xwiki/rte  $jettyDir
#cp target/rt-gwt-client-1.0-SNAPSHOT-shared.jar $libDir

#Realtime plugin

cd wiki30-realtime-wysiwyg/wiki30-realtime-wysiwyg-plugin
mvn -o -nsu clean install -DskipTests=true -DfailIfNoTests=false

wysiwygDir=$jettyDir/wysiwyg/xwe
rm $wysiwygDir/*.cache.html
rm $wysiwygDir/*.rpc

cd $curDir/xwiki-platform-wiki30/xwiki-platform-core/xwiki-platform-wysiwyg/xwiki-platform-wysiwyg-war

mvn -o -nsu clean gwt:compile -DskipTests=true -DfailIfNoTests=false -Pdev
cp target/xwiki-platform-wysiwyg-war-${version}/resources/js/xwiki/wysiwyg/xwe/*.html $wysiwygDir
cp target/xwiki-platform-wysiwyg-war-${version}/resources/js/xwiki/wysiwyg/xwe/xwe.nocache.js $wysiwygDir
cp target/xwiki-platform-wysiwyg-war-${version}/resources/js/xwiki/wysiwyg/xwe/*.rpc $wysiwygDir
cd $curDir
