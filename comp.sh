#!/bin/sh
P="/root/Desktop"
cd ${P}/something_adhoc/
git pull
javac ${P}/something_adhoc/prototype1/src/somethingadhoc/*.java
java -cp ${P}/something_adhoc/prototype1/src/ somethingadhoc.SomethingAdhoc
