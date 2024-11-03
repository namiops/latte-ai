# Location Service

## Description

Purpose of this service is to consume messages from various location data providers and
serve an API for clients to get the location information of people within the Woven City.

## Structure

The Location Service consists of the following microservices:

* Camera Location Processing pipeline
1. Camera Location Processor - to collect Vision AI data
2. Camera Location Publisher - to aggregate Vision AI data to camera location data
3. Camera Detection Syncer - to sync identified Woven IDs with previously processed camera location data

* GPS Location Processing pipeline
1. GPS Location Processor - to collect and publish GPS location data

* Event Location Processing pipeline
1. Event Location Processor - to collect and publish Facility Event location data

* Location Geo Processor - to retrieve associated POI IDs from Map service for all data aggregated by the pipelines

* Location provider 
1. Location Data Manager - to manage the processed location data
2. Location API - to expose the on-demand location API for clients (current, last-seen, query)
3. Location Subscription Manager - to expose the location tracking subscription endpoint for clients
4. Location Tracking Publisher - to publish the notifications based on location tracking subscriptions

* Statistics provider
1. Statistics Data Manager - to request revere geocoding of processed location data 
2. Statistics Data Publisher - to aggregate geocoded statistics on processed location data
3. Statistics API - to expose Location congestion histogram API for clients

## Data retention policy

### Kafka

For all messages inside Location Service message queues and input queue (Vision AI),
data retention time is set to 3 days by adding the following configurations to MSK cluster on in-house AWS
(configuration name locationService rev.2)

```shell
log.retention.hours=72
log.message.timestamp.type=LogAppendTime
```

Second configuration needed to be set to do the expiration of old messages based on queue incoming time
rather than document time (to avoid timezone issues)

### MongoDB

For all persistent storages in Location Service (both for processed location data and tracking subscriptions),
data retention time is set to 7 days.
This can be done by setting a TTL index manually on in-house AWS for processed location and subscription collections.
TTL setting commands:

Location Data:

```shell
# connect to server
mongosh "mongodb://docdb-location-data.c6cb9uq4a01j.ap-northeast-1.docdb.amazonaws.com:27017" --username root --authenticationDatabase admin
# enter password
use location
db.collection.createIndex( { "timestamp": 1 }, { expireAfterSeconds: 604800 } )
```

Location Subscriptions:

```shell
# connect to server
mongosh "mongodb://docdb-location-subscriptions.c6cb9uq4a01j.ap-northeast-1.docdb.amazonaws.com:27017" --username root --authenticationDatabase admin
# enter password
use location-subscriptions
db.collection.createIndex( { "created_at": 1 }, { expireAfterSeconds: 604800 } )
```
