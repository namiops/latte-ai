# ADR-BE-0001 Auth Token Handling in the Middleware Layer

| Status | Last Updated |
|---|---|
|Drafted| 2023-11-28 |

## Context and Problem Statement

Each of our backend applications utilizes [Drako](https://developer.woven-city.toyota/docs/default/Component/drako-service), an Agora's Policy Decision Point system.
Because Drako always verifies users' authentication tokens (signed JSON Web Token) before routing to each application, the backend application can always obtain verified user tokens with Drako.
However, tokens don't have any information on the users except their woven IDs.
If a backend application wants to refer to such data, the application should send a request to Keycloak.

Assuming these points, this document determines how to handle users' auth tokens in the middleware layer.

---

## Considered Options

There are two discussion points.

- Whether the middleware verifies tokens.

While verifying them is more secure in case Drako causes a problem, the middleware has to implement certification retrieval and verification.

- Whether the middleware sends requests to Keycloak to get users' additional information.

As the backend basically refers to user information stored in BURR, most APIs will not require one stored in Keycloak.

---

## Decision Outcome

To make the implementation simple, we chose the following options.

- We completely rely on the verification on Drako and skip verification on the backend.
- Skip information retrieval from Keycloak and set woven ID in a token to the request context.

---

## Note

- 2023-11-28 : Drafted, Originator: Hajime Miyazawa
