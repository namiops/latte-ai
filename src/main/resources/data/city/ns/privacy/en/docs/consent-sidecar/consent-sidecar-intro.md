# Consent Sidecar

## What is Consent sidecar?

Consent sidecar is a component implemented by the Agora Personal Data team. It aims to provide user consent checking and data filtering functionalities for service APIs.

Every time a service responds to an HTTP request returning personal data, it has to confirm that the owner of the data agrees to it. This ensures that no personal data is shared without the user's knowledge. Services should confirm user consent before sharing personal data by making the appropriate requests to the [Consent service](https://developer.woven-city.toyota/catalog/default/api/consent-api-v3alpha/definition).

Since we expect a number of services to require these functionalities, Consent sidecar minimizes redundant implementations by handling the consent-related communication. It serves privacy requirements without the need to update the service code. As a trade-off, service developers need to prepare the appropriate configurations and deploy the sidecar alongside their own service in the Agora cluster.

For a more detailed description of the relevant consent concepts, see [Consent management in Agora](../consent/README.md).

If you're only interested in the consent checking functionality - [Drako Consent Policies](../drako-consent-policy/drako-consent-policy.md) might be also interesting for you.

### Terminology

Before we move on, please note the following terms and definitions as used in this document:

* Resource: Data including personal information (PII)
* Resource server: Service sharing the resource
* Consuming service: Service making the request to the resource server to share a resource

### How it works

![sidecar diagram](./consent-sidecar-simplified.png)

On a high-level, Consent sidecar acts as an HTTP reverse proxy for the resource server.  

Using the provided configuration, it matches the requests of the consuming service based on the request path and method, then acts on them accordingly, performing consent checks and filtering if applicable. If a given path or method is not specified in the sidecar configuration, the sidecar fails to match it and just forwards the communication without performing any action. 

Depending on the configuration, Consent sidecar reads the necessary data from the HTTP request or response. It initially forwards the request and performs the appropraite actions once the resource server provides a response.

#### Consent check

Based on the user's consent decision, one of the following occurs:

1. The requested data (resource) is forwarded to the consuming service, or
2. An appropriate error response is returned.

For a more detailed description of the consent check logic, see [Consent management in Agora: Consent check logic](../consent/README.md#consent-check-logic-consent-check-logic).

#### Response filtering (coming soon)

_**NOTE:** This functionality has not been implemented yet. If you are interested, [contact us](https://toyotaglobal.enterprise.slack.com/archives/C06230AUVSS) to learn more._

In some cases where consent has not been granted, instead of returning an error response, we might want to filter out the unconsented data and return a successful response to the consuming service anyway. Examples of such scenarios include:

* Resource server returning a collection of personal data belonging to multiple users.
* Resource server returning different types of personal data, but the consuming service only has consent to access some of them.

After checking for consent, the sidecar could filter out the unconsented data and return a redacted response without unnecessarily disrupting communication with the consuming service.

## What's next?

For more information on how to start using the Consent sidecar, see:

- [Consent sidecar configuration](./how-to-write-sidecar-configuration.md)
- [Consent sidecar deployment](./how-to-deploy-sidecar.md)
