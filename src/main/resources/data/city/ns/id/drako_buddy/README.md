# drako_buddy

Drako Buddy is the name of a kubernetes operator that enforces Drako for all
HTTP services in a kubernetes + Istio cluster setup. It was specifically created
for working around permission limitations of Istio mesh configuration on
Speedway: http://go/tn-0467

There is also a security requirement for Agora to use Drako as the single Policy
Decision Point (PDP) in the cluster.

## How it works

Put simply, Drako Buddy watches a kubernetes cluster for HTTP services and
installs Istio EnvoyFilters that point to Drako. This way, Drako will become a
PDP for all HTTP services in the cluster.

> [!NOTE]
> Drako Buddy checks whether a service is HTTP using [explicit protocol
> selection](https://istio.io/latest/docs/ops/configuration/traffic-management/protocol-selection/#explicit-protocol-selection)
> for `http`, `http2`, `grpc`, and `grpc-web` protocols only.
> Currently, no automatic protocol selection or HTTP gateway protocol selection
> is supported.
>
> This means, the service needs to have at least 1 port where the `name` is
> `<protocol>[-<suffix>]` or the `appProtocol` is `<protocol>` (where protocol is
> either `http`, `http2`, `grpc`, or `grpc-web`). You only need `name` or `appProtocol`
> but you can also have both defined. The following are 2 possible examples of how to
> achieve this:
>
> ```yaml
> kind: Service
> metadata:
>   name: my-service
> spec:
>   ports:
>   - name: http-web
>     port: 8080
> ```
> 
> ```yaml
> kind: Service
> metadata:
>   name: my-service
> spec:
>   ports:
>   - name: web # http-web is also OK
>     port: 8080
>     appProtocol: http
> ```

Drako Buddy also detects changes to EnvoyFilters: if a managed EnvoyFilter is
modified/deleted by someone else, Drako Buddy will update/create it again to
enforce Drako.

If you want to find all objects managed by Drako Buddy in a given namespace,
you can do the following:

```console
kubectl get envoyfilters -n NAMESPACE -l drako-buddy.woven-city.global/service
```

## Exceptions

> [!NOTE]
> One of Drako Buddy's goals is to create EnvoyFilters for every single
> service. The only exceptions to this are services that have been allowed by
> Security to not use Drako. However, setting this behavior as default for such a
> new operator has the potential to cause a major outage or unpredictable
> behavior on Speedway. Because of that, we are temporarily setting the default
> to ignore all services unless explicitly stated otherwise. Our plan is to roll
> out the changes gradually, service by service, with the cooperation of its
> developers to ensure a safe transition.

In order to explicitly make Drako Buddy watch a service, you need to add the
following label to the service:

```yaml
drako-buddy.woven-city.global/ignore: no # or "false"
```

In order to explicitly make Drako Buddy ignore a service, you need to add the
following label to the service:

```yaml
drako-buddy.woven-city.global/ignore: yes # or "true"
```

If an EnvoyFilter managed by Drako Buddy already exists for a given service,
adding this label will cause the EnvoyFilter to be deleted.

## How to use

### CLI usage

```
Usage: drako_buddy [OPTIONS] --ext-authz-hostname <EXT_AUTHZ_HOSTNAME> --ext-authz-port <EXT_AUTHZ_PORT>

Options:
      --observability-service-name <OBSERVABILITY_SERVICE_NAME>
          The service name to report for observability purposes [env: OBSERVABILITY_SERVICE_NAME=] [default: drako-buddy.id]
      --ext-authz-hostname <EXT_AUTHZ_HOSTNAME>
          The external authorizer hostname (e.g. "drako-v1.id.svc.cluster.local") [env: EXT_AUTHZ_HOSTNAME=]
      --ext-authz-port <EXT_AUTHZ_PORT>
          The external authorizer port (e.g. "9001") [env: EXT_AUTHZ_PORT=]
  -h, --help
          Print help
  -V, --version
          Print version
```

### Running on speedway/local

Ensure you are in the correct kube context:

```console
kubectl config use-context CONTEXT_NAME
```

Then, you can run drako-buddy directly from the repository:

```console
bazel run //ns/id/drako_buddy:drako_buddy -- \
    --ext-authz-hostname drako-v1.id.svc.cluster.local \
    --ext-authz-port 9001
```

> [!NOTE]
> Make sure you run a single instance of drako-buddy, otherwise they may
> attempt to execute conflicting modifications to the cluster. Turn off
> drako-buddy running on speedway/local if necessary.
