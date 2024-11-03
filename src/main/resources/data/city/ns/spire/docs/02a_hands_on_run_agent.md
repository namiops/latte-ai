# Hands-on: Running Spire Agent

## Procedure

### Node attestation

As mentioned, Spire involves 2-step-attestations; node attestation and workload attestation.

In the node attestation step, spire-agent needs to provide spire-server with some information about the node so that the spire-server can run some verification process and determine “OK, this node is trustworthy.”

There are several methods (in the form of plugins, take a look at `plugin_server_nodeattestor*` files under [this list](https://github.com/spiffe/spire/tree/main/doc)) to do this, but in this tutorial we’ll be using [join_token](https://github.com/spiffe/spire/blob/main/doc/plugin_server_nodeattestor_jointoken.md) method, where spire-server issues a short-TTL token that spire-agent can use to get itself attested.

!!! danger

    This way of node attestation is easy, but is unreliable, insecure and *ONLY FOR THIS TUTORIALS*.
    In the real life, we'll be using properer methods such as [x509pop](https://github.com/spiffe/spire/blob/main/doc/plugin_agent_nodeattestor_x509pop.md) or [k8s_psat](https://github.com/spiffe/spire/blob/main/doc/plugin_server_nodeattestor_k8s_psat.md).

1. Access https://spire-tutorial.cityos-dev.woven-planet.tech/issue-token and take the memo of the token value.
    - The token's TTL is 10mins and you have to complete `Confirm the agent is running` section within 10 mins. If things don't work well, re-issue the token by accessing the URL and try again.


### Run Spire Agent

1. In the city repo, go to [ns/spire/docs-k8s/spire-agent directory](https://github.com/wp-wcm/city/tree/main/ns/spire/docs-k8s/spire-agent) and make an amendment to agent.conf
    ```sh
    cd <city-repo-dir>/ns/spire/docs-k8s/spire-agent

    # Replace `agent.conf` -> `<YOUR_JOIN_TOKEN_HERE>` to the token you obtained in the previous step.
    vim agent.conf
    ```
2. Apply the manifests.
    ```sh
    kubectx minikube

    kubectl apply -k .
    ```


### Confirm the agent is running

1. By Pod's status
    ```sh
    kubectl -n spire-agent-ns get pod
    ```

    Expected Output. May take a few mins to before it turns to Running status.
    ```
    NAME                                      READY   STATUS    RESTARTS   AGE
    spire-agent-z8stk                         1/1     Running   0          3m
    ```
2. By the log
    ```sh
    kubectl -n spire-agent-ns logs -f $(kubectl -n spire-agent-ns get po -l app=spire-agent -ojsonpath='{.items[0].metadata.name}')
    ```

    Expected log.
    ```
    time="2023-02-15T04:38:20Z" level=info msg="Node attestation was successful" rettestable=true spiffe_id="spiffe://spire-tutorial.cityos-dev.woven-planet.tech/spire/agent/join_token/<JOIN_TOKEN>" subsystem_name=attestor trust_domain_id="spiffe://spire-tutorial.cityos-dev.woven-planet.tech"
    ```

If you are only interested in how to set things up practically, you can skip to the next page.

If you are interested in the internals please read through.

## Deep Dive

Let's look into each of the manifests and the conf file bit by bit.

### [agent.yaml](https://github.com/wp-wcm/city/blob/main/ns/spire/docs-k8s/spire-agent/agent.yaml)

This is the main manifest of deploying spire-agent.

```yaml
apiVersion: apps/v1
kind: DaemonSet
```

Notice that spire-agent is deployed as DaemonSet, meaning that the Pod is run in each node of the Kubernetes cluster.

Remember Spire agent exposes its Workload API through Unix Domain Socket. This means the consumer of the API, _External Workload_ in the above diagram, needs to run on the same Kubernetes node where spire-agent is run. Running spire-agent as DaemonSet is one way of ensuring spire-agent is always running right beside the workload on the same node.

```yaml
spec:
  containers:
    - name: spire-agent
      volumeMounts:
        - name: spire-agent-socket
          mountPath: /run/spire/sockets
          readOnly: false
  # ...omitted...
  volumes:
    - name: spire-agent-socket
      hostPath:
        path: /run/spire/client-sockets
        type: DirectoryOrCreate
```

These parts are where the socket directory is defined. These essentially mean “Create /run/spire/client-sockets on the *node* where this Pod is run and mount that directory to /run/spire/sockets on the *Pod*.” We need the node file system to share the socket with other containers in separate Pods. We’ll revisit this on the next page where the _External Workload_ deployment is explained.

### [spire-bundle.yaml](https://github.com/wp-wcm/city/blob/main/ns/spire/docs-k8s/spire-agent/spire-bundle.yaml)

This the CA certificate of the spire-server.

As much as spire-server needs to verify the agent (node) through the node attestation step, spire-agent also needs to verify the server it's connecting to.

This ConfigMap is the declaration that spire-agent will trust spire-server that has the certificate under this CA.

### [agent.conf](https://github.com/wp-wcm/city/blob/main/ns/spire/docs-k8s/spire-agent/agent.conf)

This is the configuration file for spire-agent. (cf. [SPIRE Agent Configuration Reference](https://spiffe.io/docs/latest/deploying/spire_agent/)).

```conf
agent {
  # ...omitted...
  server_address = "spire-tutorial.cityos-dev.woven-planet.tech"
  server_port = "8081"
  socket_path = "/run/spire/sockets/agent.sock"
  trust_bundle_path = "/run/spire/bundle/bundle.crt"
  trust_domain = "spire-tutorial.cityos-dev.woven-planet.tech"
}
```

This is the core configuration.

- Address and port: spire-server's address and port that this agent is connecting to.
- socket_path: Where to place Workload API socket. Notice it matches with `mountPath` of the agent's DaemonSet above.
- trust_bundle_path: Where to place the bundle file declared in `spire-bundle.yaml`.
- trust_domain: [Trust domain](https://spiffe.io/docs/latest/spiffe-about/spiffe-concepts/#trust-domain) of this spire-server&agent(s). Note that the domain in `server_address` and `trust_domain` are independent, and having different values for them is a valid configuration (while this example happnens to use the same value).


```conf
plugins {
  # ...omitted...
}
```

This is the plugin configuration. As said, NodeAttestor and WorkloadAttestor are configured as plugins.
