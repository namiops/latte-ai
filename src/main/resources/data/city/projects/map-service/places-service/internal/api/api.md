# Map-Service API specification

## Spatial Reference / Coordinate systems
A Spatial Reference System (SRS) (also called a Coordinate Reference System (CRS)) defines how geometry is referenced to locations on the Earth's surface. The map service supports many different spatial reference systems that are considered industry standards, along side the custom defined Woven City Coordinate System (WCCS). An overview of the supported coordinate systems and their details can be found at the following links:
* Stanard SRS: [EPSG](https://epsg.io/)
* Woven City Coordinate System: [Confluence page 1](https://docs.google.com/document/d/1iMfTQigXO285CepfnkkuKU7mvyK1cGB39fg-4_DN23E/edit) & [Confluence page 2](https://docs.google.com/document/d/1OVfqo48YljZ0tIIbK8-iWaROonNrwzm5CKORMNTJr9s/edit#heading=h.7melyu6zxvte)

## Query operations
Given the complexity of dealing with geospatial data the first version of the API was designed to provide basic geospatial query operations that can be combined to provide more powerful operations. The initial query options are: Inside(x), Envelops(x), Nearby(x) where x can be an existing place or a custom query location created by the caller.

### Inside(x)
The inside operation is used to find regions or points of interest that exist inside the area defined by X. Because of the nature of this operation the value X can only be a region of interest, as points of interest have no surface/volume.
By using the inside operation it is possible to find any points of interest that might be present inside a region of interest, for example:
* Finding all vending machines inside a building. (POI inside ROI)
* Finding all rooms inside a floor. (ROI inside ROI)

### Envelops(x)
The envelopes operator can be thought of as the inverse of the Inside operator: It is used to find all regions of interest that completely surround and envelop the query value X, where X is is a point or place of interest.
This operator allows the caller to expand a search outward of a given query place. Examples of this operation are:
* Finding the room in which a vending machine is located. (ROI enveloping POI)
* Finding the name of the building in a query restaurant is located. (ROI enveloping ROI)

### Nearby(x)
The nearby operator is used to not expand on or zoom in on a query location but instead is used to find relatively close points or regions of interest. This operator returns information on the nearest points or regions of interest that meet the query requirements. The operator will also allow for sorting on distance. Examples of this operation are:
* Finding the nearest toilet near my current location. (ROI nearby POI)
* Finding the nearest post box near my office building. (POI nearby ROI)
* Finding the nearest grocery store near my home. (ROI nearby ROI)


## Combining the operator
By combining the operators defined above it is possible to create more complex and powerful queries. Though it does not provide the full power of directly querying the database it does provide tools to combine into powerful queries. To give an example of a combination of operators: Finding all vending machines on my current floor by combining _Inside(Envelops(X))_.
Where X is my current position inside a room, the Envelops(x) call can return all regions of interest that I am currently surrounded by: My current room, the floor I am on, the building I am in. By selecting the floor I am on from these results I can use the Inside(floor) to find all vending machines points of interest that are present inside the current floor that I am on.
Though not as powerful as writing a single SQL query it does allow, via multiple queries, to still get the desired result.
 
All the operators listed above will allow for an ordering in the returned data sets. This should help with the selection and filtering of query results. This ordering operator is currently not yet included in the OpenAPI specification but will be added soon. For example: When querying the enveloping regions of interest around a query point it could be helpful to order the result set by volume: Sorting the regions from smallest to largest. This volumetric ordering ensures that _Room < Floor < Building_, since the volume of a room cannot exceed the volume of the building that the room is located in.
 
__Example of Implicit relationships between POI and ROI: [Inside(x) & envelop(x) operations](https://jamboard.google.com/d/1yDyf12g-N4-Dcu5oEFjC7TLTAKaeXV1NQ4XmpxiR3wM/viewer?f=0)__


## Additional information
The overview of all the category tags used to filter places: [Places category tags](https://docs.google.com/document/d/1G3oSBhiduIl7gMathAn9W2w6caTzskRAdz4RZuyc3CU/edit#)