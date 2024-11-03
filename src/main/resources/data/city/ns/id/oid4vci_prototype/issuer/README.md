## Overview

- This prototype implements the issuer part for [OID4VCI(Draft11)](https://openid.net/specs/openid-4-verifiable-credential-issuance-1_0-11.html#name-token-request)
- It's responsible for OID4VCI Credential Endpoint and Metadata Endpoint.
- Keycloak is responsible for other endpoints(Authorization, Token)
- Use Sphereon SDK's OID4VCI Issuer REST API.
  - [GitHub](https://github.com/Sphereon-Opensource/SSI-SDK/tree/v0.18.1/packages/oid4vci-issuer-rest-api)
  - [npm](https://www.npmjs.com/package/@sphereon/ssi-sdk.oid4vci-issuer-rest-api)
- Retrieve following information in an access token that comes from keycloak via the wallet, and put them into Verifiable Credential as claims.(NOTE: currenty, dummy data is set because of the probelm in src/vc/index.ts)
  - sub
  - name
  - mail_address
- DIDs
  - did:web for signing to VCs.
    - JWA: ES256K(keytype: secp256k1)
  - did:key for resolve and verify Proof of Posession JWT.
    - ES256,ES256K,EdDSA
- Database
  - Currenty, it's using SQLite3.
  - When you run this, a database directory will be made on root path.

## Usage

### Nodejs on Local

Run following command after `yarn install` to deploy app in localhost.

```bash
yarn start-local
```

#### Endpoints

- Issuer Metadata Endpoint
  - http://localhost/issuer/.well-known/openid-credential-issuer
- Credential Endpoint
  - http://localhost/issuer/credential
- did:web:localhost:issuer
  - http://localhost/issuer/.well-known/did.json

### Docker on Local

```bash
cd docker
docker compose build
docker compose up -d
```

#### Endpoints

The same as above.

## Credential Request Example

- Change proof.jwt
- Access Token can be any value since Sphereon SDK doesn't validate it.(I dont't know why)

```bash
#!/bin/bash
HOST="localhost"
BASE_PATH="issuer"
CONTENT_TYPE="Content-Type: application/json"
AUTHORIZATION="Authorization: BEARER anyvalue"
DATA='{
   "format":"jwt_vc_json",
   "types":[
      "VerifiableCredential",
      "VerifiedEmployee"
   ],
   "proof":{
      "proof_type":"jwt",
      "jwt":"Your_PoP_JWT"
   }
}'

curl -X POST -H "$CONTENT_TYPE" -H "$AUTHORIZATION" -d "$DATA" "http://$HOST/$BASE_PATH/credential"
```

### proof.jwt

- ~~For `nonce`, always set it to `Bfe0fI0TBUX2fMQLrRz4vQ`. This issuer only accepts it for now.~~
- For `nonce`, you can get new nonce at `/prepare` endpoint. Get it before credential request.
- For `aud`, when you run this on local with default env setting, set it to `http://localhost/issue`

```
nonce=$(curl -H "$AUTHORIZATION" "http://$HOST/$BASE_PATH/prepare")

{
  "iat": 1706512949.937,
  "exp": 1806512949.937,
  "aud": "http://localhost/issuer",
  "iss": "did:jwk:eyJhbGciOiJFUzI1NiIsInVzZSI6InNpZyIsImt0eSI6IkVDIiwiY3J2IjoiUC0yNTYiLCJ4IjoiVEcySDJ4MmRXWE4zdUNxWnBxRjF5c0FQUVZESkVOX0gtQ010YmdqYi1OZyIsInkiOiI5TThOeGQwUE4yMk05bFBEeGRwRHBvVEx6MTV3ZnlaSnM2WmhLSVVKMzM4In0",
  "nonce": ${nonce}
}
```
