# id-test-drako-polis-v1

This is a playground for testing drako-polis-v1 service.

## Overview

- `sleep`: This is a pod that does nothing except wait indefinitely. This pod is
  useful for testing `curl` against the running drako-polis-v1 service.
- `sleep-no-ext-authz`: Similar to `sleep`, except that this service is not
  authorized to communicate with drako-polis-v1, so requests from this service
  should be denied by Drako.

## Requirements

1. On your `infrastructure/k8s/local/id/kustomization.yaml` add the following to
   the list of resources:
   ```yaml
     - ../../common/id-test-drako-polis-v1
   ```
2. Push these changes to your local branch and reconcile with FluxCD.
3. Confirm that you have a `id-test-drako-polis-v1` namespace:
   ```console
   kubectl --context minikube get all -n id-test-drako-polis-v1
   ```

> [!NOTE]
> Please note that you can also use a different environment and avoid FluxCD
> by manually using `kubectl`.

## Testing

In the following example we use `curl` (inside a testing-specific `sleep`
container) to call drako-polis-v1 service:

```console
$ kubectl --context minikube exec -n id-test-drako-polis-v1 deployment/sleep \
    -c sleep -- curl -s -D - http://drako-polis-v1.id:8080/v1/namespaces/id-test-drako-polis-v1/groups/test

HTTP/1.1 200 OK
content-type: application/json
content-length: 83
date: Fri, 22 Dec 2023 06:51:34 GMT
x-envoy-upstream-service-time: 6
server: envoy

{"group":{"namespace":"id-test-drako-polis-v1","name":"test","description":"test"}}
```

You can also test whether authorization is working properly as follows:
```console
$ kubectl --context minikube exec -n id-test-drako-polis-v1 deployment/sleep-no-ext-authz \
    -c sleep -- curl -s -D - http://drako-polis-v1.id:8080/v1/namespaces/id-test-drako-polis-v1/groups/test

HTTP/1.1 403 Forbidden
date: Mon, 25 Dec 2023 13:51:28 GMT
server: envoy
content-length: 0
x-envoy-upstream-service-time: 8
```
