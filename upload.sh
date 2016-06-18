#!/bin/bash
read -s -p "Sonatype pwd ?" sonatypePwd
read -s -p "Key pwd ?" keyPwd
./gradlew dualcache-library:uploadArchives -PsonatypePwd=$sonatypePwd -PkeyPwd=$keyPwd
./gradlew dualcache-jsonserializer:uploadArchives -PsonatypePwd=$sonatypePwd -PkeyPwd=$keyPwd
./gradlew dualcache-serializerinterface:uploadArchives -PsonatypePwd=$sonatypePwd -PkeyPwd=$keyPwd
