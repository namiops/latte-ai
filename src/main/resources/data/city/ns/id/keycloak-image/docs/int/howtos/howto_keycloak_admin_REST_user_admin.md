
### Howto: Create/Get User through Keycloak admin REST API  - script example

Prerequisite: 
1. Obtain an access token (`$ACCESS_TOKEN`) by authenticating your Service Account (i.e. ClientID). How? [Here](howto_service_account_authentication.md)'s an example.
2. Service Account must be granted the following roles:
 - For creating users: `user-admin` role (beware: this request might be granted under specific circumstances)
 - For getting users: `view-users` and `query-users` roles.

#### Create new user

```shell
# To learn more about USER_OBJECT, check the following page.
# https://www.keycloak.org/docs-api/22.0.1/rest-api/index.html#UserRepresentation
USER_NAME="hoge-123"
USER_OBJECT=$(cat << EOS
{
  "username": "$USER_NAME",
  "firstName": "hoge",
  "lastName": "fuga"
}
EOS
)

# Remove all CR & CF charaters from the json string
USER_STRING=$(echo "$USER_OBJECT" | sed -e 's/\r\n//g')

# Post new user
RESULT=$(curl -sS --request POST --url "$SERVER_DOMAIN/auth/admin/realms/woven/users" \
  --header 'Content-Type: application/json' \
  --header 'Authorization: Bearer '$ACCESS_TOKEN \
  -d "$USER_STRING"
)

# Show the result
# When the request succeeded, it do not return any body.(Just code 201 is returned)
# But the request failed, it returns error reason in json format.
echo $RESULT
echo $RESULT | jq "."

```

#### Get the created user

```shell
# This search API returns ARRAY of users.
QUERY_PARAMS="?username=${USER_NAME}&exact=true"
RESULT=$(curl -sS --request GET --url "$SERVER_DOMAIN/auth/admin/realms/woven/users$QUERY_PARAMS" \
  --header 'Accept: */*' \
  --header 'Content-Type: application/json' \
  --header 'Authorization: Bearer '$ACCESS_TOKEN
)

# Show the result
echo $RESULT | jq "."

# Extract userID
USER_ID=$(echo $RESULT | jq -r ".[0].id")


### Get user representaiton specifying the USER ID
# This get API returns ONE user representation.
RESULT=$(curl -sS --request GET --url "$SERVER_DOMAIN/auth/admin/realms/woven/users/$USER_ID" \
  --header 'Accept: */*' \
  --header 'Content-Type: application/json' \
  --header 'Authorization: Bearer '$ACCESS_TOKEN
)

# Show the result
echo $RESULT | jq "."

echo "Script finished"
```

#### Further documentation
- [User Representation](https://www.keycloak.org/docs-api/22.0.1/rest-api/index.html#UserRepresentation) (i.e. User schema) 
- [Keycloak Admin REST API](https://www.keycloak.org/docs-api/22.0.1/rest-api/index.html)