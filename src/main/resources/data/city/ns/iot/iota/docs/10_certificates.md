# Credentials Distributions

## Bulk Provisioning

During the bulk provisioning phase an operator will add a group to their tenant, receive a provisioning secret and N devices to that group. The provisioning secret will need to be embedded on the device encrypted storage (for example) and used at provisioning time together with a Certificate Signing Request (CSR) to obtain a certificate / broker credentials pair. 
Note: it's not mandatory to use the broker and its credentials although it's necessary to have any communication with the cluster via mTLS and therefore the CA Chain and the Certificate returned by IoTA will need to be used to set a TLS client on the device itself. This will require setting an ingress ad hoc to redirect the traffic on the desired service.

Below, a sequence diagram to show the whole flow:

```plantuml
@startuml
!pragma teoz true
autonumber
title Certificate Distribution
participant Device as D
participant User as U
box "Agora" #lightblue
participant IoTA
participant RabbitMQ as MQ
participant YourService as YS
end box
box TMNA (for now still Agora)
participant CA
end box

U -> IoTA: iotactl add group <groupName>
IoTA --> U: Provisioning Secret: PS123
note top
This is a bulk provisioning secret,
you can use it for N devices
if they belong to the same group
end note
U -> IoTA: iotactl add <deviceName> -g <groupName>
IoTA --> U: success
{start} U -> D: Embed Provisioning Secret PS123
activate D
D -> D: generate keypair (iotactl does this)
D -> D: generate CSR (iotactl does this)
D -> IoTA: iotactl provision <deviceName> -g <groupName> --provision-secret PS123
note right
This is sending the CSR and the 
provisioning secret to IoTA
end note
activate IoTA
IoTA -> CA: POST /sign with CSR
CA --> IoTA: return certificate for device
IoTA -> MQ: create broker credentials for device
MQ --> IoTA: ok
note right
Broker credentials are stored in a separate encrypted DB
end note
{end} IoTA --> D: CA Chain, crt, broker creds
deactivate IoTA
deactivate D
D -> D: init TLS Client
D -> MQ: connect with broker credentials
D -> YS: call HTTP REST
YS -> MQ: Consume MQTT

{start} <-> {end} : provisioning flow

@enduml
```