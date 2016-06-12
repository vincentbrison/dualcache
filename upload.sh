#!/bin/bash
read -s -p "Sonatype pwd ?" sonatypePwd
read -s -p "Key pwd ?" keyPwd
./gradlew dualcache:uploadArchives -PsonatypePwd=$sonatypePwd -PkeyPwd=$keyPwd
./gradlew dualcache-jsonserializer:uploadArchives -PsonatypePwd=$sonatypePwd -PkeyPwd=$keyPwd
