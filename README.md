# JBWS-5248#

run 

  mvn verify

to download AS into target directory and use it for test

To use existing AS installation run
  
  mvn verify -Djboss.home=...

To use existing running AS installation run

  mvn verify -Djboss.home=... -DallowConnectingToRunningServer=true
  
To connect to AS running on different address:port then defualt localhost:8080 

  -Djboss.bind.address=... -Djboss.bind.port=...
  
