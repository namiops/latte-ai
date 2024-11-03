### How to run spire-agent in local k8s cluster and have it talk with spire-server in Lab

#### Prerequisite

- A local k8s cluster is running
    ```
    !!! Note
        The spire-server in Lab is only accessible with GlobalProtect VPN conencted.
        This means dev-ec2 can't be used for this. Use your laptop.
    ```
- AWS CLI is authen'ed against Lab
- `kubectx`, `jq` are installed
- Your machine is connected with GlobalProtect VPN.

#### Steps

1. Save spire-server's upstream CA cert to tell the agent to trust the server.
    1. Get the cert
        ```sh
        kubectx lab

        # `httpbin-cert` is just one example cert that comes from the same CA as spire-server's.
        # The below command will print the CA cert Spire is also using.
        kubectl -n spire-sample get secret httpbin-cert -ojsonpath='{.data.ca\.crt}' | base64 -d
        ```
    2. Create `spire-bundle-configmap-lab.yaml` and put this contents.
        ```yaml
        apiVersion: v1
        kind: ConfigMap
        metadata:
          name: spire-bundle
          namespace: spire-agent-ns
        data:
          bundle.crt: |
            -----BEGIN CERTIFICATE-----
            <PUT UPSTREAM CERT HERE>
            -----END CERTIFICATE-----
        ```
    3. Apply it
        ```sh
        kubectx <YOUR_LOCAL_CONTEXT>
        kubectl create ns spire-agent-ns
        kubectl apply -f spire-bundle-configmap-lab.yaml
        ```
2. Get the spire-agent keypair for x509pop node attestation.
    1. Talk to agora-data-orchestration members to share the credentials, save them as `tls.crt`, `tls.key`.
    2. Save them as secret.
        ```sh
        kubectl -n spire-agent-ns create secret generic spire-agent-cert --from-file=./tls.crt --from-file=./tls.key
        ```
3. Run the agent
    1. Create `spire-agent.yaml` and put this contents.
        ```yaml
        apiVersion: v1
        kind: ServiceAccount
        metadata:
          name: spire-agent
          namespace: spire-agent-ns
        ---
        # Required cluster role to allow spire-agent to query k8s API server
        kind: ClusterRole
        apiVersion: rbac.authorization.k8s.io/v1
        metadata:
          name: client-side-spire-agent-cluster-role
        rules:
        - apiGroups: [""]
          resources: ["pods","nodes","nodes/proxy"]
          verbs: ["get"]
        ---
        # Binds above cluster role to spire-agent service account
        kind: ClusterRoleBinding
        apiVersion: rbac.authorization.k8s.io/v1
        metadata:
          name: client-side-spire-agent-cluster-role-binding
        subjects:
        - kind: ServiceAccount
          name: spire-agent
          namespace: spire-agent-ns
        roleRef:
          kind: ClusterRole
          name: client-side-spire-agent-cluster-role
          apiGroup: rbac.authorization.k8s.io
        ---
        apiVersion: v1
        kind: ConfigMap
        metadata:
          name: spire-agent
          namespace: spire-agent-ns
        data:
          agent.conf: |
            agent {
              data_dir = "/run/spire"
              log_level = "DEBUG"
              server_address = "spire-server.agora-lab.woven-planet.tech"
              server_port = "8081"
              socket_path = "/run/spire/sockets/agent.sock"
              trust_bundle_path = "/run/spire/bundle/bundle.crt"
              trust_domain = "spire.agora-lab.woven-planet.tech"
            }

            plugins {
              NodeAttestor "x509pop" {
                plugin_data {
                  private_key_path = "/opt/spire/conf/agent/agent.key.pem"
                  certificate_path = "/opt/spire/conf/agent/agent.crt.pem"
                }
              }

              KeyManager "memory" {
                plugin_data {
                }
              }

              WorkloadAttestor "k8s" {
                plugin_data {
                  # Defaults to the secure kubelet port by default.
                  # Minikube does not have a cert in the cluster CA bundle that
                  # can authenticate the kubelet cert, so skip validation.
                  skip_kubelet_verification = true
                }
              }

              WorkloadAttestor "unix" {
                  plugin_data {
                  }
              }
            }

            health_checks {
              listener_enabled = true
              bind_address = "0.0.0.0"
              bind_port = "8089"
              live_path = "/live"
              ready_path = "/ready"
            }
        ---
        apiVersion: apps/v1
        kind: DaemonSet
        metadata:
          name: spire-agent
          namespace: spire-agent-ns
          labels:
            app: spire-agent
        spec:
          selector:
            matchLabels:
              app: spire-agent
          template:
            metadata:
              labels:
                app: spire-agent
            spec:
              hostPID: true
              hostNetwork: true
              dnsPolicy: ClusterFirstWithHostNet
              serviceAccountName: spire-agent
              containers:
                - name: spire-agent
                  image: ghcr.artifactory-ha.tri-ad.tech/spiffe/spire-agent:1.6.3
                  args: ["-config", "/run/spire/config/agent.conf"]
                  volumeMounts:
                    - name: spire-config
                      mountPath: /run/spire/config
                      readOnly: true
                    - name: spire-bundle
                      mountPath: /run/spire/bundle
                    - name: spire-agent-socket
                      mountPath: /run/spire/sockets
                      readOnly: false
                    - name: spire-agent-cert
                      mountPath: /opt/spire/conf/agent
                      readOnly: true
                  livenessProbe:
                    httpGet:
                      path: /live
                      port: 8089
                    failureThreshold: 2
                    initialDelaySeconds: 15
                    periodSeconds: 60
                    timeoutSeconds: 3
                  readinessProbe:
                    httpGet:
                      path: /ready
                      port: 8089
                    initialDelaySeconds: 5
                    periodSeconds: 5
              volumes:
                - name: spire-config
                  configMap:
                    name: spire-agent
                - name: spire-bundle
                  configMap:
                    name: spire-bundle
                - name: spire-agent-socket
                  hostPath:
                    path: /run/spire/client-sockets
                    type: DirectoryOrCreate
                - name: spire-agent-cert
                  secret:
                    secretName: spire-agent-cert
                    items:
                      - key: tls.crt
                        path: agent.crt.pem
                      - key: tls.key
                        path: agent.key.pem
        ```
    2. Apply it
        ```sh
        kubectl apply -f spire-agent.yaml
        ```
4. Confirm the agent is running
    1. By Pod's status
        ```sh
        kubectl -n spire-agent-ns get pod
        ```

        Expected Output. May take a few mins to before it turns to Running status.
        ```
        NAME                                      READY   STATUS    RESTARTS   AGE
        spire-agent-z8stk                         1/1     Running   0          3m
        ```

        ```
        !!! Note
            Koh Satoh faced some issues running the agent container in Mac M1 chip machine.
            If you face any issues, talk to him. (e.g. `exec /opt/spire/bin/spire-agent: exec format error` is a sign of having the same issue.)
        ```
    2. By the log
        ```sh
        kubectl -n spire-agent-ns logs -f $(kubectl -n spire-agent-ns get po -l app=spire-agent -ojsonpath='{.items[0].metadata.name}')
        ```

        Expected log.
        ```
        time="2023-02-15T04:38:20Z" level=info msg="Node attestation was successful" rettestable=true spiffe_id="spiffe://spire.agora-lab.woven-planet.tech/spire/agent/x509pop/<SOME_ID>" subsystem_name=attestor trust_domain_id="spiffe://spire.agora-lab.woven-planet.tech"
        ```

### How to get X.509 SVID and use it to establish mTLS with an internal workload

#### Prerequisite

- The spire agent is running in your local cluster by following the above steps

#### Steps

1. Create a registration entry for your workload on the Spire server side (i.e. Agora cluster). **This step is already done for you.** (refer to [httpbin-spiffe-id.yaml](https://github.com/wp-wcm/city/blob/main/infrastructure/k8s/common/spire-sample/spire-sample-0.1.0/httpbin-spiffe-id.yaml)) However, in real life, you will apply some manifest like the following in Agora cluster.
    ```yaml
    apiVersion: spiffeid.spiffe.io/v1beta1
    kind: SpiffeID
    metadata:
      name: <NAME_OF_YOUR_CHOICE>
      namespace: <YOUR_NAMESPACE_IN_AGORA>
    spec:
      # The SPIFFE ID of the agent you got above in the log, or through this command
      # `kubectl exec -n spire-server spire-server-0 -- /opt/spire/bin/spire-server agent list`
      parentId: spiffe://spire.agora-lab.woven-planet.tech/spire/agent/x509pop/e3baa4a900ffb7a75d31183b1c3898652478db09
      # cf. https://github.com/spiffe/spire/tree/v1.5.1/support/k8s/k8s-workload-registrar/mode-crd#spiffe-id-custom-resource-example
      selector:
        namespace: <YOUR_NAMESPACE_IN_YOUR_LOCAL_CLUSTER>
        podLabel:
          app: <APP_LABEL_OF_YOUR_WORKLOAD_IN_YOUR_LOCAL_CLUSTER>
      spiffeId: spiffe://spire.agora-lab.woven-planet.tech/<SPIFFE_ID_PATH>
    ```
2. Run a workload in your local cluster.
    1. Create a namespace.
        ```sh
        kubectx <YOUR_LOCAL_CONTEXT>
        kubectl create ns client-test
        ```
    2. Save the following as `Dockerfile`.
        ```
        FROM ghcr.artifactory-ha.tri-ad.tech/spiffe/spire-agent:1.6.3 AS spire-agent

        FROM docker.artifactory-ha.tri-ad.tech/alpine:3.17.3

        WORKDIR /opt/spire
        RUN mkdir -p /opt/spire/bin
        RUN apk add dumb-init ca-certificates curl openssl
        COPY --from=spire-agent /opt/spire/bin/spire-agent bin/spire-agent

        CMD ["sleep", "infinity"]
        ```
    3. Build the image.
        ```sh
        # If you're using minikube, switch the docker env.
        eval $(minikube docker-env)

        docker build -t my-spire-client-test:local .
        ```
    4. Run it.
        1. Create `httpbin-client.yaml` with the following contents.
            ```yaml
            apiVersion: v1
            kind: Pod
            metadata:
              name: httpbin-client
              namespace: client-test
              labels:
                app: httpbin-client
            spec:
              hostPID: true
              hostNetwork: true
              dnsPolicy: ClusterFirstWithHostNet
              containers:
                - name: httpbin-client
                  image: my-spire-client-test:local
                  imagePullPolicy: Never
                  command: ["sleep"]
                  args: ["infinity"]
                  volumeMounts:
                    - name: spire-agent-socket
                      mountPath: /run/spire/sockets
                      readOnly: true
              volumes:
                - name: spire-agent-socket
                  hostPath:
                    path: /run/spire/client-sockets
                    type: Directory
            ```
        2. Apply it.
            ```sh
            kubectx <YOUR_LOCAL_CONTEXT>
            kubectl apply -f httpbin-client.yaml
            ```
3. Get the SVID.
    ```sh
    kubectl -n client-test exec -it httpbin-client -- /bin/sh

    # (Inside the container)
    cd ./bin
    ./spire-agent api fetch -socketPath /run/spire/sockets/agent.sock -write /tmp/
    ```

    Here's the expected output from the last command.
    ```
    Received 1 svid after 9.643776ms

    SPIFFE ID:              spiffe://spire.agora-lab.woven-planet.tech/ns/sni-test/sa/client
    SVID Valid After:       2023-04-21 05:19:58 +0000 UTC
    SVID Valid Until:       2023-04-21 06:20:08 +0000 UTC
    Intermediate #1 Valid After:    2023-04-20 12:30:15 +0000 UTC
    Intermediate #1 Valid Until:    2023-04-21 12:30:45 +0000 UTC
    CA #1 Valid After:      2021-11-08 00:33:34 +0000 UTC
    CA #1 Valid Until:      2031-11-08 01:33:34 +0000 UTC
    CA #2 Valid After:      2023-04-04 08:17:59 +0000 UTC
    CA #2 Valid Until:      2024-04-14 13:06:07 +0000 UTC

    Writing SVID #0 to file /tmp/svid.0.pem.
    Writing key #0 to file /tmp/svid.0.key.
    Writing bundle #0 to file /tmp/bundle.0.pem.
    ```

    `svid.0.pem` is the x509 SVID, svid.0.key is the corresponding private key, and bundle.0.pem is the CA cert.
4. Test `curl`.
    ```sh
    # Expected to get some json response. This means mTLS is successful!
    curl --cacert /tmp/bundle.0.pem --cert /tmp/svid.0.pem --key /tmp/svid.0.key https://httpbin.spire-sample.agora-lab.woven-planet.tech:7029/headers

    # Expected to get `RBAC: access denied` because this internal workload is not configured to allow traffics from
    # the principal used (i.e. `spiffe://spire.agora-lab.woven-planet.tech/ns/spire-sample/sa/client`)
    curl --cacert /tmp/bundle.0.pem --cert /tmp/svid.0.pem --key /tmp/svid.0.key https://nginx.spire-sample.agora-lab.woven-planet.tech:7029
    ```
