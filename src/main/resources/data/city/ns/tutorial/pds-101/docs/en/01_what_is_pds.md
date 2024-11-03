# What is PDS

The CityOS PDS - Personal Data Store - is a central data store for personal data records belonging to CityOS users that are likely to be accessed by more than one CityOS service. Data in PDS is stored as structured JSON documents validated against a pre-approved data schema. The data is Encrypted-at-Rest and unencrypted keys/ID values must not contain any PII for security purposes. This data is intended to be a central store that is available and reliable for calling services to access user data.

Relative to each service storing information about the user seperately this approach has a few benefits:

* Security - As data is centrally controlled, user information and especially PII is encrypted to protect their data
* Centralization - By keeping the data in a centralized source, there is some prevention of data fragmentation and the data that services access are more likely to have up to date data without conflicts.

Indirectly PDS also has finely grained permissioning provided through the DPS and the Privacy flow.

## Basic Flow

![Basic Flow](./assets/pdsflow.png)

The above assumes permissions have already been granted

1. Client (Service or App) send a request to a CityOS service
2. Ingress Gateway terminates TLS connection
3. Upon reaching the CityOS service the appropriate Auth token must be fetched from STS
4. The CityOS service makes a request to the PDS
5. The PDS does a lookup in its KVS/DB
6. The PDS sends a response to the CityOS Service
7. The CityOS service returns a response via the Ingress/API Gateway
8. The gateway returns the response to the client

Full information about this flow can be found at [TN-0107](https://docs.google.com/document/d/1aZf0K0WRTNep-xcD4UOWkgM5J3MCZZObZDn3aGlPFDU/edit#)

## What is DPS

The Data Privacy Service manages user consent and it is necessary that clients that wish to access user data stored in the PDS to first have that permission. The DPS is for the most part outside the scope of this tutorial. The Technical Note detailing its prototype can be found [here](https://docs.google.com/document/d/1sbZ8_b-WKYN3GWFayefUFcAA8Po0YmUFTJM9ZbfJZWY/edit#heading=h.ni1wjiph8qvy).
