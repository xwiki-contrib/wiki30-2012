#!/bin/sh

currentDir=`pwd`
serverDir=wiki30-distribution/wiki30-distribution-zip/target/wiki30-distribution-zip-1.0-SNAPSHOT

cd $serverDir
sh start_xwiki.sh
