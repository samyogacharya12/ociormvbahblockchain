#!/bin/bash

JAR="jeromq-0.5.2.jar"

echo "Compiling Java files..."
javac -cp $JAR ZMQServer.java ZMQClient.java

echo "Run options:"
echo "1. Run Server"
echo "2. Run Client"
read -p "Choose [1-2]: " choice

if [ "$choice" == "1" ]; then
  java -cp .:$JAR ZMQServer
elif [ "$choice" == "2" ]; then
  java -cp .:$JAR ZMQClient
else
  echo "Invalid choice."
fi
