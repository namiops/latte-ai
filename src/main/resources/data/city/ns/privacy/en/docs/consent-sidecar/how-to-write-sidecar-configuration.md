# Consent Sidecar configuration

Every consent sidecar needs to be configured with the details of the API it's protecting. 
If you're planning to deploy your own instance of it, it is necessary to prepare a suitable **yaml configuration file** providing the configuration. 
The following document helps to understand how to prepare said file.

The consent sidecar requires a configuration in order to know which request to perform consent checks for and which data to use in the process.
The file is parsed and loaded into the sidecar's memory on startup. Because of that, following a specific format inside the file is required.

Sidecar tries to match every HTTP request path and method to the ones included in the configuration.
If a path or method for a given path is **not included** in the configuration, sidecar forwards the request and **skips the consent check**.

## Configuration file format

Let's take a look at the following example configuration covering only one endpoint.   
In the following sections, we're going to analyze its parts one by one.

    api_version: v0
    paths:
        "/core/v2alpha/persons/:wovenId":
            GET:
                subject_id_source:
                    source_type: request_path
                    parameter_name: wovenId
                data_attributes:
                    - data_attribute_source:
                        source_type: static
                        name: "EMAIL_ADDRESS"
                    - data_attribute_source:
                        source_type: static
                        name: "PERSON_BIRTHDAY"
                    - data_attribute_source:
                        source_type: static
                        name: "PERSON_GIVEN_NAME"
                    - data_attribute_source:
                        source_type: static
                         name: "PERSON_NAME"
                    - data_attribute_source:
                        source_type: static
                        name: "PERSON_PRIMARY_NAME"


### API Version

To make introducing changes to the sidecar functionalities easier in the future, we've introduced the `api_version` field in the configuration file.  
Currently, the only possible value is: `v0`.

### Paths and methods

As mentioned, the sidecar checks if the path for incoming requests is listed in the configuration.  
`paths` is the object storing all the paths, HTTP methods, and details needed for the consent check.  
When listing paths, it is important to provide them in their full format, for example `/core/v2alpha/persons/:wovenId`, not `/persons/:wovenId`. If there are any path parameters, they must be written in a `:name` format.

For every incoming request, sidecar checks if the request path is included in the `paths` object and, subsequently, if its HTTP method is listed as well. Once a "full match" happens, the sidecar proceeds to perform a consent check.
If request path is included in the configuration, but HTTP method is different sidecar forwards the request skipping the consent check.
If no request methods are not included in the configuration, parsing the configuration will fail at the sidecar start.

To extend the above example, it's enough to just list another path by adding new attributes to the `paths` like:

    api_version: v0
    paths:
        "/core/v2alpha/persons/:wovenId":
            GET:
                (...)
        "/another/path/:wovenId":
            GET:
                (...)

Similarly, to extend the existing paths configuration, we can just add another HTTP method as follows:

    api_version: v0
    paths:
        "/core/v2alpha/persons/:wovenId":
            GET:
                (...)
            POST:
                (...)
            PATCH:
                (...)
Please note: **Currently only GET method is supported**.

### Data subjects and `subject_id_source` configuration

As mentioned in the [consent check logic](../consent/README.md#consent-check-logic-consent-check-logic), subject ID is one of the pieces of information necessary to perform a consent check.  
Object `subject_id_source` describes where said ID can be found for a given path and HTTP method. It always includes the `source_type` and supplementing parameters depending on the `source_type` value.

The `source_type` parameter corresponds to the location where the subject ID can be found in the request or response structures. The following sections will describe the currently supported locations.

#### Request path

If a subject ID is included in the request path, we need to set the `source_type` to `request_path` and additionally provide `parameter_name`.
The `parameter_name` value has to match one of the path parameters included in the path definition.  
For example, `subject_id_source` for the `/core/v2alpha/persons/:wovenId` path could be:

    subject_id_source:
        source_type: request_path
        parameter_name: wovenId

#### Request body JSON and Response body JSON

If a subject ID is included in the request or response JSON body, we can set the `source_type` to `request_body_json` or `response_body_json`.
Since the body can include other data as well it is necessary to provide an additional `query` parameter, which includes [JSONPath](https://datatracker.ietf.org/doc/rfc9535/) query pointing to the subject ID location in the JSON.

The following could be the example `subject_id_source` configurations:

    subject_id_source:
        source_type: response_body_json      # or request_body_json
        parameter_name: $.personId

#### Other source types

Please [contact us](https://toyotaglobal.enterprise.slack.com/archives/C06230AUVSS) if there are source types that are not listed above but are necessary for your consent configuration.

### Data attributes

Parallel to the `subject_id_source` - `data_attributes` array lists information that should be used for the consent check for the given path and HTTP method.
Each `data_attribute_source` included in the array corresponds to one data attribute used in the consent check.

**To understand which data attributes should be included in the configuration, please read the following ["Choosing the data attributes tutorial"](./choosing-the-right-data-attributes.md).**

#### Static data attributes source

Currently, the only supported type of data attribute is `static`  
`Static` type of data attribute source means that for each consent check for the given endpoint, the data attribute will always be used.  
Using `source_type: static` requires us to provide a `name` parameter as well, which describes the name of the data attribute.

Putting it all together, the following could be the configuration reflecting that **every consent check** for some example `/core/v2alpha/persons/:wovenId` endpoint will include `EMAIL_ADDRESS` data attribute:

    api_version: v0
    paths:
        "/core/v2alpha/persons/:wovenId":
            GET:
                (...)
                data_attributes:
                    - data_attribute_source:
                        source_type: static
                        name: "EMAIL_ADDRESS"


#### Other types of data attribute sources

Please [contact us](https://toyotaglobal.enterprise.slack.com/archives/C06230AUVSS) if there are other data attribute sources that are necessary for your consent configuration.

## Example configuration file

Please check [BURR consent sidecar configuration](https://github.com/wp-wcm/city/blob/main/infrastructure/k8s/common/brr/core-v2-0.1.3/sidecar-config.yaml) used by [BURR v2 API](https://developer.woven-city.toyota/catalog/default/api/burr-core-v2/definition) for an example of configuration currently running in Agora clusters.

## What's next?

Once the configuration file is finished. It's time to understand [how to deploy the consent sidecar](./how-to-deploy-sidecar.md).
