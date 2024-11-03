# How to run the debug kafka cli

Based on the nice article [Securely Connecting Kafka and Kafka Command Line Tools Using Mutual TLS — Smallstep](https://smallstep.com/hello-mtls/doc/combined/kafka/kafka-cli),  we can run the kafka-cli without istio-injection.


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

Now let's list the topics:

```shell
[root@debug-kafka-cli-5776cf44b5-vqndt appuser]# kafka-topics --bootstrap-server kafka.default:9094  --command-config client.properties --list
[2023-07-11 08:45:57,600] ERROR [AdminClient clientId=adminclient-1] Connection to node -1 (kafka.default/10.220.59.108:9094) failed authentication due to: SSL handshake failed (org.apache.kafka.clients.NetworkClient)
[2023-07-11 08:45:57,601] WARN [AdminClient clientId=adminclient-1] Metadata update failed due to authentication error (org.apache.kafka.clients.admin.internals.AdminMetadataManager)
org.apache.kafka.common.errors.SslAuthenticationException: SSL handshake failed
Caused by: javax.net.ssl.SSLHandshakeException: No subject alternative DNS name matching kafka.default found.
	at java.base/sun.security.ssl.Alert.createSSLException(Alert.java:131)
	at java.base/sun.security.ssl.TransportContext.fatal(TransportContext.java:349)
	at java.base/sun.security.ssl.TransportContext.fatal(TransportContext.java:292)
	at java.base/sun.security.ssl.TransportContext.fatal(TransportContext.java:287)
	at java.base/sun.security.ssl.CertificateMessage$T12CertificateConsumer.checkServerCerts(CertificateMessage.java:654)
	at java.base/sun.security.ssl.CertificateMessage$T12CertificateConsumer.onCertificate(CertificateMessage.java:473)
	at java.base/sun.security.ssl.CertificateMessage$T12CertificateConsumer.consume(CertificateMessage.java:369)
	at java.base/sun.security.ssl.SSLHandshake.consume(SSLHandshake.java:392)
	at java.base/sun.security.ssl.HandshakeContext.dispatch(HandshakeContext.java:443)
	at java.base/sun.security.ssl.SSLEngineImpl$DelegatedTask$DelegatedAction.run(SSLEngineImpl.java:1074)
	at java.base/sun.security.ssl.SSLEngineImpl$DelegatedTask$DelegatedAction.run(SSLEngineImpl.java:1061)
	at java.base/java.security.AccessController.doPrivileged(Native Method)
	at java.base/sun.security.ssl.SSLEngineImpl$DelegatedTask.run(SSLEngineImpl.java:1008)
	at org.apache.kafka.common.network.SslTransportLayer.runDelegatedTasks(SslTransportLayer.java:430)
	at org.apache.kafka.common.network.SslTransportLayer.handshakeUnwrap(SslTransportLayer.java:514)
	at org.apache.kafka.common.network.SslTransportLayer.doHandshake(SslTransportLayer.java:368)
	at org.apache.kafka.common.network.SslTransportLayer.handshake(SslTransportLayer.java:291)
	at org.apache.kafka.common.network.KafkaChannel.prepare(KafkaChannel.java:178)
	at org.apache.kafka.common.network.Selector.pollSelectionKeys(Selector.java:543)
	at org.apache.kafka.common.network.Selector.poll(Selector.java:481)
	at org.apache.kafka.clients.NetworkClient.poll(NetworkClient.java:561)
	at org.apache.kafka.clients.admin.KafkaAdminClient$AdminClientRunnable.processRequests(KafkaAdminClient.java:1333)
	at org.apache.kafka.clients.admin.KafkaAdminClient$AdminClientRunnable.run(KafkaAdminClient.java:1264)
	at java.base/java.lang.Thread.run(Thread.java:829)
Caused by: java.security.cert.CertificateException: No subject alternative DNS name matching kafka.default found.
	at java.base/sun.security.util.HostnameChecker.matchDNS(HostnameChecker.java:212)
	at java.base/sun.security.util.HostnameChecker.match(HostnameChecker.java:103)
	at java.base/sun.security.ssl.X509TrustManagerImpl.checkIdentity(X509TrustManagerImpl.java:455)
	at java.base/sun.security.ssl.X509TrustManagerImpl.checkIdentity(X509TrustManagerImpl.java:415)
	at java.base/sun.security.ssl.X509TrustManagerImpl.checkTrusted(X509TrustManagerImpl.java:283)
	at java.base/sun.security.ssl.X509TrustManagerImpl.checkServerTrusted(X509TrustManagerImpl.java:141)
	at java.base/sun.security.ssl.CertificateMessage$T12CertificateConsumer.checkServerCerts(CertificateMessage.java:632)
	... 19 more
Error while executing topic command : SSL handshake failed
[2023-07-11 08:45:57,605] ERROR org.apache.kafka.common.errors.SslAuthenticationException: SSL handshake failed
Caused by: javax.net.ssl.SSLHandshakeException: No subject alternative DNS name matching kafka.default found.
	at java.base/sun.security.ssl.Alert.createSSLException(Alert.java:131)
	at java.base/sun.security.ssl.TransportContext.fatal(TransportContext.java:349)
	at java.base/sun.security.ssl.TransportContext.fatal(TransportContext.java:292)
	at java.base/sun.security.ssl.TransportContext.fatal(TransportContext.java:287)
	at java.base/sun.security.ssl.CertificateMessage$T12CertificateConsumer.checkServerCerts(CertificateMessage.java:654)
	at java.base/sun.security.ssl.CertificateMessage$T12CertificateConsumer.onCertificate(CertificateMessage.java:473)
	at java.base/sun.security.ssl.CertificateMessage$T12CertificateConsumer.consume(CertificateMessage.java:369)
	at java.base/sun.security.ssl.SSLHandshake.consume(SSLHandshake.java:392)
	at java.base/sun.security.ssl.HandshakeContext.dispatch(HandshakeContext.java:443)
	at java.base/sun.security.ssl.SSLEngineImpl$DelegatedTask$DelegatedAction.run(SSLEngineImpl.java:1074)
	at java.base/sun.security.ssl.SSLEngineImpl$DelegatedTask$DelegatedAction.run(SSLEngineImpl.java:1061)
	at java.base/java.security.AccessController.doPrivileged(Native Method)
	at java.base/sun.security.ssl.SSLEngineImpl$DelegatedTask.run(SSLEngineImpl.java:1008)
	at org.apache.kafka.common.network.SslTransportLayer.runDelegatedTasks(SslTransportLayer.java:430)
	at org.apache.kafka.common.network.SslTransportLayer.handshakeUnwrap(SslTransportLayer.java:514)
	at org.apache.kafka.common.network.SslTransportLayer.doHandshake(SslTransportLayer.java:368)
	at org.apache.kafka.common.network.SslTransportLayer.handshake(SslTransportLayer.java:291)
	at org.apache.kafka.common.network.KafkaChannel.prepare(KafkaChannel.java:178)
	at org.apache.kafka.common.network.Selector.pollSelectionKeys(Selector.java:543)
	at org.apache.kafka.common.network.Selector.poll(Selector.java:481)
	at org.apache.kafka.clients.NetworkClient.poll(NetworkClient.java:561)
	at org.apache.kafka.clients.admin.KafkaAdminClient$AdminClientRunnable.processRequests(KafkaAdminClient.java:1333)
	at org.apache.kafka.clients.admin.KafkaAdminClient$AdminClientRunnable.run(KafkaAdminClient.java:1264)
	at java.base/java.lang.Thread.run(Thread.java:829)
Caused by: java.security.cert.CertificateException: No subject alternative DNS name matching kafka.default found.
	at java.base/sun.security.util.HostnameChecker.matchDNS(HostnameChecker.java:212)
	at java.base/sun.security.util.HostnameChecker.match(HostnameChecker.java:103)
	at java.base/sun.security.ssl.X509TrustManagerImpl.checkIdentity(X509TrustManagerImpl.java:455)
	at java.base/sun.security.ssl.X509TrustManagerImpl.checkIdentity(X509TrustManagerImpl.java:415)
	at java.base/sun.security.ssl.X509TrustManagerImpl.checkTrusted(X509TrustManagerImpl.java:283)
	at java.base/sun.security.ssl.X509TrustManagerImpl.checkServerTrusted(X509TrustManagerImpl.java:141)
	at java.base/sun.security.ssl.CertificateMessage$T12CertificateConsumer.checkServerCerts(CertificateMessage.java:632)
	... 19 more
 (kafka.admin.TopicCommand$)
```

Oops, kafka.default cannot be used. Let's try the raw URL.

```shell
kafka-topics --bootstrap-server b-1.agoralab2kafkaclu.k761ez.c4.kafka.ap-northeast-1.amazonaws.com:9094  --command-config client.properties --list 
```
The result would be 
```log
__amazon_msk_canary
__consumer_offsets
kafka-admin.test
```

Worked! :)

let's list the ACLs:

```shell
[root@debug-kafka-cli-5776cf44b5-vqndt appuser]#  kafka-acls --bootstrap-server b-1.agoralab2kafkaclu.k761ez.c4.kafka.ap-northeast-1.amazonaws.com:9094 --list  --command-config client.properties
Current ACLs for resource `ResourcePattern(resourceType=CLUSTER, name=kafka-cluster, patternType=LITERAL)`:
 	(principal=User:CN=kafka-admin.agora-lab.w3n.io,OU=CityOS, host=*, operation=ALL, permissionType=ALLOW)
	(principal=User:CN=kafka-monitor.agora-lab.w3n.io,OU=CityOS, host=*, operation=DESCRIBE, permissionType=ALLOW)
	(principal=User:CN=kafka-operator.agora-lab.w3n.io,OU=CityOS, host=*, operation=ALL, permissionType=ALLOW)
	(principal=User:CN=kafka-monitor.agora-lab.w3n.io,OU=CityOS, host=*, operation=DESCRIBE_CONFIGS, permissionType=ALLOW)

Current ACLs for resource `ResourcePattern(resourceType=GROUP, name=*, patternType=LITERAL)`:
 	(principal=User:CN=kafka-monitor.agora-lab.w3n.io,OU=CityOS, host=*, operation=DESCRIBE_CONFIGS, permissionType=ALLOW)
	(principal=User:CN=kafka-admin.agora-lab.w3n.io,OU=CityOS, host=*, operation=ALL, permissionType=ALLOW)
	(principal=User:CN=kafka-monitor.agora-lab.w3n.io,OU=CityOS, host=*, operation=DESCRIBE, permissionType=ALLOW)

Current ACLs for resource `ResourcePattern(resourceType=TOPIC, name=kafka-admin.test, patternType=LITERAL)`:
 	(principal=User:CN=kafka-operator.agora-lab.w3n.io,OU=CityOS, host=*, operation=READ, permissionType=ALLOW)
	(principal=User:CN=kafka-admin.agora-lab.w3n.io,OU=CityOS, host=*, operation=DESCRIBE, permissionType=ALLOW)
	(principal=User:CN=kafka-operator.agora-lab.w3n.io,OU=CityOS, host=*, operation=WRITE, permissionType=ALLOW)
	(principal=User:CN=kafka-admin.agora-lab.w3n.io,OU=CityOS, host=*, operation=READ, permissionType=ALLOW)
	(principal=User:CN=kafka-operator.agora-lab.w3n.io,OU=CityOS, host=*, operation=DESCRIBE, permissionType=ALLOW)
	(principal=User:CN=kafka-admin.agora-lab.w3n.io,OU=CityOS, host=*, operation=WRITE, permissionType=ALLOW)

Current ACLs for resource `ResourcePattern(resourceType=TOPIC, name=*, patternType=LITERAL)`:
 	(principal=User:CN=kafka-monitor.agora-lab.w3n.io,OU=CityOS, host=*, operation=DESCRIBE_CONFIGS, permissionType=ALLOW)
	(principal=User:CN=kafka-admin.agora-lab.w3n.io,OU=CityOS, host=*, operation=CREATE, permissionType=ALLOW)
	(principal=User:CN=kafka-operator.agora-lab.w3n.io,OU=CityOS, host=*, operation=DESCRIBE, permissionType=ALLOW)
	(principal=User:CN=kafka-monitor.agora-lab.w3n.io,OU=CityOS, host=*, operation=DESCRIBE, permissionType=ALLOW)
	(principal=User:CN=kafka-admin.agora-lab.w3n.io,OU=CityOS, host=*, operation=DESCRIBE, permissionType=ALLOW)
	(principal=User:CN=kafka-admin.agora-lab.w3n.io,OU=CityOS, host=*, operation=ALTER, permissionType=ALLOW)
	(principal=User:CN=kafka-admin.agora-lab.w3n.io,OU=CityOS, host=*, operation=ALTER_CONFIGS, permissionType=ALLOW)
	(principal=User:CN=kafka-operator.agora-lab.w3n.io,OU=CityOS, host=*, operation=DESCRIBE_CONFIGS, permissionType=ALLOW)
	(principal=User:CN=kafka-admin.agora-lab.w3n.io,OU=CityOS, host=*, operation=DESCRIBE_CONFIGS, permissionType=ALLOW)
	(principal=User:CN=kafka-operator.agora-lab.w3n.io,OU=CityOS, host=*, operation=ALTER_CONFIGS, permissionType=ALLOW)
	(principal=User:CN=kafka-admin.agora-lab.w3n.io,OU=CityOS, host=*, operation=DELETE, permissionType=ALLOW)

Current ACLs for resource `ResourcePattern(resourceType=GROUP, name=kafka-admin.consumer, patternType=LITERAL)`:
 	(principal=User:CN=kafka-operator.agora-lab.w3n.io,OU=CityOS, host=*, operation=READ, permissionType=ALLOW)
	(principal=User:CN=kafka-operator.agora-lab.w3n.io,OU=CityOS, host=*, operation=DESCRIBE, permissionType=ALLOW)
	(principal=User:CN=kafka-admin.agora-lab.w3n.io,OU=CityOS, host=*, operation=DESCRIBE, permissionType=ALLOW)
	(principal=User:CN=kafka-admin.agora-lab.w3n.io,OU=CityOS, host=*, operation=READ, permissionType=ALLOW)
```
