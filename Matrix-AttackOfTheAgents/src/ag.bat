start cmd /C javac *.java
start cmd /C java jade.Boot -gui -port 9999 -platform-id AgentsCreator
start cmd /C java jade.Boot -gui -port 9500 -platform-id AgentArchitect
start cmd /C java jade.Boot -port 9500 -container AA:ArchitectAgent
start cmd /C java jade.Boot -port 9999 -container  AC:AgentsCreator