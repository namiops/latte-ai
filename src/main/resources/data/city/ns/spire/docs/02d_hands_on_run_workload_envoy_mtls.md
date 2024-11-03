# Hands-on: Running External Workload Part 2: mTLS in Envoy

In the previous page, we wrote the explicit logic in the application code to handle fetching SVID.

In this page, we'll a way to delegate the SVID retrival to an Envoy proxy, so that the application code will care less about mTLS.

## Procedure

1. In the city repo, go to [ns/spire/docs-k8s/workload-mtls-envoy directory](https://github.com/wp-wcm/city/tree/main/ns/spire/docs-k8s/workload-mtls-envoy), deploy it, and get into the container.
    ```sh
    cd <city-repo-dir>/ns/spire/docs-k8s/workload-mtls-envoy
    kubectl apply -k .

    kubectl -n spire-tutorial exec -it $(kubectl -n spire-tutorial get po -l app=workload-mtls-sample -ojsonpath='{.items[0].metadata.name}') -- sh
    ```
2. Within the container, build & run the program to get the X.509 SVID and send HTTP GET request to the Envoy proxy.
    ```sh
    cd /go/src ; go build .
    ./main --envoy-proxy-url http://127.0.0.1:3001
    ```

The expected output is the Nginx Welcome html.

## Clean up

1. Delete the deployed resources in minikube.
    ```sh
    kubectx minikube

    cd <city-repo-dir>/ns/spire/docs-k8s/workload-mtls-envoy
    kubectl delete -k .
    ```
2. Delete the registration entry. This typically includes delete `spiffe-id.yaml` under your Agora k8s manifest folder and remove the entry under `kustomization.yaml`, raise PR for the change and get it merged.
    1. Get the amendment merged.
        ```sh
        rm <city-repo-dir>/<your-k8s-manifest-folder>/spiffe-id.yaml

        # Remove `- spiffe-id.yaml` under `resources:`
        vim <city-repo-dir>/<your-k8s-manifest-folder>/kustomization.yaml

        # Commit, raise PR, merge.
        ```
    2. Confirm `SpiffeID` is deleted properly by Flux.
        ```sh
        kubectx dev

        kubectl -n <YOUR_AGORA_NAMESPACE> get spiffeid
        ```

        Expected Output.
        ```
        No resources found in <YOUR_AGORA_NAMESPACE> namespace.
        ```

## Deep Dive

### [main.go](https://github.com/wp-wcm/city/blob/main/ns/spire/docs-k8s/workload-mtls-envoy/go-client/main.go)

You'll find there's no much interesting going on in the program (which is a good thing!); it just makes a GET request. This is because all the interesting things we had to do in the previous page is handled by the Envoy proxy we'll go over in a moment.

Just one thing to note is, as you may have noticed in the above command, you specify the URL for the local Envoy proxy, not the URL for _Internal Workload_. The Envoy proxy that gets the request will in turn make the request to _Internal Workload_ (in this example, _Internal Workload_ is hard-coded in the Envoy config).

### [client.yaml](https://github.com/wp-wcm/city/blob/main/ns/spire/docs-k8s/workload-mtls-envoy/client.yaml)

Now the client deployment has two containers, the app container (`workload-mtls-envoy`) and the Envoy-proxy (`envoy`).

```yaml
spec:
    # ...omitted...
    containers:
    - name: workload-mtls-envoy
    # ...omitted...
    - name: envoy
        image: envoyproxy/envoy:v1.26-latest
        imagePullPolicy: Always
        args:
        - -l
        - debug
        - --local-address-ip-version
        - v4
        - -c
        - /run/envoy/envoy-conf.yaml
        - --base-id
        - "1"
        ports:
        - name: http
            containerPort: 3001
            protocol: TCP
        volumeMounts:
        - name: envoy-conf
            mountPath: "/run/envoy"
            readOnly: true
        - name: spire-agent-socket
            mountPath: /run/spire/sockets
            readOnly: true
    # ...omitted...
    volumes:
    - name: spire-agent-socket
        hostPath:
        path: /run/spire/client-sockets
        type: Directory
    - name: envoy-conf
        configMap:
        name: envoy-conf
```

Compared to the previous example where mTLS is handled by the app, notice that `spire-agent-socket` mount is moved from the app container to the Envoy container.

In `args`, `-c` `/run/envoy/envoy-conf.yaml` is where it passes the Envoy configuration file that we’ll be looking at in a moment (see it's mounted through a ConfigMap).

### [envoy-conf.yaml](https://github.com/wp-wcm/city/blob/main/ns/spire/docs-k8s/workload-mtls-envoy/envoy-conf.yaml)

This is the config file for Envoy. This may be unfamiliar to many of us, so let's take a moment to look into it.

The basic structure is the following.

```yaml
node:
  #...omitted
static_resources:
  listeners:
    #...omitted
  clusters:
    #...omitted
```

At the top-level, we configure `node` and `static_resources`. Don't worry about [node](https://www.envoyproxy.io/docs/envoy/latest/api-v3/config/core/v3/base.proto#config-core-v3-node) too much, it's just an identifier for the Envoy being configured.

`static_resources` is the main configuration in this file.

- `listeners` includes settings about how this proxy listens to incoming traffic, such as the port to listen on, the protocol to use (TCP or HTTP), and the SSL certificate to use.
- `clusters` includes settings about where this proxy forwards to traffic to, such as the load balancing algorithm to use, the list of endpoints to use, and the tls settings.

#### Listeners

Let's take a closer look at the listener setting.

```yaml
address:
  socket_address:
    address: 0.0.0.0
    port_value: 3001
```

The above part declares it listens to port 3001.

```yaml
route_config:
  name: service_route
  virtual_hosts:
  - name: outbound_proxy
    domains: ["*"]
    routes:
    - match:
        prefix: "/"
      route:
        cluster: backend
```

The above part declares the proxy route basically any http traffic to the cluster named `backend`. We'll visit `backend` soon.

#### Clusters

Moving on to the cluster part.

```yaml
  - name: spire_agent
    connect_timeout: 0.25s
    http2_protocol_options: {}
    load_assignment:
      cluster_name: spire_agent
      endpoints:
      - lb_endpoints:
        - endpoint:
            address:
              pipe:
                path: /run/spire/sockets/agent.sock
```

The above part configures spire agent endpoint, so that it can be used in the TLS setting later. Notice the socket path is in line with the mount path of client.yaml that we looked at earlier.

```yaml
- name: backend
  load_assignment:
    # ...omitted...
  transport_socket:
    # ...omitted...
```

Now `backend`; remember the earlier listener refered to this cluster as its route destination.

```yaml
load_assignment:
  cluster_name: mtls-envoy
  endpoints:
    - lb_endpoints:
      - endpoint:
          address:
            socket_address:
              address: nginx.spire-tutorial.cityos-dev.woven-planet.tech
              port_value: 7029
```

As you can see above, this cluster points to `nginx.spire-tutorial.cityos-dev.woven-planet.tech:7029` as its eventual destination.

```yaml
transport_socket:
  # ...omitted...
  typed_config:
    # ...omitted...
    common_tls_context:
      tls_certificate_sds_secret_configs:
        - # ...omitted...
          sds_config:
            api_config_source:
              api_type: GRPC
              grpc_services:
                - envoy_grpc:
                    cluster_name: spire_agent
      # ...omitted...
```

`transport_socket` is where TLS setting is declared. Importantly, it declares it takes the secret configs from the cluster `spire_agent` through its GRPC API (=Spire Agent's workload API).

!!! success

    With this config, Envoy also takes care of the key rotation and makes the application development easier.
    Whenever possible, consider this Envoy approach over the app mTLS approach.

## Wrap up

Conguratulations! You learned how to establish mTLS sessions between workloads outside and inside of Agora in different ways.
