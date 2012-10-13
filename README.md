# Owl Platform REST Interface #

## Build ##
mvn clean compile

## Run ##
The REST server takes several execution environment options to control its
behavior. Each one is specified by passing the "-Dcommand" flag to maven when
running. Below is a list of all possible options:

* __bind.host__: The hostname or IP address on which the REST server should bind.
  This option is useful if, for example, your server has multiple IP addresses
  and only one should host the server.  By default, the server will bind to
  the wildcard interface, meaning all interfaces will be used.
* __bind.port__: The TCP port on which the REST server should bind.  By default it
  will use port 9998.
* __wm.host__: The hostname or IP address of the world model to connect to.  This
  defaults to "localhost" if not specified.
* __wm.client.port__: The TCP port on which the world model is listening for
  incoming client connections.  Defaults to 7010 if not specified.

Example of running the server which connects to the world model at
server.my.com:1234, and binds to all local interfaces on port 5555:

    mvn exec:java -Dbind.port=5555 -Dwm.host=server.my.com \\
      -Dwm.client.port=1234
