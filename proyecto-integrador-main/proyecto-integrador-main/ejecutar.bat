@echo off
java -Xms16m -Xmx96m -XX:+UseSerialGC -XX:MaxMetaspaceSize=64m -cp "out;lib/*" MainApplication
