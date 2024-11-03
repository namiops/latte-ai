# Overview

## What is the Location Service?

The Location Service provides a centralized API to retrieve the location information of people within the Woven City based on data provided from various sources of location data. 

Sources of location data supported by the service:
* Vision AI cameras
* Woven App GPS
* Facility events

The service contains 12 backend microservices.

You can

* retrieve current location for a Woven ID (near real time)
* retrieve 'last seen' location for a Woven ID (last record in the database)
* do a time-based query for location data for a Woven ID (receive a list of location records within a time range)
* subscribe to receive location updates for a Woven ID via an HTTP callback (service will send a request to the provided URL)
* do a time-based location congestion histogram query for one or multiple Place IDs (Map service POI ID)

## Prerequisites

The services are hosted in the Agora Speedway environment.

### Network/Endpoints location
There is no extra network configuration required. Touchpoint application can access the Speedway endpoint directly
Depending on your application location, different network setup is required. If your application is hosted

* *Speedway PROD environment:* https://location-service.woven-city-api.toyota/location-service/api/v1
* *Speedway DEV environment:* https://dev-location-service.woven-city-api.toyota/location-service/api/v1
* *Woven In-house AWS:* http://common-backend.tri-ad.tech/location-service/api/v1

### Backend-to-backend Service
The service is designed to support backend-to-backend connection only. The Frontend direct connection to the service is not supported. If you want to call the service from the frontend, eg, browser/mobile app, please consider other backend solutions, eg Backend-For-Frontend (BFF).

### Agora Authentication & Authorization
The Location Service is protected by the Agora, Agora Authentication & Authorization are enforced.

* A Woven ID service account credentials are required. Please contact the Agora ID team for an account.
* A Keycloak access token is required for each request to a map service endpoint using the service account credentials.

## Links

- [confluence page](https://confluence.tri-ad.tech/x/qFoODQ)
- [tutorial pages](https://confluence.tri-ad.tech/x/n59fNQ)
- [source code](https://github.com/wp-wcm/city/tree/main/projects/location-service)
