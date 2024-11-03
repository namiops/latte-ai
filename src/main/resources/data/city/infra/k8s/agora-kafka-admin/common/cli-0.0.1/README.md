# Kafka CLI tools

Since the isito-proxy can be used with simple commands when injected (
e.g. `kafka-topics.sh --bootstrap-server ${KAFKA_BOOTSTRAP_SERVERS} --list`, `kcat -b ${KAFKA_BOOTSTRAP_SERVERS} -L`), here we will demonstrate how to use commands when
it is not injected.

## Check the network connection

```shell
nc -vz $KAFKA_BOOTSTRAP_HOST $KAFKA_TLS_PORT
```

## (w/o istio) Kafka CLI basic command usage

> [!NOTE]
> This doesn't work in the Speedway Prod environment because `readOnlyRootFilesystem: true` needs to be set in the `securityContext`. 
> Therefore, please use `kcat` instead. This section is left for reference.

Based on the nice
article [Securely Connecting Kafka and Kafka Command Line Tools Using Mutual TLS — Smallstep](https://smallstep.com/hello-mtls/doc/combined/kafka/kafka-cli),
we can run the kafka-cli without istio-injection.

Enter the password `aaaaaa` for all the field

```shell
openssl pkcs12 -export -in /etc/certs/tls.crt -inkey /etc/certs/tls.key -name myuser > client.p12
```

```shell
keytool -importkeystore -srckeystore client.p12 -destkeystore kafka.client.keystore.jks -srcstoretype pkcs12 -alias myuser
```

```shell
keytool -keystore kafka.client.truststore.jks -alias CARoot -import -file /etc/cacert/aws-root-cert.pem
```

```shell
mkdir -p /var/private/ssl
cp kafka.client.*.jks /var/private/ssl/
```

```shell
cat << EOF >> client.properties
security.protocol=SSL
ssl.keystore.location=/var/private/ssl/kafka.client.keystore.jks
ssl.keystore.password=aaaaaa
ssl.key.password=aaaaaa
ssl.truststore.location=/var/private/ssl/kafka.client.truststore.jks
ssl.truststore.password=aaaaaa
EOF
```

- list the topics:

```shell
kafka-topics.sh --bootstrap-server $KAFKA_BOOTSTRAP_SERVER --command-config client.properties --list 
```

- list the ACLs:

```shell
kafka-acls.sh --bootstrap-server $KAFKA_BOOTSTRAP_SERVER --list  --command-config client.properties
```

## (w/o istio)  kcat usage

With [kcat](https://github.com/edenhill/kcat), we can pub/sub messages without creating the jks files:

- list the topics:

```shell
kcat -b $KAFKA_BOOTSTRAP_SERVER -X broker.address.family=v4 -X security.protocol=SSL -X ssl.key.location=/etc/certs/tls.key -X ssl.certificate.location=/etc/certs/tls.crt -X ssl.ca.location=/etc/cacert/kafka-ca-cert.pem -L 
```


- publish a message
  - we can specify the option `-X broker.address.family=v4` for IPv4 request ,`-X broker.address.family=v6` for IPv6 request

```shell
echo "Hello, Kafka!" | kcat -v -b  $KAFKA_BOOTSTRAP_SERVER  -X broker.address.family=v4 -X security.protocol=SSL -X ssl.key.location=/etc/certs/tls.key -X ssl.certificate.location=/etc/certs/tls.crt -X ssl.ca.location=/etc/cacert/kafka-ca-cert.pem -t kafka-admin.test -P
```

- consume the message

```shell
kcat -v -b $KAFKA_BOOTSTRAP_SERVER  -X broker.address.family=v4 -X security.protocol=SSL -X ssl.key.location=/etc/certs/tls.key -X ssl.certificate.location=/etc/certs/tls.crt -X ssl.ca.location=/etc/cacert/kafka-ca-cert.pem -G kafka-admin.consumer -o beginning kafka-admin.test
```
