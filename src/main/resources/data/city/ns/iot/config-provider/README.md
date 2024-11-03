# IoT Config Provider
This is a server to provide a dynamic configuration for IoT based on tenant or group.

## Who should use it
This is for the service that want to have configuration in the tenant and/or group level. And it can be dynamically configurable e.g. via calling API or via UI. For example, OTA(SUS) want to have slack channel to notify new release and look up for auto-distribute flag.

## How does it works
IoT Config Provider stores configuration that is defined in [metadata](pkg/metadata/metadata.go). 

The data us stored in AWS parameter store, each configuration can be set per tenant level and group level this is achieved by parameter path 

For example xenia_slack_enable is stored in the following paths for tenant: test group: test-group
* tenant path
    format: /iota/tenant/`<tenant-name>`/<`config-id`>
    sample: /iota/tenant/test/xenia_slack_enable
* group path
    format: /iota/tenant/`<tenant-name>`/group/`<group-name>`/<`config-id`>
    sample: /iota/tenant/test/group/test-group/xenia_slack_enable

It is providing configuration with data overriding rule such that if the group level configuration is not set, it will looks for tenant level configuration. And if neither exists, it give the default value. 

## How do I add new configuration?
1. Add it at [metadata](pkg/metadata/metadata.go) 
1. Deploy the changes
1. [optional] contact [#wcm-org-agora-devrel](https://toyotaglobal.enterprise.slack.com/archives/C0415J5P1FD) to add it to AgoraUI e.g. https://agora-ui.agora-lab.woven-planet.tech/admin/xenia-settings

## How do I set and get the config value
### Call the endpoint via curl
First get the token
```sh
kubectx dev2-worker1-east
ID_USERNAME=$(kubectl -n testkube get secret bob-credentials -o jsonpath='{.data.username}' | base64 -d)
ID_PASSWORD=$(kubectl -n testkube get secret bob-credentials -o jsonpath='{.data.password}' | base64 -d)
TOTP_KEY=$(kubectl -n testkube get secret bob-credentials -o jsonpath='{.data.otp-key}' | base64 -d)
OTP_CODE=$(oathtool -b --totp $TOTP_KEY)
curl -s -X POST https://id.agora-dev.w3n.io/auth/realms/woven/protocol/openid-connect/token \
    --header 'Authorization: Basic' \
    --header 'Content-Type: application/x-www-form-urlencoded' \
    --data-urlencode 'grant_type=password' \
    --data-urlencode "username=$ID_USERNAME" \
    --data-urlencode "password=$ID_PASSWORD" \
    --data-urlencode 'client_id=iota-client' \
    --data-urlencode 'scope=openid' \
    --data-urlencode "otp=$OTP_CODE" | jq -r .access_token
```


```sh
curl --location 'https://iot.woven-city-api.toyota/config-provider/v1/tenants/test/configs' \
--header 'Authorization: Bearer <token>'
```

### Call the endpoint via service API call
The calling between service required the allowlist add it to MIDDLEWARE_ALLOWED_INTERNAL_SERVICES list, e.g. https://github.com/wp-wcm/city/blob/main/infrastructure/k8s/common/iot/kn-iota-config-provider/additional-configuration.yaml#L30-L31 after this, you can call it without using token.

For the detail of APIs usage, please see the [swagger](api/config-provider-v1.yaml) for more information.

### For user to set the config, 
xenia-config: https://agora-ui.agora-lab.woven-planet.tech/admin/xenia-settings
please contact [#wcm-org-agora-devrel](https://toyotaglobal.enterprise.slack.com/archives/C0415J5P1FD) if you want new config setting added.

## Development

### OpenAPI-based code
The base code in this project was generated by the [oapi-codegen](https://github.com/deepmap/oapi-codegen) utility.
This generates support code in the pkg/ota directory.

The generated code MUST not be edited manually, as it will be overwritten if and when the YAML specification for the service changes. See the file api/ota.yaml to see the specification from which this code is generated.

to re-generate the oapi code run
```sh
oapi-codegen -config generator-configs/lib.yaml api/config-provider.yaml # for based APIs
oapi-codegen -config generator-configs/libv1.yaml api/config-provider-v1.yaml # for V1 APIs
```

### Running locally with VSCode
* Get AWS access keys from go/aws in `services-default-dev-Z278lss (370564492268)` lab account
* Setup the launch.json in vscode like this to access lab's AWS resource
    ```json
            {
                "name": "Launch Config provider",
                "type": "go",
                "request": "launch",
                "mode": "auto",
                "program": "iot/config-provider/main.go",
                "args": [
                    "--tenant-key-ids",
                    "{ \"test\" : \"05a56300-74a9-4d9b-a853-6773544beba6\", \"test-mimir\" : \"032ce5ec-e875-45ef-858c-620ba421e225\"}",
                    "--client-id",
                    "iota",
                    "--client-secret",
                    "<kubectl get secret -n iot keycloak-client-secret-iota -o json | jq -r .data.CLIENT_SECRET | base64 -d>",
                    "--idp-url",
                    "https://id.agora-lab.woven-planet.tech/auth",
                    "--idp-allowed-clients",
                    "iota-client"
                ],
                "env": {
                    "AWS_ACCESS_KEY_ID":"",
                    "AWS_SECRET_ACCESS_KEY":"",
                    "AWS_SESSION_TOKEN": "",
                }
            },
    ```
* the key-ids can be retrieve from KMS customer keys with key alias in the format `iot-config-provider-<tenant>`
* Sample calls


    First get the token
    ```sh
    kubectx dev2-worker1-east
    ID_USERNAME=$(kubectl -n testkube get secret bob-credentials -o jsonpath='{.data.username}' | base64 -d)
    ID_PASSWORD=$(kubectl -n testkube get secret bob-credentials -o jsonpath='{.data.password}' | base64 -d)
    TOTP_KEY=$(kubectl -n testkube get secret bob-credentials -o jsonpath='{.data.otp-key}' | base64 -d)
    OTP_CODE=$(oathtool -b --totp $TOTP_KEY)
    curl -s -X POST https://id.agora-dev.w3n.io/auth/realms/woven/protocol/openid-connect/token \
        --header 'Authorization: Basic' \
        --header 'Content-Type: application/x-www-form-urlencoded' \
        --data-urlencode 'grant_type=password' \
        --data-urlencode "username=$ID_USERNAME" \
        --data-urlencode "password=$ID_PASSWORD" \
        --data-urlencode 'client_id=iota-client' \
        --data-urlencode 'scope=openid' \
        --data-urlencode "otp=$OTP_CODE" | jq -r .access_token
    ```

    ```sh
    curl --location 'http://localhost:8080/tenants/test/configs' \
        --header 'Authorization: Bearer <token>'
    ```

    ```sh
    curl --location 'http://localhost:8080/tenants/test/groups/nt-group/configs' \
        --header 'Authorization: Bearer <token>'
    ```