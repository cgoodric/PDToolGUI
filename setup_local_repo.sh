#!/bin/sh
#
# add CIS libraries to local repository
PWD=`pwd`

mvn org.apache.maven.plugins:maven-install-plugin:2.3.1:install-file -Dfile=lib/cscommon-2230.jar -DgroupId=com.compositesw -DartifactId=common -Dversion=6.2.2 -Dpackaging=jar -DlocalRepositoryPath="$PWD/repo/"