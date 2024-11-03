# Face Identifier Service

## Overview

- functions
  - This service provides a single source of truth for face enrollment and identification in the Woven City. A human face can be enrolled into the service and all touch point applications can identify the user by calling the identification service.
    - Face enrollment - Enroll user's face in the system by uploading a user's photo. The photo is not stored, just used to calculate a feature vector.[^1]
    - Face identification - Identify the enrolled user by providing a photo or calculated feature vector*.
    - By using the face identification API, touch point applications can provide a better user experience for user authentication and authorization.
- Restrictions for Usage
  - Currently, the service is deployed on in-house AWS. So if you would like to connect to the service, clients should be in WP network. And, if clients are on Agora, you need to set up the private link.
    - In the future, the service will be migrated to Agora. And feature vectors will be able to be managed for each purpose. e.g. clients can enroll only users who are allowed to enter restricted areas.

## Links

- [confluence page](https://confluence.tri-ad.tech/x/qx3nCw)
- [open APIs](https://developer.woven-city.toyota/catalog/default/api/face-identifier-api)
- [source code](https://github.com/wp-wcm/city/tree/main/projects/face-identifier-service)

[^1]: feature vector is float array data that is calculated according to facial features in a provided photo.
