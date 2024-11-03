# Keycloak Extension for External Services Integration

## Introduction

This Proof of Concept (PoC) showcases the integration of Keycloak with external services like Consent and BURR. It augments the OpenID Connect Authorization Code flow with explicit user consent management during authentication. This enhancement prioritizes user privacy and security while ensuring a fluid user experience. Upon user consent, client-specified information is retrieved from services analogous to BURR.

## Components

- **Service Now**: Initiates the authentication code flow.
- **Consent Service**: Provides a mock UI to capture user consent as part of the Keycloak consent request workflow.
- **BURR Service**: Emulates BURR by supplying customized user information.
- **Keycloak Service**: Integrates consent handling and interactions with BURR to include claims in the token during authentication.

## Flow

For an in-depth understanding of the interaction between these components, refer to the sequence diagram and flow description available [here](https://docs.google.com/document/d/1vt9iPkuuZMb0nh5ujXlaJkfFbIBtk59t1J6gRGmAb-4).

## Usage

To interact with the Proof of Concept and understand the extended Keycloak functionality:

1. Start all components with Docker Compose by executing the following command:

    ```bash
    docker-compose up --build
    ```

2. With the services running, initiate the user login flow by navigating to the Service Now mock login page:

    ```
    http://localhost:6044
    ```

3. Use the following test credentials to log in and evaluate the consent flow:

    - **Username**: elon
    - **Password**: elon

4. You'll be redirected to consent UI, where click on "yes" to give consent.(this uses the custom consent SPI implementation )

5. You'll be redirected back to keycloak and from there back to service now.
    In background the consent decision is checked by custom consent SPI and custom protocol mapper SPI gets the claims for this user from BURR and embed it in the token.

Also note that the client used by service_now here is configured to use a custom login flow that involves using the custom consent SPI and custom protocol mapper SPI in keycloak already.

### Detailed Flow

Once you start the login process, here is what to expect step-by-step:

1. **User Login**:
    - The user logs in to Service Now, which redirects to Keycloak for authentication.

2. **Authentication and Consent Verification**:
    - Keycloak authenticates the user credentials.
    - Keycloak then checks if the user has previously given consent via the Consent API.
    - If consent is not recorded, Keycloak redirects to the Consent UI.

3. **User Consent**:
    - The user provides consent through the Consent UI.
    - Consent UI communicates the user's decision back to Keycloak.

4. **Data Retrieval and Tokenization**:
    - Keycloak requests user information from the BURR service, based on the given consent.
    - The BURR service returns the necessary information to Keycloak.
    - Keycloak includes this information in the ID token.

5. **Data Storage in Service Now**:
    - Keycloak sends the token to Service Now, where the user information is stored.
