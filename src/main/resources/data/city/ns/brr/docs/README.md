!!! warning
    This is a document for the original version of BURR service that is going to be replaced with [BURR v2 service](https://developer.woven-city.toyota/docs/default/Component/burr-v2) soon. Since this version of BURR won't be available in production while BURR v2 service is intended to provide stable features/operation for the very first city open, we highly recommend to consider to start working with BURR v2 instead of this.

# Overview

The Basic Users and Residents Register (BURR) (formerly BRR = Basic Residents 
Register) is an API for Woven inventor and
operator services on the Agora platform to utilize personal information about
Woven City users and residents. Beyond some basic data required to operate the 
city, this service will only collect and store information about users as they 
opt in to sharing it.

Users will also be able to control which services can access their data stored
in BURR. Sharing their data allows users to better utilize various services
and utilities Woven City will provide, for a more engaging tailored experience.
With the user's consent, their data can be used by inventor and operator 
services, with the goal of enhancing city facilities, services and experience 
for all users and residents.

## Further Reading and Resources
For further information about BURR, please see the links below:

- For information on all the API endpoints that BURR offers, see the 
[BURR API documentation](http://go/burr-api).
- For further background reading about BURR, its purpose and the motivation
behind it, please refer to the Product Requirements Document 
[TN-0174](http://go/tn-0174). 
- For a description of BURR's domain models, please see
[Technical Note TN-0146](http://go/tn-0146).
- For questions, please visit the [#wcm-org-agora-burr Slack channel][burr-slack].
[BURR Consent documentation](https://developer.woven-city.toyota/docs/default/Component/consent-management-service).
- For Developer Portal Consent information
[Authorization for Woven ID, Agora Technical Note](http://go/tn-176).
- For further information about Authorization and Keycloak

## Status

BURR is currently in the alpha stage of development. Therefore, there is a
possibility that, during this phase, some information stored will be dropped at
some point due to the API version upgrade or redesign of the data schema. Of
course, we will make an effort to migrate all existing data during upgrades and
new releases, but we are unable to make strong guarantees.

Also note that although authorization has been enabled within the BURR, within
non-production clusters services may be skip-consent listed. This allows them
to bypass user consent checks when fetching or updating BURR user data.
We recommend that consumers/users of any non-production deployment of
BURR assume that any and all data stored is retrievable by any
service on the Agora platform. And assume that all user information within BURR
can be acccessed by anybody with access to the company network.
It is therefore strongly recommended that storing any real PII be avoided.

## Consent Integration

The BURR has been integrated with the Agora consent management flow.
When calling the BURR API from within the cluster, BURR will automatically check
with the Consent service whether the data subject has granted consent for your
service to access that data. The data subject is the user whose data your
service is trying to access.
See the [Consent Service developer portal documentation](https://developer.woven-city.toyota/docs/default/Component/consent-management-service) for further information.

When calling the BURR API from outside of the cluster, consent will _not_ be
checked, to allow service developers to test out the API manually during development.
However, this will **not** be possible in the production cluster.

## Getting Started Using BURR

This section is to help you get started as a BURR user, starting from how to
reach the BURR API and showing examples of basic usage patterns.

If you have any questions or feedback about this documentation, please contact
the BURR team anytime on our Slack channel [#wcm-org-agora-burr][burr-slack].

### Calling the BURR API

BURR is currently deployed on the Development Cluster, the API service
is listening at the following base URL

```
http://api.brr.svc.cluster.local/api/v1alpha
```

In order to make your development easy, we've exposed the API to outside of the
`dev` cluster but limited to calls from within the company network.
You can also access it from inside the company network from
your local machine with the following base URL

```
https://brr.cityos-dev.woven-planet.tech/api/v1alpha
```

Please note that there are no plans to expose the API to outside of the
cluster in production environment. But the user image-serving endpoint will be
an exception since client applications need to access it from outside of the
cluster.

BURR also has another instance of the service that is completely independent from the
above one in terms of data and any states. The base URL is:

```
# Cluster internal
http://api.brr-b.svc.cluster.local/api/v1alpha
# Outside of the cluster
https://brr-b.cityos-dev.woven-planet.tech/api/v1alpha
```

This is intended to provide another environment for service developers on the
dev cluster for testing. You may use this for any development or testing
purpose. For example, your team may use `brr` for your development environment,
and `brr-b` for your staging environment.

### Prerequisite

BURR requires a Woven ID issued by Agora's identity platform (Keycloak).
This is because BURR manages all person records with a `personId`,  which is
actually a Woven ID that is generated by the identity platform.
Please make sure the user you are registering in BURR has an identity
platform Keycloak account before proceeding.
See the [Authorization for Woven ID 2.0](http://go/tn-176) Agora Technical Note for further background.

### Creating a new Person

Creating a new Person can be done with [`POST /person/{personId}`](https://developer.woven-city.toyota/catalog/default/api/brr-api/definition#/default/post_person__personId_),
which takes a Woven ID as a path parameter.

```go
package main

import (
    "bytes"
    "encoding/json"
    "fmt"
    "github.com/google/uuid"
    "net/http"
)

const (
    ApplicationJson = "application/json"
    BaseBurrUrl      = "https://brr.cityos-dev.woven-planet.tech/api/v1alpha"
)

func main() {
    newPersonId := uuid.New()

    //Let's start with some data
    //A root of the BURR is a person, so let's create some basic information
    newPerson := PostPersonPersonIdJSONBody{
        BasicInfo: BasicInfo{
            Name: PersonName{
                Normative: FullName{
                    GivenNames:  []string{"Y'shtola, Sun"},
                    PrimaryName: "Rhul",
                },
            },
        },
        UserTypes: []UserType{Administrator, Guest},
    }
    fmt.Println("creating person - " + newPersonId.String())

    data, err := json.Marshal(newPerson)
    if err != nil {
        fmt.Println("error - couldn't marshal JSON: " + err.Error())
    }

    //Call the POST API Endpoint
    personUrl := fmt.Sprintf("%s/person/%s", BaseBurrUrl, newPersonId.String())
    postResponse, err := http.Post(personUrl, ApplicationJson, bytes.NewBuffer(data))
    if err != nil {
        fmt.Println("error - unable to post: " + err.Error())
    }

    getResponse, err := http.Get(personUrl)
    if err != nil {
        fmt.Println("error - unable to get: " + err.Error())
    }
    if getResponse.StatusCode != http.StatusOK {
        fmt.Println("error - unable to get person")
    }
    defer getResponse.Body.Close()

    var person Person
    if err = json.NewDecoder(getResponse.Body).Decode(&person); err != nil {
        fmt.Println("error - could not unmarshal response: " + err.Error())
    }

    //Prints 'get - person ID [<ID>], person given names [[Y'shtola, Sun]]'
    fmt.Printf("get - person ID [%s], person given names [%v]\n", person.Id, person.BasicInfo.Name.Normative.GivenNames)
    //Prints 'get - person ID [<ID>], person primary name [Rhul]'
    fmt.Printf("get - person ID [%s], person primary name [%s]\n", person.Id, person.BasicInfo.Name.Normative.PrimaryName)
}
```

IDs for Person Entities **must be unique**: if we were to try and run the
same ID generated above and create another POST, the service returns a general
error response

```go
    data, err = json.Marshal(newPerson)
    if err != nil {
         fmt.Println("error - couldn't marshal JSON: " + err.Error())
    }

    postResponse, err = http.Post(personUrl, ApplicationJson, bytes.NewBuffer(data))
    if err != nil {
        fmt.Println("error - unable to post: " + err.Error())
    }
    defer postResponse.Body.Close()

    var duplicateResponse map[string]interface{}
    if err = json.NewDecoder(postResponse.Body).Decode(&duplicateResponse); err != nil {
         fmt.Println("error - could not unmarshal response: " + err.Error())
    }

    //Prints 'person already exists. personId: <ID>
    fmt.Printf("%s\n", duplicateResponse["message"])
```

### Updating a Person's contact

You can manage a person's contact information with Contact API endpoints.

- [`GET /person/{personId}/contact`](https://developer.woven-city.toyota/catalog/default/api/brr-api/definition#/default/get_person__personId__contact)
- [`PUT /person/{personId}/contact`](https://developer.woven-city.toyota/catalog/default/api/brr-api/definition#/default/put_person__personId__contact)

Let's update the person with some Contact information with PUT

```go
    putBody := PutPersonPersonIdContactJSONBody{
        EmailAddresses: &[]string{"ysh@wol.com", "matoya@wod.com"},
        PhoneNumbers:   &[]string{"+818034567890"},
    }
    data, err = json.Marshal(putBody)

    //Let's call the PUT API
    //If successful the PUT returns a 204 per the API Specification
    contactUrl := fmt.Sprintf("%s/person/%s/contact", BaseBurrUrl, newPersonId.String())
    putRequest, err := http.NewRequest(http.MethodPut, contactUrl, bytes.NewBuffer(data))
    if err != nil {
        fmt.Println("error - unable to create put request: " + err.Error())
    }
    putRequest.Header.Set("Content-Type", ApplicationJson)

    putResponse, err := http.DefaultClient.Do(putRequest)
    if err != nil {
        fmt.Println("error - unable to put: " + err.Error())
    }
    if putResponse.StatusCode != 204 {
        fmt.Println("error - wrong status code returned")
    }

    getResponse, err = http.Get(contactUrl)
    if err != nil {
        fmt.Println("error - unable to get: " + err.Error())
    }

    var contact Contact
    if err = json.NewDecoder(getResponse.Body).Decode(&contact); err != nil {
        fmt.Println("error - could not unmarshal response: " + err.Error())
    }

    //get - person ID [<ID>], emails [&[ysh@wol.com matoya@wod.com]]
    fmt.Printf("get - person ID [%v], emails [%v]\n", contact.PersonId, contact.EmailAddresses)
    //get - person ID [<ID>], phone numbers [&[+818034567890]]
    fmt.Printf("get - person ID [%s], phone numbers [%v]\n", contact.PersonId, contact.PhoneNumbers)
```

### Access to a Person's profile

You can manage a profile with Profile API endpoints. The profile includes basic
information, contacts, user types, and a few dedicated attributes for a profile.

- [`GET /person/{personId}/profile`](https://developer.woven-city.toyota/catalog/default/api/brr-api/definition#/default/get_person__personId__profile)
- [`PUT /person/{personId}/profile`](https://developer.woven-city.toyota/catalog/default/api/brr-api/definition#/default/put_person__personId__profile)

### Manage Avatar Image

You can upload Person's avatar images to the BURR. Uploaded images can be served
to the client applications. Use Image API endpoints for this purpose.

So, how can we manage avatar images? You can upload images via [`POST /image`](https://developer.woven-city.toyota/catalog/default/api/brr-api/definition#/default/post_image).
The API returns `imageId` that is an identifier of each image. Secondly, you
need to connect the uploaded image to a person via [`PUT /person/{personId}/profile`](https://developer.woven-city.toyota/catalog/default/api/brr-api/definition#/default/put_person__personId__profile)
that takes `avatarImageId` in the request body. Next, you can see an avatar
image URL in the response of [`GET /person/{personId}/profile`](https://developer.woven-city.toyota/catalog/default/api/brr-api/definition#/default/get_person__personId__profile).
Finally, you can fetch the image via URL.


[burr-slack]: http://go/burr-slack
