#!/bin/sh

TARGET="/tmp/spacenet"

ant jar

rm -Rf $TARGET
mkdir $TARGET

cp dist/automenta.spacegraph.1.jar $TARGET
cp run.sh $TARGET
cp run.bat $TARGET
cp -R dist/lib $TARGET
cp -R lib/jogl $TARGET

cd /tmp
zip spacenet.`date +%s`.zip -r -9 spacenet

