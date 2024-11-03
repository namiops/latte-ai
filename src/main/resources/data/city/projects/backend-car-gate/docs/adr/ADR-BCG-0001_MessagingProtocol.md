# ADR-BCG-0001 Messaging Protocol

| Status | Last Updated |
|---|---|
|Approved| 2024-XX-XX |

## Context

### Context

- BCG(Backend of Car Gate) is responsible for the two messaging tasks listed below.
  1. Receive messages delivered at each status change from the car gate.
  2. Deliver the messages to the vehicle at 2 Hz.
 ![image](./img/ADR-BCG-0001-img-01.png)
- BCG uses Agora's RabbitMQ (iota) for messaging.
  - Agora's RabbitMQ (iota) supports MQTT and AMQP.
  
### Problem

- The messaging protocol (MQTT or AMQP) to be used by BCG has not been decided.


### Factors BCG looks for in messaging (level of importance)

##### High
 - Data Loss: Not allowed. The car gate only sends a message for each state change. If lose it, BTS never get it again. (*1)
 - Communication Latency: The vehicle uses car gate status information to make a Go or Stop decision.(*2)
 - Messaging Ordering: Not allowed. There are [cases](https://docs.google.com/presentation/d/15M6alcMvqnsV9vkEngJo4Sw8XZCMi2g2Dv3-z6HneLE/edit#slide=id.g2e05547df7c_0_21) where vehicles cannot pass through the gate due to order disruption.(*3)

##### Low
 - Load Balancing: Communication frequency is not high.(2Hz) (*4)
 - Data Duplication: Not a problem because it is not a command, just a status notification. (*5)
 - IoTCore Support: IoTCore is not used for this service. (*6)

## MQTT vs AMQP Comparison


| Aspect               | Importance | MQTT (QoS0) | MQTT (QoS1) with custom processing in Golang (*7) | AMQP                                           |
|----------------------|------------|-------------|---------------------------------------------------|------------------------------------------------|
| Data Loss            | High (*1)  | ❌          | ✔️ No data loss.                                   | ✔️ No data loss. |
| Communication Latency| High (*2)  | ✔️           | ✔️ few 1ms ~ few 10ms                              | ✔️ few 10ms ~ 100ms |
| Message Ordering     | High (*3)  | ❌          | ❌ By custom processing in Golang (*7)             | ✔️ Ensures message ordering through queues.       |
| Implementation Cost  | Medium     | ✔️           | ✔️ The protocol is more simple and there is much information available online. | ➖ The protocol is more complex and there is little information available online. |
| Adoption (FSS)       | Medium     | ✔️           | ✔️ Backend-Traffic-Signal                          | ✔️ ac-management-service-api-server |
| Load Balancing       | Low (*4)   | ❌          | ❌ Not Supported.                                 | ✔️ Supported. |
| Data Duplication     | Low (*5)   | ✔️           | ❌                                                | ✔️  |
| IoTCore Support      | Low (*6)   | ✔️           | ✔️ Supported.                                      | ❌ Not Supported.|


 - ✔️  : Excellent
 - ➖ : Neutral
 - ❌ : Poor
 - *7 : Older messages can be ignored by checking timestamp, but receiving order cannot be guaranteed.

## Decision Outcome

- AMQP is the preferred protocol for BCG.
  - We compared the High and Medium importance items and determined.

## Reference

- [Agora IoT Deep Dive](https://developer.woven-city.toyota/docs/default/component/iota-service/99_deepdive/)
- FSS documents.
  - [About AMQP and RabbitMQ](https://confluence.tri-ad.tech/pages/viewpage.action?pageId=430226500)
  - [MQTT vs AMQP for Access Control](https://confluence.tri-ad.tech/display/CISAM/MQTT+vs+AMQP+for+Access+Control)


## Note
- 2024-05-30 : Review conducted, [minutes (Japanese only)](https://confluence.tri-ad.tech/pages/viewpage.action?pageId=883038923)
- 2024-05-31 : Drafted, Originator: Yuichi Takahashi
- 2024-06-26 : fix decision from **MQTT(QoS1) with custom processing in Golang** to **AMQP**
