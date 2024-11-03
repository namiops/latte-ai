# Data Design

This document describes the design of data handled by AC user information service Backend.

## Log Database

This database is a log that records the operations of the user information service backend.

Database: SecureKVS (CouchDB)
Database Name: user-info-logs
(The actual database name is namespace + database name, e.g. `ac-access-control_user-info-logs` for the gen1 dev cluster.)

### Log document schema

| Field name      | Type   | Description                                                 |
| :-------------- | :----- | :---------------------------------------------------------- |
| +operatorId     | string | Woven ID of the user who operated user information service  |
| +timestampMs    | number | Unix timestamp (ms) of when the log was record              |
| +method         | string | Method of the executed API                                  |
| +host           | string | host of the executed API                                    |
| +path           | string | Path of the executed API                                    |
| +pathParameter  | object | Path parameters of the executed API                         |
| +queryParameter | object | Query parameters of the executed API                        |
| +resultCode     | number | Status code of the response to the API executed by the user |
| +requestId      | string | The value of `X-Request-ID` header in the request           |

#### API list for logging target and log example

The APIs to be logged are as follows.

- GET /users/search
- GET /users/:wovenId/details
- GET /resource-groups/:resourceGroupName

Example:

- If there is a path parameter (Example is `GET /users/:wovenId/details`)

```json
{
  "+operatorId": "01234567-0123-0123-0123-0123456789ab",
  "+timestampMs": 1688020433123,
  "+method": "GET",
  "+host": "https://ac-access-control.cityos-dev.woven-planet.tech",
  "+path": "/user-info/api/v1/users/:wovenId/details",
  "+pathParameter": {
      "wovenId": "11234567-0123-0123-0123-0123456789ab"
  },
  "+resultCode": 200,
  "+requestId": "000011112222-3333-4444-5555-666677778889",
}
```

- If there is a query parameter (Example is `GET /users/search`)

```json
{
  "+operatorId": "01234567-0123-0123-0123-0123456789ab",
  "+timestampMs": 1688020433123,
  "+method": "GET",
  "+host": "https://ac-access-control.cityos-dev.woven-planet.tech",
  "+path": "/user-info/api/v1/users/search",
  "+queryParameter": {
      "searchKey": ["name"],
      "searchTexts": [
        "firstName",
        "givenName"
      ]
  },
  "+resultCode": 200,
  "+requestId": "000011112222-3333-4444-5555-666677778889",
}
```