# Camera Restreaming Service

## Description
Purpose of this service is to consume camera frame info messages from Vision AI and
provide an API for Vision AI frontend to subscribe and receive camera frame info messages
by wovenID and cameraID

## Structure
The Camera Restreaming Service consists of the following microservices:
1. Restreamer - to consume Vision AI data and restream messages based on active subscriptions on wovenIDs/cameraIDs
2. Subscription Manager - to provide an API for subscribing to updates by wovenID/cameraID

## Data retention policy
### Kafka
For all messages from input queue (Vision AI), 
data retention time is set to 3 days by adding the following configurations to MSK cluster on in-house AWS
(configuration name locationService rev.2)
```shell
log.retention.hours=72
log.message.timestamp.type=LogAppendTime
```
Second configuration needed to be set to do the expiration of old messages based on queue incoming time 
rather than document time (to avoid timezone issues)
### MongoDB
For the persistent storage in Camera Restreaming Service (subscriptions),
data retention time is set to 7 days.
This can be done by setting a TTL index manually on in-house AWS for subscription collections.
TTL setting commands:

Subscriptions:
```shell
# connect to server
mongosh "mongodb://docdb-camera-frame-subscriptions.c6cb9uq4a01j.ap-northeast-1.docdb.amazonaws.com:27017" --username root --authenticationDatabase admin
# enter password
use subscriptions
db.collection.createIndex( { "timestamp": 1 }, { expireAfterSeconds: 604800 } )
```
