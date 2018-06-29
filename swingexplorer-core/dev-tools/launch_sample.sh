#!/bin/bash
#
# A script to launch the SwingExplorer example from within a local
# development environment. This is a development tool only, to help
# debug the migration from Ant to Maven builds.
#
# For this to work, you must have swingexplorer-agent built and installed 
# in your local Maven repo, and swingexplorer-core built with `mvn package`. 

M2_REPO=$HOME/.m2/repository
VERSION=1.7.0-SNAPSHOT

this_dir=$(dirname $0)
this_dir=$(realpath $this_dir)
core_dir=$(dirname $this_dir)

JAVA_CLASSPATH=$M2_REPO/org/swingexplorer/swingexplorer-agent/${VERSION}/swingexplorer-agent-${VERSION}.jar:$M2_REPO/org/jdesktop/swing-layout/1.0.3/swing-layout-1.0.3.jar:$M2_REPO/javassist/javassist/3.12.1.GA/javassist-3.12.1.GA.jar
java -cp $JAVA_CLASSPATH:${core_dir}/target/swingexplorer-core-${VERSION}.jar org.swingexplorer.Launcher sample.FRMPerson
