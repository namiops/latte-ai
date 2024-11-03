# Authenticate your test with Keycloak

## Table of contents

- [Authenticate your test with keycloak](#Authenticate-your-test-with-Keycloak)
  - [Prerequisite](#Prerequisite)
  - [Authenticate your test against SUT](#Authenticate-your-test-against-SUT)
  
## Prerequisite
Please ensure you have compelted local testkube setup by this [Install TestKube Local Cluster](bootstrap_local_testkube.md) 

## Authenticate your test against SUT

When a local test communicates with the Service Under Test (SUT), it requires authentication before receiving a response. Therefore, integrating your test with Keycloak is an essential step.

### Test is local while SUT is in Agora cluster
In this scenario, the test resides locally on your development machine, and the SUT is running in the Agora cluster. As a result, the test behaves like an external consumer accessing the SUT in the Agora cluster. You should apply the authentication mechanism used in your system to your test.

For example, if your service employs `drako` as an external authorizer, with `authenticationMode` set to `OAuth2`, then the local test can use an access token for authentication. You can find an explanation of `authenticationMode` [here](https://developer.woven-city.toyota/docs/default/Component/drako-service/crd/DrakoPolicyBinding/#authenticationmode).

To obtain an access token, there are a few approaches. One utility script developed by the ID team is [oauth2-curl](../../../id/utils/oauth2-curl), which you can use as a reference. Please note that you should modify the script's parameters to fit your specific usage scenario, such as replacing `client_id` with your Keycloak client ID, and `device_code_endpoint` and `token_endpoint` with those of the Agora cluster you are testing with.

We are aware that nowadays, there are various authentication mechanisms in Agora clusters beyond `drako`, which means test authentication can also vary. Feel free to bring your questions or concerns to the #wcm-agora-testkube Slack channel for discussion.

### Test and SUT are both in Agora cluster
In this scenario, testkube and the SUT reside in the same cluster but different namespace. They can communicate with each other through secrets and tokens. Please refer to the steps below to configure your test for authentication against the SUT.

#### Obtain your keycloak client credentials
The test case needs to use your client credentials to authenticate against SUT. 

Assume SUT is `my-service`, then you maybe need to define `ExternalSecret` in testkube, example like below

```yaml
apiVersion: external-secrets.io/v1beta1
kind: ExternalSecret
metadata:
  name: my-service-secrets-testkube
  namespace: testkube
spec:
  refreshInterval: 1h
  # SecretStoreRef defines which SecretStore to fetch the ExternalSecret data.
  secretStoreRef:
    kind: SecretStore
    name: my-service-secrets
  # target defines the name of the Kubernetes Secret to be created. There can be only one target per ExternalSecret.
  target:
    name: keycloak-client-secret-my-service
  data:
    - secretKey: CLIENT_SECRET
      remoteRef:
        key: keycloak-client-secret-my-service # match secret name in "my-service" namespace
        property: CLIENT_SECRET
    - secretKey: CLIENT_ID
      remoteRef:
        key: keycloak-client-secret-my-service # match secret name in "my-service" namespace
        property: CLIENT_ID
```

In above yaml,  we defined `CLIENT_ID` and `CLIENT_SECRET` which are going to be used in test script in next section. 

 Also, it requires SecretStore `my-service-secrets` be predefined to tell where the secrets come from, like below
```yaml
apiVersion: external-secrets.io/v1beta1
kind: SecretStore
metadata:
  name: my-service-secrets
  namespace: testkube
spec:
  provider:
    kubernetes:
      # with RBAC permissions, the store is able to pull only from a specific namespace
      remoteNamespace: my-service
      server:
        caProvider:
          type: ConfigMap
          name: kube-root-ca.crt
          key: ca.crt
      auth:
        serviceAccount:
          name: "testkube-my-secret-store"
``` 
In above yaml, secretStore `my-service-secrets` is defined, supposed to retrieve secrets from ConfigMap in `my-service` namespace. Of course, to make the authentication pass,  the serviceAccount `testkube-my-secret-store` shall be precreated and granted with proper permission by `my-service`.  


#### Obtain token to run test

Firstly, we need to define Keycloak variables in `executionRequest` context of the test.

```yaml
executionRequest:
    variables:
      KEYCLOAK_URL:
        name: KEYCLOAK_URL
        value: https://id.${cluster_domain}
        type: basic
      KEYCLOAK_CLIENT_ID:
        name: KEYCLOAK_CLIENT_ID
        type: secret
        valueFrom:
          secretKeyRef:
            name: keycloak-client-secret-my-service # MUST create a KeycloakClient to obtain tokens
            key: CLIENT_ID
      KEYCLOAK_CLIENT_SECRET:
        name: KEYCLOAK_CLIENT_SECRET
        type: secret
        valueFrom:
          secretKeyRef:
            name: keycloak-client-secret-my-service
            key: CLIENT_SECRET
```

Above variables `KEYCLOAK_URL`, `KEYCLOAK_CLIENT_ID`, `KEYCLOAK_CLIENT_SECRET` will be used to fetch access token from Keycloak by `preRunScript` in testkube. 
```yaml
preRunScript: |
      # This script is used to get a keycloak token and store it in a Postman
      # collection environment variable file.
      #
      # TOKEN is a postman collection variable that should store the token.
      # We cannot directly use environment variables with newman CLI (e.g. --env-var TOKEN=$KEYCLOAK_TOKEN)
      # because of how Kubernetes Jobs work.
      #
      set -euo pipefail

      echo "PreRun Script started."

      echo "Login to Keycloak"
      TOKEN=$(wget -q \
      --post-data 'grant_type=client_credentials&client_id='"$${KEYCLOAK_CLIENT_ID}"'&client_secret='"$${KEYCLOAK_CLIENT_SECRET}"'' \
      --header 'Authorization: Basic' \
      --header 'Content-Type: application/x-www-form-urlencoded' \
      --output-document - "$${KEYCLOAK_URL}/auth/realms/woven/protocol/openid-connect/token" \
      | awk -F'"' '/access_token/ {print $4}')

      if [ -n "$TOKEN" ]; then
        echo "Keycloak token obtained successfully."
        echo '{
          "name": "KEYCLOAK",
          "values": [
            {
              "key": "TOKEN",
              "value": "'"$${TOKEN}"'",
              "type": "secret",
              "enabled": true
            }
          ]
        }' > keycloak-token.json
        echo "Token is written to: keycloak-token.json"
      else
        echo "Failed to obtain Keycloak token."
        exit 1
      fi

      echo "PreRun Script finished."
      echo "----------------------------------------"
```

Above `preRunScript` will be executed prior to the test run, aiming to fetch an access token from Keycloak and then save the token to `keycloak-token.json`. This JSON will be parsed as an argument to the command that carries out the real test execution.

Arriving here, you can now run the test in testkube. It will authenticate the test against the System Under Test (SUT) and get a response smoothly!

These changes are key pieces of the whole test manifest. For the full example, please refer to the [fabrication service test manifest](../../../../infrastructure/k8s/environments/dev2/clusters/worker1-east/testkube/tenants/fabrication-service-dev)
