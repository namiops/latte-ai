# Identity service's external data provider for Gatekeeper.

External provider serves to following endpoint `/gkedp/v1/:function` where function is
* `uuid`
* `count-ids`

## Functions

### `uuid`
Calculate uuid-v5 with the namespace URL.

Example
```
curl -X POST localhost:30000/gkedp/v1/uuid -d '{"request":{"keys":["123", "abc"]}}' | jq
```
```json
{
  "providerKind": "ProviderResponse",
  "response": {
    "items": [
      {
        "key": "123",
        "value": "b9b6607d-6974-594f-8e99-ac3de71c4d89",
        "error": ""
      },
      {
        "key": "abc",
        "value": "68661508-f3c4-55b4-945d-ae2b4dfe5db4",
        "error": ""
      }
    ]
  }
}
```

### `count-ids`
Count number of reserved identifier with specified id. It expected that id's are unique and there no more than one reserved identifier with the specified id. In the case of multiple instances with the same id, gatekeeper can throw a policy violation. Both key and value must be encoded to base64.
Data provider itself does not counts multiple instances as an error.


Example
```sh
key=$(echo '{ "id": "sample-n5", "exportTo": [{"namespace": "n5-1"}]}' | base64 -w0)
body=$(printf '{"request":{"keys":["%s"]}}' $key)
curl -k -X POST https://external-data-id-v1.gatekeeper-system/gkedp/v1/count-ids -d "$body" | jq
```
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

Or decode base64 on the fly
```sh
curl -k -X POST https://external-data-id-v1.gatekeeper-system/gkedp/v1/count-ids -d "$body" | jq -r '.response.items[] | .value | @base64d'
```
```json
{"total":2,"exported":1}
```

In this example, provider found two reserved identifier with the same key: `sample-n5`, this is violation of the uniqueness policy.
