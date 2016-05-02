@echo off

echo Setting PATH...
set PATH=C:\ec2\deployment-vdo\Github\VGS\bin;C:\Program Files\Java\jdk1.8.0_77\bin

echo Starting RMI registry...
start rmiregistry
pause