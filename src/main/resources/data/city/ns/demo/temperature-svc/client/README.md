
# About

This is an example client that is designed to show how to implement a service that exists externally to the
Agora/City OS cluster, and which communicates with said cluster via the IoT gateway, using the MQTT protocol.
It is written in Python and uses the "Paho" library, but this is not a requirement.  It could be written in any
language that supports communication via TCP/IP sockets using TLS encryption for security, and using any support
library.  MQTT is a very simple protocol, so you could even write your own implementation that doesn't depend on
a support library if required.

Preferably, this example would be executed on a Raspberry Pi with an attached DHT11 temperature and humidity sensor.
It reads the temperature and humidity from that sensor and sends it via MQTT to the temperature-svc service, which
lives inside the Agora/City OS cluster, and which then forwards the message to be displayed on a Slack channel via
Agora's _Notifier_ service.

This example client exists purely for educational purposes.

# Usage

On a Raspberry Pi with an attached DHT11 sensor, simply running the application using the default Python 3
interpreter is enough:

`./main.py`

If you wish to run the device on a Raspberry Pi that does not have a DHT11 sensor attached, or on a desktop system,
it is possible to simulate the DHT11 sensor's readings using an environment variable:

`VIRTUAL_DEVICE=True ./main.py`

It is required to have certificates and a public key that have been registered with the Agora/City OS platform, in
order to perform TLS handshaking with the system's ingress access point.  These should be placed in the
../certs/dev directory.  See the call to mqtt.tls_set() in the code for naming conventions.

The MQTT broker that resides within the Agora/City OS cluster must also have a user registered, which will be used in
the call to mqtt.username_pw_set().  For security purposes, this user and its associated password are not included
in the source code.  Obtaining this information and the certificates mentioned above is left as an excercise for the
reader.

# Problems

Some Python interpreters do not function correctly and will output the following error:

`ssl.SSLCertVerificationError: [SSL: CERTIFICATE_VERIFY_FAILED] certificate verify failed: unable to get local issuer certificate (_ssl.c:1129)`

The reason for this is complicated and has not yet been ascertained.  Please try with another Python interpreter.
