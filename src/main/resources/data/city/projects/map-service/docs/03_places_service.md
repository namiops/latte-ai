# Places Service

## Overview
The Places service is a service developed within the Utility Team's Map Services and is used to manage Places of Interest (POI) and Regions of Interest (ROI) geospatial data, as well as corresponding meta data. This places services is the first service within the Map Services domain of the Utility team and as such a Proof of Concept (PoC) was first developed in Q2 2022 to learn more about the geospatial data domain -how to best manage this type of data- as well as learning more about the stakeholders that would be using the map services and how to best serve their needs.

---

## Geodata CRUD Operations
To maintain the map project hierarchy and geodata, CRUD APIs are provided.

API Specification:
https://developer.woven-city.toyota/catalog/default/api/places-service-api/definition

### Spatial Reference / Coordinate systems
A Spatial Reference System (SRS) (also called a Coordinate Reference System (CRS)) defines how geometry is referenced to locations on the Earth's surface. The map service supports many different spatial reference systems that are considered industry standards, along side the custom defined Woven City Coordinate System (WCCS). An overview of the supported coordinate systems and their details can be found at the following links:

Standard SRS: EPSG

---

## Query Operations

### Inside
The inside operation is used to find regions or points of interest that exist inside the area defined by X. Because of the nature of this operation the value X can only be a region of interest, as points of interest have no surface/volume.
By using the inside operation it is possible to find any points of interest that might be present inside a region of interest, for example:

* Finding all vending machines inside a building. (POI inside ROI)
* Finding all rooms inside a floor. (ROI inside ROI)

### Envelopes
The envelopes operator can be thought of as the inverse of the Inside operator: It is used to find all regions of interest that completely surround and envelop the query value X, where X is a point or place of interest.
This operator allows the caller to expand a search outward of a given query place. Examples of this operation are:

*Finding the room in which a vending machine is located. (ROI enveloping POI)
*Finding the name of the building in a query restaurant is located. (ROI enveloping ROI)

### Nearby
The nearby operator is used to not expand on or zoom in on a query location but instead is used to find relatively close points or regions of interest. This operator returns information on the nearest points or regions of interest that meet the query requirements. The operator will also allow for sorting on distance. Examples of this operation are:

* Finding the nearest toilet near my current location. (ROI nearby POI)
* Finding the nearest post box near my office building. (POI nearby ROI)
* Finding the nearest grocery store near my home. (ROI nearby ROI)
