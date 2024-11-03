# Redis DB POC for Location service

## Description

Purpose of this service is to test if the Redis-powered DB is sufficient to cover all requirements of Location service.
Since MongoDB will not be supported by Agora, it is required to assess the feasibility of migration to Redis stack for persistent storage 
of Location data with the end goal of migrating the Location service to Agora.

## Functionality

This POC app mimics the functionality of Location Data Manager and Location API microservices and provides the following:
1. Kafka consumer to receive location data
2. Redis repository to store the location data and be able to query it by timestamps and Woven ID
3. API to query stored Location data
