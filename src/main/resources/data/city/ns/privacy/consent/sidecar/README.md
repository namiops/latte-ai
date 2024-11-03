# Consent Sidecar

Consent Sidecar is a reverse proxy implementation enforcing consent in the http communication.
It maps requests and responses into http [contexts](../sidecar/src/contexts.rs) and decides whether or not to share data from the resource server according to the results of consent checks by the consent service.

![Sidecar Architecture](docs/consent-sidecar-architecture.png)

[Sidecar Architecture in Lucid](https://lucid.app/lucidchart/bdd9f622-6713-4130-ac63-bd48cfc21e98/edit?viewport_loc=-165%2C-92%2C3013%2C2094%2C0_0&invitationId=inv_7da1d21b-e9da-42e8-b0ed-82366e82c6dc)

## Short /src summary
- checker - checker project code parsing out client ID, calling `HttpContextParser` and making grpc calls to the Consent Service
- contexts - context traits that can used by an application using checker to enforce consent
- config - configurations for Sidecar
- logging - log formatter
- main - Starting point
- parser - code for `HttpContextParser` which based on provided contexts returns a list of users and data attributes required for consent checks
- xfcc - parse xfcc header

## Running / Manually Testing the Sidecar locally

For the moment, we test the sidecar with the address service and the food service from the Consent 101.

* Start all the services:

  * Start the Postgres docker container as described [here in the main consent README](../README.md#running-the-service-cargo-or-bazel) (with default values)

  * Start the consent service with default config

    ```shell
    bazel run //ns/privacy/consent
    ```

  * Start the address service from section 02 of the codelab (i.e. no middleware)
    * This service receives request to http://localhost:8081/address
      * POST: Keep pairs of a user id and an address during running
      * GET: Retrieve the address of the user id   

    ```shell
    bazel run //ns/privacy/consent_101/02_consent_setup/address_service
    ```
  
  * Modify and start the food service from section 04 of the codelab (i.e. including XFCC):
    * Change the port that the food service uses to talk to the address service from `8081` to the sidecar's port `8080` (in `ns/privacy/consent_101/04_xfcc_what_that/food_service/main.go`)
    * This service receives POST request to http://localhost:8082/feedme and request to http://localhost:{8081,8080}/address/{userId}
    * Run the food service:

    ```shell
    bazel run //ns/privacy/consent_101/04_xfcc_what_that/food_service
    ```
  
  * Start the sidecar(http://localhost:8080) with the config
    * If the config is missing for request items, sidecar doesn't check user's consent
    * Please look at [config examples](./example_configs/path_config.yaml) to check the style of sidecar config

    ```shell
    bazel run //ns/privacy/consent/sidecar -- --resource-server-api-config="{api_version: v0, paths: {'/address/:userId': {GET: {subject_id_source: {source_type: request_path, parameter_name: userId}, data_attributes: [{data_attribute_source: {source_type: static, name: USER_ID}}, {data_attribute_source: {source_type: static, name: CITY_ADDRESS_ID}}]}}}}"
    ```
    
    * If you want to disregard consent check result for the food service (or any other service) it's possible to configure it 
      by listing client id in the following env  variable:
    
      ```shell
      ADVISORY_ONLY_NAMESPACES=food-delivery-service
      ```
  
* Set up the data:

  * Add a service mapping entry for the food service in the consent service to save it to db:

    ```shell
    curl -i -X POST "http://localhost:3000/v2alpha/admin/service_mapping" \
        -H "Content-Type: application/json" \
        -d '{ "service_name": "FooDelivery", "clients": [ {"client_id": "food-delivery-service"} ] }'
    ```

  * Add a user address entry to the address service:

    ```shell
    curl -i -X POST http://localhost:8081/address \
        -H 'Content-Type:application/json'       \
        -d '{"user_id":"e39eb5fe-bd8f-11ed-afa1-0242ac120002","address":"3-chōme-2-1 Nihonbashimuromachi, Chuo City, Tokyo 103-0022"}'
    ```

* Talk to the food service, which will talk to the address service via the sidecar:

  * Try ordering food via the food service:

    ```shell
    curl -X POST http://localhost:8082/feedme \
      -H 'Content-Type:application/json'      \
      -d '{"user_id":"e39eb5fe-bd8f-11ed-afa1-0242ac120002","food":"Sushi"}'
    ```

    You should see this request show up in the log output of the sidecar and of the consent service.

    The food service should print out an error like: `Food order failed: Could not get address. status: 404 Not Found` (since we haven't given consent yet).

  * Give consent to the food service

    ```shell
    curl -i -X POST "http://localhost:3000/v2alpha/consents" \
        -H "Content-Type: application/json" \
        -d '{ "user_id": "e39eb5fe-bd8f-11ed-afa1-0242ac120002", "service_name": "FooDelivery", "data_attributes": [ "CITY_ADDRESS_ID", "USER_ID" ] }'
    ```

  * Try ordering food again:

    ```shell
    curl -X POST http://localhost:8082/feedme \
      -H 'Content-Type:application/json'      \
      -d '{"user_id":"e39eb5fe-bd8f-11ed-afa1-0242ac120002","food":"Sushi"}'
    ```

    You should see this request show up in the logs again.

    The food service should now print out a success message like: `Food ordered! It will be delivered to "3-chōme-2-1 Nihonbashimuromachi, Chuo City, Tokyo 103-0022"`
