# RADIUS POC server

This code is designed for a PoC to study how to achieve the authentication required when connecting to the network.
The PoC will be conducted by the ICT team, who will design the City Network, and the City Platform, who will provide the Woven ID. City Platform team will develop RADIUS proxy server to connect the two and examine how they connect to each other.

## PoC Architecture
ICT team plans to deploy a Cisco ISE as a network appliance to manage authentication authorization. The RADIUS server will accept authentication requests from the Cisco ISE via the RADIUS protocol, and then make authentication queries to the Woven ID provided by the City Platform via the Open ID Connect protocol.
![arch](architecture.svg)

## How to run(development mode)
In development mode, docker-compose is used to start all the following services on the local machine.
- RADIUS server
- Keycloak server
- RADIUS client

1. Go to the project directory
The directory where this README is located is the project directory

2. Build the project
```
docker-compose build`
```
This command will download the image which include Rust compiler and build the code.
It will take a few minutes.

3. Run the project
```
docker-compose up
```
This command starts all components sequentially.
Keycloak takes several tens of seconds to start up. After that, RADIUS Server starts, and finally, RADIUS client starts.

4. Check the result of RADIUS client
After a while, the following execution results are displayed.
```
keycloak-server  | 16:22:18,596 INFO  [org.jboss.as] (Controller Boot Thread) WFLYSRV0060: Http management interface listening on http://127.0.0.1:9990/management
keycloak-server  | 16:22:18,596 INFO  [org.jboss.as] (Controller Boot Thread) WFLYSRV0051: Admin console listening on http://127.0.0.1:9990
radius-server    | [2022-11-07T16:22:24Z INFO  poc_radius_tokio] serve is now ready: 0.0.0.0:1812
radius-server    | [2022-11-07T16:22:30Z INFO  poc_radius_tokio] Login suceeded.(user: alice)
radius-client    | connecting to radius-server
radius-client    | Sending authentication request
radius-client    | Access accepted
radius-client    | Attributes returned by server:
radius-client exited with code 0
```
The RADIUS client sends an authentication request to the RADIUS server and can confirm that the Access is successful.

## How to connect to RADIUS server from outside local machine
After performing the above startup procedure, the RADIUS service exposes on port 1812 of localhost.
When connecting from a Cisco ISE or other device, you can specify the IP address or hostname of the local machine running docker-compose and the port number 1812.

## How to run(experiment mode)
In the experimental mode, you can connect to the Woven ID dev environment to check how to connect to the actual ID provider. To connect to the dev cluster, the following 3 environment variables in the RADIUS SERVER must be rewritten.
- OPENID_TOKEN_ENDPOINT
- OPENID_CLIENT_ID
- OPENID_CLIENT_SECRET

For this experiment, it is necessary to register a Client in the Dev environment and get a Secret, which can be created by asking the Agora team in the #wcm-agora-team-ama channel.
