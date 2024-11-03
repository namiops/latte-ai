# Agora Curl executor

## How to use this executor

Configure a `Test` object as following:

```yaml
apiVersion: tests.testkube.io/v3
kind: Test
metadata:
  name: curl-executor-test
  namespace: testkube
spec:
  type: agora-curl-yq/test # confirm the executor with this type exists via the "Executors" page on Testkube UI.
  executionRequest:
    # command: ["NOT", "SUPPORTED"]
    args: # add curl options and url here
      - -f
      - "myservice.mynamespace.svc.cluster.local"
      - [...]
```
