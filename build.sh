#!/bin/bash

JARS=
for f in `ls ~/bin/apparat/*.jar` ; do
	JARS=$JARS:$f
done
JARS=${JARS:1}
scalac -cp $JARS -d bin DoWork.scala
