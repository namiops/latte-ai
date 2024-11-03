# Agoractl SSO

Single sign-on (SSO) is an authentication process that allows a user to access multiple applications or websites using a single set of login credentials. This means that after the user logs in to Agoractl, they can access all Agoractl plugins without needing to sign in again.

## Introduction

To log in, use following command:

```
bazel run //ns/agoractl -- sso login
```

Then, use a jq command to extract the access token stored in `~/.cache/agoractl/sso/token.json`.

```
jq '.access_token' ~/.cache/agoractl/sso/token.json
```

##### Accessing Keycloak admin API

The `--print-token` option prints the access token itself and its type to `stdout`. You can use it to insert the `Authorization` header in HTTP requests.

```sh
curl https://id.cityos-dev.woven-planet.tech/auth/admin/realms/woven/users/count -X GET -H "Content-Type: application/json" -H "Authorization: $(bazel run //ns/agoractl -- sso login --print-token)"
```
!!! Note
    The above example requires you to have `view-users` permissions.
