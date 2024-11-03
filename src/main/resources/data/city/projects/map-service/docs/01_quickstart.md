# Quickstart

This Quickstart guides you to create a project, layer and place using APIs.  

## Prerequest

Before you start, you should

* have a Woven ID from Agora ID team
* know how to get an access token for a http request
* provision a Map Service from the Backend Utility team so that you know you map project uuid.

---

## Let's Start

Let's build a simple map using the Map APIs. Here are the high level steps

* create a project (Places Service API)
* create a layer and under the project (Places Service API)
* create a POI and under the layer (Places Service API)
* create a review for the POI (Reviews Service API)
* upload a jpg image for the POI and review (Media Service API)

### Create a project
The Utility team creates a project for you, you should receive a project ID from the Utility team, eg Project ID: 057b03c9-e5d8-4d60-abdf-615037f0082f, no action is requested for you.

### Create a layer and under the project
Project creates a layer with the following request, the admin_ids, editor_ids, viewer_ids can be empty if you don't have a user list. The user can be update to the layer later using API or Authoring Portal. 

Example Request:

POST /map-service/api/v1/layers

```
Body
{"name": "Woven Project layer",
"description": " Woven layer Description",
"project_id": "057b03c9-e5d8-4d60-abdf-615037f0082f",
"admin_ids": ["8e37f064-0752-4743-8e32-19a92793c511"],
"editor_ids": ["6b9bc946-aeec-4fd3-af64-424ae9a6bd82"],
"viewer_ids": ["806e5648-9ebc-4d3b-9322-2feb7df729a2", "291213f2-1521-4862-8c65-420b660381a3"]
}
```

New layer ID Response:

```
Headers 
location: 007fa901-c53f-43d9-86cd-ee299514d152
```

### Create a POI and under the layer
Example Request:

POST /map-service/api/v1/places?layer=layer-ID

```
Body
{
   "address": "3 Chome-2-1 Test Address, Chuo City, Tokyo 103-0022",
   "categories": [ "4d1d847e-e578-42c6-bde0-2b1db63798a2" ],
   "geo_data": {
       "center": "SRID=4326;POINT(0.00 0.00 0.00)"
   },
   "name": "Tutorial POI A",
   "open_since": "1987-11-27",
   "opening_times": {
       "1": { "hours": [
               { "open": "09:00:00+09:00", "closed": "12:00:00+09:00" },
               { "open": "13:00:00+09:00", "closed": "20:00:00+09:00" }
           ],
           "note": "Last Order at 19:30"
       },
       "2": { "hours": [
               { "open": "09:00:00+09:00", "closed": "12:00:00+09:00" },
               { "open": "13:00:00+09:00", "closed": "21:00:00+09:00" }
           ],
           "note": "Last Order at 20:30"
       }
   },
   "owner_woven_id": "4d1d847e-e578-42c6-bde0-2b1db63798a2",
   "telephone": "+31123456789",
   "website": "https://www.example.com"
}
```

New POI ID Response:

```
Headers 
location: /c9e68167-464d-40c3-adb5-22d13da882c3
```

### Create a review for the POI
Example Request:

POST /map-service/api/v1/api/v1/reviews

```
Body
{
  "place_id": "a1a309d9-645d-414d-a27f-ba05cd47e14f",
  "rating": 5,
  "text": "The food was great!"
}
```

New Review ID Response:

```'
Body
{
  "review_id": "a1a309d9-645d-414d-a27f-ba05cd47e14f"
}
```

### Upload a jpg image for the POI and review

Example Request:

POST /map-service/api/v1/media/

```
body (form-data)
photoInfo:
{
  "placeId": "4be7cd38-2506-4730-9d8e-063b210837ce",
  "reviewId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "text": "string"
}
photoImage:
Image binary
```

New Media ID Response:

```
body
{
    "result": "Success",
    "photoId": "e68ff6f3-f314-41c0-8ee0-496b062cd558"
}
```
