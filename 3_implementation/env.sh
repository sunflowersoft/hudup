#!/bin/sh

echo "You should set environmental variable JDK_HOME or JAVA_HOME"

export ANT_HOME=./tools/ant

if [ "$HUDUP_OLD_PATH" == "" ]
then
	export HUDUP_OLD_PATH=$PATH
fi

export PATH=.:$JDK_HOME/bin:$JAVA_HOME/bin:$ANT_HOME/bin:$HUDUP_OLD_PATH

echo PATH=$PATH

export CLASSPATH=./hudup.jar:./hudup-server.jar:./hudup-evaluator.jar:./hudup-listener.jar:./hudup-toolkit.jar:hudup-core.jar:./bin:./runtime-lib.jar:./lib/*

echo CLASSPATH=$CLASSPATH

export JAVA_CMD=java -cp $CLASSPATH
