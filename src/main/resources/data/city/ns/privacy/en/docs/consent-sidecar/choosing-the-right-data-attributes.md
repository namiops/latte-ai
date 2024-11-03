# What is a data attribute?

In the consent configuration for both Drako and Consent sidecar, a list of data attributes need to be reflected. This is a way to describe data types used in the [consent check](https://developer.woven-city.toyota/docs/default/Component/consent-management-service/en/consent/#consent-check-logic).

This document helps identify data attributes for your service endpoints and explains some of the more complex cases.

# Possible data attributes

Currently, there is no canonical, closed list of data attributes that developers have to adhere to. Consent-checking components don't require data attributes specified in the configurations to belong to any specific collection or follow some specific format.

However, **we strongly recommend** you to use values from [this list](https://docs.google.com/spreadsheets/d/1UtdYCk7UoJ3-DdGQlytkcmpPPdYAv1jmQY1DYLIdIuE/edit#gid=804982504&range=F2) provided by the Agora Privacy Team. Using values from this list will ensure consistent attribute format/naming across the consent configurations and help with potential integration with other services or migration to a canonical list in the future.

**NOTE:** Since the above list is non-exhaustive enough, it is possible to create a new data attribute. **We strongly recommend** following the format of the existing attributes when creating new attributes. Please contact the [Agora Consent Team](https://toyotaglobal.enterprise.slack.com/archives/C06230AUVSS) if you need help with developing a new data attribute name.

# Identifying data attributes per endpoint

The data attribute list depends on the data returned by the given endpoint. The list **must** include only attributes describing personal information.

## Selecting data attributes to include in the configuration

In cases where some piece of personal information is used to make a request, the consuming service would already have access to it, so checking consent for it would be redundant. Therefore, it does not need to be included in the configuration.

Let's consider the following example:

A resource server has the endpoint `/person_name/{user_id}` that returns the following data:

    {
        "user_id": 12345
        "person_name": "Taro Tanaka",
    }

Looking at the response, we can deduce that the list of data attributes for it could be something like: `CITY_USER_ID, PERSON_NAME`

However, to retrieve the data, the consuming service had to make a request providing the user ID in the first place. The request path would've been something like `/person_name/12345`. Therefore, we don't need to check consent for sharing `CITY_USER_ID` with the consuming service.

In short, in the above example, the data attribute list could contain only one element: `PERSON_NAME`.

## Optional fields in the response

In some cases, the response from a resource server might include optional fields.

For example, the endpoint `/personal_data/{user_id}` returns the following responses for different users:

`/personal_data/12345`:

    {
        "user_id": 12345
        "person_name": "Taro Tanaka",
    }

`/personal_data/67890`:

    {
        "user_id": 67890
        "person_name": "Satoshi Nakamura",
        "birthdate: "1990-06-01"
    }

In the above example, we can see that the birthdate data is not included in the response for the user "Taro Tanaka". This user may update their record and save their birthdate information in the resource server at some point.

The data attribute lists defined in this document are **static** and cannot adapt depending on the response content. Additionally, the absence or presence of some information may also reveal personal information. Imagine that we have an optional field for "employment". An empty response _may_ indicate that the subject is currently unemployed. To ensure a full consent check in such cases, we need to specify **all the possible data attributes that an endpoint can return**.

Therefore, for the given example, the list of data attributes for this endpoint would be: `PERSON_NAME, PERSON_BIRTHDATE`.

# Examples

Let's go over some examples using [BURR v2 API](https://developer.woven-city.toyota/catalog/default/api/burr-core-v2/definition) as a source and prepare data attribute lists for each of them.

## Simple personal data case

For the [GET persons/{wovenID}/basicInfo](https://developer.woven-city.toyota/catalog/default/api/burr-core-v2/definition#/Person%20API/get_persons__wovenId__basicInfo) endpoint, the response could look something like this:

    {
        "wovenId": "string",
        "name": {
            "normative": {
                "primaryName": "Adam",
                "givenNames": [
                    "Adam"
                ]
        },
        "phonetic": {
            "primaryName": "Adam",
            "givenNames": [
                "Adam"
            ]
        },
        "latin": {
            "primaryName": "Adam",
            "givenNames": [
                "Adam"
            ]
        }
        },
        "dateOfBirth": "2024-03-18",
        "emailAddress": "adam@woven-planet.global",
        "phoneNumber": "+133"
    }

Looking at all the data types returned by the endpoint and using the existing data attributes from [the list](https://docs.google.com/spreadsheets/d/1UtdYCk7UoJ3-DdGQlytkcmpPPdYAv1jmQY1DYLIdIuE/edit#gid=804982504&range=F2) mentioned above, we can deduce the following list of data attributes for it:  
`EMAIL_ADDRESS, PERSON_BIRTHDATE, PERSON_GIVEN_NAME, PERSON_NAME, PERSON_PRIMARY_NAME, PHONE_NUMBER`.

**NOTE:** Notice that `CITY_USER_ID` is not included in the list, as explained in ["Selecting data attributes to include in the configuration"](#selecting-data-attributes-to-include-in-the-configuration).

## Unidirectional relationships and data subjects

Now let's consider another endpoint, [GET persons/{wovenID}/emergencyContactInfo](https://developer.woven-city.toyota/catalog/default/api/burr-core-v2/definition#/Person%20API/get_persons__wovenId__emergencyContactInfo):

    {
        "wovenId": "string",
        "name": "Adam",
        "phoneNumber": "+9273866"
    }

From the above response, we can assume that the data attribute list should look something like: `PERSON_NAME, PHONE_NUMBER`.

However, it is important to remember which entity is the _data subject_ of the endpoint. The required consent check when accessing the data is conducted on the user whose Woven ID was provided in the request and not on the user "Adam". The emergency contact information is a record owned by another user that is pointing to the someone named Adam as a contact. Therefore, the data attribute list should be simply: `EMERGENCY_CONTACT`.

You may be wondering: seeing as Adam's name and phone number are being shared, shouldn't we check consent with him as well? But according to the current consent model, this is **not necessary**, as we only need to check consent with the data subject to share information that they have provided unilaterally. This information is not linked to Adam's Woven ID or user profile by API.

For the above case, we can simply assume that Adam himself gave verbal permission beforehand or received a notification informing him that he has been saved in the system as the data subject's emergency contact person.

### Bi-directional relationships and shared data attribute names

In some cases, data on both sides of the relationship are accessible by API. One example is the [Guardianship API](https://developer.woven-city.toyota/catalog/default/api/burr-core-v2/definition#/Guardianship%20API), where we can request data as to which users have guardianship over the subject using the [GET persons/{wovenID}/guardianship](https://developer.woven-city.toyota/catalog/default/api/burr-core-v2/definition#/Guardianship%20API/get_persons__wovenId__guardianship) endpoint:

    {
        "wardId": "child-id",
        "guardianIds": [
            "parent-id"
        ]
    }

Similarly, we can check wards (in this example the children) of the subject with the [GET persons/{wovenID}/wards](https://developer.woven-city.toyota/catalog/default/api/burr-core-v2/definition#/Guardianship%20API/get_persons__wovenId__wards) endpoint:

    [
        {
            "wovenId": "child-id",
            "basicInfo": {
                "name": {
                    "normative": {
                        "primaryName": "Peter",
                        "givenNames": [
                            "Peter"
                        ]
                    },
                    "phonetic": {
                        "primaryName": "Peter",
                        "givenNames": [
                            "Peter"
                        ]
                    },
                    "latin": {
                        "primaryName": "Peter",
                        "givenNames": [
                            "Peter"
                        ]
                    }
                },
            "dateOfBirth": "2024-03-18",
            "emailAddress": "peter@woven-planet.global",
            "phoneNumber": "+182095266"
            }
        }
    ]

As you can see, the two responses above refer to the same relationship but describe different sides of it. In this case, you can use the **same data attribute name** in both data attribute lists, as they both deal with the "guardianship-ward" relationship.

In other words, the data attribute list for both of these endpoints could be: `PERSON_GUARDIANSHIP`.

## Connectable personal information

In some cases, the data in question might not look like personal information at first glance. But when connected with some additional observable facts, it could be used to identify someone.

Consider the [GET persons/{wovenID}/trainingQualification](https://developer.woven-city.toyota/catalog/default/api/burr-core-v2/definition#/Certification%20API/get_persons__wovenId__trainingQualification) endpoint below:


    {
        "wovenId": "some-id",
        "trafficRuleTrainingCompletedDate": "2024-03-18",
        "trafficRuleTrainingRevision": "some-revision"
    }

The response includes data about the qualifications (i.e., traffic rule training) obtained by the subject. This information could be potentially be combined with responses from some other APIs or a list of people who completed training on the specified date to help deduce the identity of the individual. Therefore, the data attribute list for this endpoint should be: `TRAINING_QUALIFICATION`, to ensure that we conduct the necessary consent check for sharing the qualification details.

## More examples

For more examples of data attribute lists, see the [BURR Consent sidecar configuration](https://github.com/wp-wcm/city/blob/main/infrastructure/k8s/common/brr/core-v2-0.1.3/sidecar-config.yaml) used by the [BURR v2 API](https://developer.woven-city.toyota/catalog/default/api/burr-core-v2/definition).

# Dynamic data attribute lists

In this document, we have focused on defining static lists of data attributes per endpoint. Dynamic data attribute lists are currently not supported. If you need this functionality, please [contact us](https://toyotaglobal.enterprise.slack.com/archives/C06230AUVSS) directly.
