#!/bin/bash

# Bash script for starting the semantic precision and recall
# calculator

find_java () {
	if [ "$JAVA_HOME" != "" ]; then
		JAVA=$JAVA_HOME/bin/java
		return
	fi
	WHICH=`which java`
	if [ $? -eq 0 ]; then
		JAVA=$WHICH
		return
	fi
	echo "Please set the environment variable JAVA_HOME to match your java directory"
}

find_java

# change to directory containing this script and thus the JAR file
cd `dirname $0`
$JAVA -jar spart.jar "$@"
cd $OLDPWD
