# Overview

## What is the Map Service?

The Map Service provides a centralized hub to create, retrieve, update and delete Places (point of interest (POI)) and Areas (ROI), inside and/or outside Woven City. The service contains 5 components, 4 backend services and an Authoring Portal.

You can

* create a new map with inside and/or outside Woven City
* retrieve Places and/or Areas inside and/or outside Woven City
* use a query operator to query, eg nearby() to find any restaurant within my 500m radius
* add a comment for a place
* upload a photo for a place
* lookup a human readable place name using Geocoding service

---

## Components of the Map Service

* *Places Service*: Provides APIs to manage Places (aka point of interest (POI)) and Area (aka ROI). For example: Retrieve information on a restaurant inside the city: the location, opening hours, etc.
* *Reviews Service*: Provides APIs for users to comment and rate for a POI and ROI. For example: users can put their comments for a restaurant
* *Media Service*: Provides APIs for users to upload images for a POI and ROI. For example: users can upload an image for a restaurant
* *GeoCoding Service*: Provides a service to convert a coordinate information into a human readable name and vice-versa
* *Authoring Portal*: The Authoring Portal is a browser-based portal, users can manage Map service without any technical knowledge

---

## Prerequisites

The services are hosted in the Agora dev environment under the Utility team namespace.

### Network/Endpoints location
There is no extra network configuration required. Touchpoint application can access the Agora endpoint directly
Depending on your application location, different network setup is required. If your application is hosted

* *Agora dev environment:* http://map-service.utility.svc.cluster.local:8080/map-service/api/v1/
* *Woven network:* https://utility.cityos-dev.woven-planet.tech/map-service/api/v1/
* *Woven In-house AWS:* https://utility.cityos-dev.woven-planet.tech/map-service/api/v1/

### Backend-to-backend Service
The service is designed to support backend-to-backend connection only. The Frontend direct connection to the service is not supported. If you want to call the service from the frontend, eg, browser/mobile app, please consider other backend solutions, eg Backend-For-Frontend (BFF).

### Agora Authentication & Authorization
The Map Service is protected by the Agora, Agora Authentication & Authorization are enforced.

* A Woven ID service account credentials are required. Please contact the Agora ID team for an account.
* A Keycloak access token is required for each request to a map service endpoint using the service account credentials.

### Map Service Authorization
In order to have more fine-grained access control over map service resources, Map service defined a logical hierarchy and role-based authorization is associated with each resource. For the [hierarchy info] (./02_internal_hierarchy.md) for details.

### Provision a Map Service account
Contract the Utility team in slack channel, a map account will be created by the Utility team.

Please provide the following when contact Utility team:

* Project name
* Project description
* Where is your application hosted, Agora/in-house AWS?
* Woven ID of project administrator, the ID is assigned as a project administrator
* How many estimated POIs are stored
* How many estimated API requests /second
* What is the expected response time
* Do you need schedule Map workflow