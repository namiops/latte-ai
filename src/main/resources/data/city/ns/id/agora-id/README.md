# Agora Identity control interface

## Brief

### Running command
Please use folllowing command to run `agora-id` application from bazel
```sh
bazel run //ns/id/agora-id -- <ARGUMENTS>
```
To make this more convenient, you can crate an alias
```sh
alias agora-id="bazel run //ns/id/agora-id --"
```

You can also download and use binary from Artifactory
*TBD*

Or use binary that you build with bazel
```sh
bazel-bin/ns/id/agora-id/agora-id
```

### Bash completion
Please use follwing command to add bash completions
```sh
source <(agora-id generate-bash-completions)
```

## SSO Login
Please use folllowing command to login to pre-prod cluster also known as `dev2`
```sh
agora-id sso login
```

If you need to login to another cluster, you can use option `--profile`, supported values `lab`, `dev`, `lab2`, `dev2`.

Access token stored in `~/.cache/agora/id/sso/token.json` file, you can use jq command to extract access_token from it.
```sh
jq '.access_token' ~/.cache/agora/id/sso/token.json
```

Option `--print-token` prints access token type and access token itself to the stdout. You can use this to insert `Authorization` header to the HTTP requests.
```sh
curl https://id.cityos-dev.woven-planet.tech/auth/admin/realms/woven/users/count -X GET -H "Content-Type: application/json" -H "Authorization: $(agora-id -- sso login --print-token)"
```
!!! Note
    Above example requires `view-users` user role.

## Keycloak admin API calls
You can find list of available API call with the `--help option`

```sh
agora-id keycloak admin --help
```

### Examples
Search group by name
```sh
agora-id keycloak admin get-groups --search woven
```

Search user by email
```sh
# search for specific user
agora-id keycloak admin get-users --email test@example.com

# get list of all users in lab cluster
agora-id keycloak admin get-users --profile lab
```

Add user to the group
```sh
agora-id keycloak admin put-user-group --email test@example.com --group-path /myproject/users
```
