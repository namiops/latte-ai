# How to run the debug kafka cli

kafka-admin-network-cli can be deployed for debugging purpose and I could confirm this can pub/sub with MSK

https://github.com/jonlabelle/docker-network-tools

## publish

Prepare the message.
```shell
echo '{"message": "some stuff"}' > sample-message.json
```

repeat the command to publish the message. (we can specify the option `-X broker.address.family=v4` for IPv4 request ,`-X broker.address.family=v6` for IPv6 request)
```shell
kcat -v -b  b-1.agoradev2kafkaclu.wnahze.c4.kafka.ap-northeast-1.amazonaws.com:9094 -X broker.address.family=v4 -X security.protocol=SSL -X ssl.key.location=/etc/certs/tls.key -X ssl.certificate.location=/etc/certs/tls.crt -X ssl.ca.location=/etc/cacert/kafka-ca-cert.pem -t kafka-admin.test -P sample-message.json
```

## consume

```shell
kcat -v -b b-1.agoradev2kafkaclu.wnahze.c4.kafka.ap-northeast-1.amazonaws.com:9094 -X broker.address.family=v4 -X security.protocol=SSL -X ssl.key.location=/etc/certs/tls.key -X ssl.certificate.location=/etc/certs/tls.crt -X ssl.ca.location=/etc/cacert/kafka-ca-cert.pem -G kafka-admin.consumer -o beginning kafka-admin.test
```

```shell
kafka-admin-network-cli-799888d56-9z2t5:/# kcat -v -b b-1.agoradev2kafkaclu.wnahze.c4.kafka.ap-northeast-1.amazonaws.com:9094 -X security.protocol=SSL -X ssl.key.location=/etc/certs/tls.key -X ssl.certificate.location=/etc/certs/tls.crt -X ssl.ca.location=/etc/cacert/kafka-ca-cert.pem -G kafka-admin.consumer -o beginning kafka-admin.test
% Waiting for group rebalance
%4|1689058115.540|FAIL|rdkafka#consumer-1| [thrd:ssl://b-1.agoradev2kafkaclu.wnahze.c4.kafka.ap-northeast-1.amaz]: ssl://b-1.agoradev2kafkaclu.wnahze.c4.kafka.ap-northeast-1.amazonaws.com:9094/bootstrap: Connection setup timed out in state CONNECT (after 30033ms in state CONNECT)
% ERROR: Local: Broker transport failure: ssl://b-1.agoradev2kafkaclu.wnahze.c4.kafka.ap-northeast-1.amazonaws.com:9094/bootstrap: Connection setup timed out in state CONNECT (after 30033ms in state CONNECT)
% Error at error_cb:1527:
% ERROR: Local: All broker connections are down: 1/1 brokers are down: terminating
kafka-admin-network-cli-799888d56-9z2t5:/# kcat -v -b b-1.agoradev2kafkaclu.wnahze.c4.kafka.ap-northeast-1.amazonaws.com:9094 -X security.protocol=SSL -X ssl.key.location=/etc/certs/tls.key -X ssl.certificate.location=/etc/certs/tls.crt -X ssl.ca.location=/etc/cacert/kafka-ca-cert.pem -G kafka-admin.consumer -o beginning kafka-admin.test
% Waiting for group rebalance
%4|1689058161.149|FAIL|rdkafka#consumer-1| [thrd:GroupCoordinator]: GroupCoordinator: b-1.agoradev2kafkaclu.wnahze.c4.kafka.ap-northeast-1.amazonaws.com:9094: Connection setup timed out in state CONNECT (after 30024ms in state CONNECT)
% ERROR: Local: Broker transport failure: GroupCoordinator: b-1.agoradev2kafkaclu.wnahze.c4.kafka.ap-northeast-1.amazonaws.com:9094: Connection setup timed out in state CONNECT (after 30024ms in state CONNECT)
% Group kafka-admin.consumer rebalanced (memberid rdkafka-73e2f168-0e9c-43db-a74a-6392abfae7d0): assigned: kafka-admin.test [0]
{"message": "some stuff"}

{"message": "some stuff"}

{"message": "some stuff"}

{"message": "some stuff"}

{"message": "some stuff"}

{"message": "some stuff"}

{"message": "some stuff"}

{"message": "some stuff"}

{"message": "some stuff"}

{"message": "some stuff"}

{"message": "some stuff"}

% Reached end of topic kafka-admin.test [0] at offset 11
```
