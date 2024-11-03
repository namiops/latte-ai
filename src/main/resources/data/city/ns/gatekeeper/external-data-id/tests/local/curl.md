Examples:
```
key=$(echo '{ "id": "sample-n5", "exportTo": [{"namespace": "n5-1"}]}' | base64 -w0)
body=$(printf '{"request":{"keys":["%s"]}}' $key)
curl -k -X POST https://external-data-id-v1.gatekeeper-system/gkedp/v1/count-ids -d "$body" | jq
```
[https://open-policy-agent.github.io/gatekeeper/website/docs/externaldata/#how-to-generate-a-self-signed-ca-and-a-keypair-for-the-external-data-provider]

```json
{
  "providerKind": "ProviderResponse",
  "response": {
    "items": [
      {
        "key": "eyAiaWQiOiAic2FtcGxlLW41IiwgImV4cG9ydFRvIjogW3sibmFtZXNwYWNlIjogIm41LTEifV19Cg==",
        "value": "eyJ0b3RhbCI6MiwiZXhwb3J0ZWQiOjF9",
        "error": ""
      }
    ]
  }
}
```
Or decode on the fly
```sh
curl -k -X POST https://external-data-id-v1.gatekeeper-system/gkedp/v1/count-ids -d "$body" | jq -r '.response.items[] | .value | @base64d'
```
```json
{"total":2,"exported":1}
```