### Howto: Authenticate a Service Account on Keycloak - script example

#### Summary
The following sample script shows how to obtain an access token for an specific Client ID (i.e. service account) 
#### Use case
On a service level, having a valid access token, it is possible to invoke keycloak actions through REST such as user creation, user role administration, etc. The access token will be inserted as an Authorization Bearer header. Example [here](howto_service_account_authentication.md)


```shell
### PRE-REQUISITES: 
# For roles assignment to an specific service account, please request it to the agora identity team at #wcm-org-agora-ama

# put your CLIENT_ID and CLIENT_SECRET here
CLIENT_ID=XXXX
CLIENT_SECRET=XXXX

# this example connect to DEV environment. Please replace this domain accordingly, if connecting to a different environment.
SERVER_DOMAIN="https://id.cityos-dev.woven-planet.tech"

# Get access token for the service account
ACCESS_TOKEN=$(
    curl -sS --request POST --url "$SERVER_DOMAIN/auth/realms/woven/protocol/openid-connect/token" \
      --header 'Accept: */*' \
      --header 'Content-Type: application/x-www-form-urlencoded' \
      --data client_id="$CLIENT_ID" \
      --data client_secret="$CLIENT_SECRET" \
      --data scope="openid" \
      --data grant_type=client_credentials \
 | jq ".access_token" -r
)

```

#### Further documentation
- [Access Tokens](https://oauth.net/2/access-tokens/)
