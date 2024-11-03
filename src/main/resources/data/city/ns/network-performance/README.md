# Kubernetes Networking Performance

## Objectives

Measurement of Kubernetes networking performance by using different command line tools installed in worker pods

## Implementation

The benchmark can be executed via a single Go binary invocation that triggers scheduling worker pods on all available Kubernetes nodes. The worker pods use a custom docker container with cli tools (e.g. iperf3, qperf, netperf, fortio) installed. 

### Workers

Are debian-based docker containers with CLI tools installed. The workers first connect to the Orchestrator gRPC server, receive CLI commands and arguments (benchmark Tasks) and send stdout/stderr (results).

```shell
Usage of /ns/network-performance/cmd/worker/image.binary:
  -deadline duration
        time to finish all tasks and exit (default 30m0s)
  -host string
        Orchestrator gRPC host
  -port int
        Orchestrator gRPC Port
```

### Orchestrator

The orchestrator pod is a gRPC server that creates tasks for a specific benchmark. It distributes tasks to connected worker pods in a serial order, and stores the results.

It also allows parsing the results into Prometheus metrics and pushing to PushGateway.

```shell
Usage of /ns/network-performance/cmd/orchestrator/image.binary:
  -benchmark value
        iperf3-tcp|netperf-tcp-rr|netperf-tcp-stream|qperf-tcp|fortio-tcp|fortio-http
  -host string
        IP address to bind to (default "0.0.0.0")
  -port int
        Port to listen on
  -results string
        file to save results
  -timeout duration
        timeout for worker registeration, task allocation and post cleanup
```

### Launcher

The launcher creates a test namespace and deploys the Orchestrator and Worker Pods and Services.
After all the benchmark tasks are done, the results in the Orchestrator pod are exported to a JSON formatted file. The Launcher copies the JSON file to its local disk.

The Orchestrator and Worker pods run independently of the Launcher, with the Orchestrator pod sending tasks to Workers and Workers communicating the results back to the Orchestrator.

- The Launcher requires to be executed in a pod running in a Kubernetes cluster with more than 2 nodes.
- The Kubernetes nodes must have the label `node-lifecycle=on-demand`.
- The Launcher requires ServiceAccounts and ClusterRoles that allows (within proper limits) create/list/delete actions for namespaces, pods, services, deployments.

```shell
Usage of /ns/network-performance/cmd/launcher/image.binary:
  -benchmark value
        iperf3-tcp|netperf-tcp-rr|netperf-tcp-stream|qperf-tcp|fortio-tcp|fortio-http
  -deadline duration
        deadline for execution (default 30m0s)
  -inject
        Namespace istio injection. Cannot be used with netperf, qperf benchmark
  -namespace string
        Test namespace for Orch/Worker pods deployment
  -node-labels string
        a node label selector for scheduling the orchestrator and worker pods. Empty schedules on all available nodes. (default "node-lifecycle=on-demand")
  -orchImage string
        Docker image for the Orchestrator pod
  -outputFileDirectory string
        Directory for orchestrator outputs (default "/share/networkperformance_results")
  -timeout duration
        timeout for worker registeration, task allocation and post cleanup
  -workerImage string
        Docker image for the Worker pods
```

## CLI tools

[iperf3](https://manpages.ubuntu.com/manpages/xenial/man1/iperf3.1.html)

[netperf-tcp-rr](https://github.com/HewlettPackard/netperf/blob/3bc455b23f901dae377ca0a558e1e32aa56b31c4/doc/netperf.txt#L1933)

[netperf-tcp-stream](https://github.com/HewlettPackard/netperf/blob/3bc455b23f901dae377ca0a558e1e32aa56b31c4/doc/netperf.txt#L1406)

[qperf](https://linux.die.net/man/1/qperf)

[fortio-tcp](https://github.com/fortio/fortio)

[fortio-http](https://github.com/fortio/fortio)

## Benchmark Results Format

```json
{
  "results": [
    {
      "output": String,
      "task": {
        "command": String,
        "args": []String,
        "task_options": {
            "timeout_seconds": int
        },
        "status": int,
        "client": {
          "worker": String,
          "ip": String,
          "vm": {
              "hostname": String,
              "availability_zone": String
          },
          "idle": Boolean
        },
        "server": {
          "worker": String,
          "ip": String,
          "vm": {
              "hostname": String,
              "availability_zone": String
          },
          "idle": Boolean
        }
      }
    }
  ]
}
```

## To add a benchmark (i.e. iperf3, qperf, ...etc.)

We need to:

1. add the CLI tool to the worker image
2. in package `benchmark`: add CLI command to the map `AllowList`
3. in package `benchmark`: use the `Benchmark` struct to create a variable

## To add a metric (i.e. bandwidth, latency ...etc.)

We need to:

1. in package `benchmark`: create two constants with the metric name and the metric unit
2. use the constants to define and add the metric to the relevant benchmark's struct

## Limitations

- Some benchmark require opening ephemeral ports dynamically (i.e. netperf, qperf), these benchmarks are incompatible with istio as we are not able to specify the ports in a Kubernetes services.
- gRPC transport credentials are insecure

## How to test it

Start a test cluster with the following commands:

Create registry container unless it already exists

```sh
reg_name='kind-registry'
reg_port='5000'
if [ "$(docker inspect -f '{{.State.Running}}' "${reg_name}" 2>/dev/null || true)" != 'true' ]; then
  docker run \
    -d --restart=always -p "127.0.0.1:${reg_port}:5000" --network bridge --name "${reg_name}" \
    registry:2
fi
```

Create kind cluster with containerd registry config dir enabled
TODO: kind will eventually enable this by default and this patch will
be unnecessary.

See:
<https://github.com/kubernetes-sigs/kind/issues/2875>
<https://github.com/containerd/containerd/blob/main/docs/cri/config.md#registry-configuration>>
<https://github.com/containerd/containerd/blob/main/docs/hosts.md>>

```sh
cat <<EOF | kind create cluster --config=-
kind: Cluster
apiVersion: kind.x-k8s.io/v1alpha4
nodes:
- role: control-plane
- role: worker
- role: worker
containerdConfigPatches:
- |-
  [plugins."io.containerd.grpc.v1.cri".registry]
    config_path = "/etc/containerd/certs.d"
EOF
```

Add the registry config to the nodes

This is necessary because localhost resolves to loopback addresses that are
network-namespace local.
In other words: localhost in the container is not localhost on the host.

We want a consistent name that works from both ends, so we tell containerd to
alias localhost:${reg_port} to the registry container when pulling images

```sh
REGISTRY_DIR="/etc/containerd/certs.d/localhost:${reg_port}"
for node in $(kind get nodes); do
  docker exec "${node}" mkdir -p "${REGISTRY_DIR}"
  cat <<EOF | docker exec -i "${node}" cp /dev/stdin "${REGISTRY_DIR}/hosts.toml"
[host."http://${reg_name}:5000"]
EOF
done
```

Connect the registry to the cluster network if not already connected
This allows kind to bootstrap the network but ensures they're on the same network

```sh
if [ "$(docker inspect -f='{{json .NetworkSettings.Networks.kind}}' "${reg_name}")" = 'null' ]; then
  docker network connect "kind" "${reg_name}"
fi
```

Document the local registry
<https://github.com/kubernetes/enhancements/tree/master/keps/sig-cluster-lifecycle/generic/1755-communicating-a-local-registry>

```sh
cat <<EOF | kubectl apply -f -
apiVersion: v1
kind: ConfigMap
metadata:
  name: local-registry-hosting
  namespace: kube-public
data:
  localRegistryHosting.v1: |
    host: "localhost:${reg_port}"
    help: "https://kind.sigs.k8s.io/docs/user/local-registry/"
EOF
```

Deploy Istio system for injection to work:

```sh
kubectl apply -k ../../infrastructure/k8s/local/istio-system
```

The required resources for the network performance package are:

- a `ClusterRole`,
- a `ServiceAccount`,
- a `ClusterRoleBinding` for the `ServiceAccount`,
- a `Job` for the launcher, using the image: `docker.artifactory-ha.tri-ad.tech/wcm-cityos/ahmed/nptests_launcher:latest`
- additionally, a `PersistentVolumeClaim`
- a `Pod` (`reader`) to read the `PersistentVolumeClaim` while testing locally.

Run the following commands to apply the above resources:

```sh
kubectl apply -f - <<EOF
apiVersion: v1
kind: ServiceAccount
metadata:
  name: testkube-networkperformance
---
apiVersion: rbac.authorization.k8s.io/v1
kind: ClusterRole
metadata:
  name: networkperformance-cluster-role
rules:
- apiGroups: ["*"]
  resources: ["namespaces", "deployments", "services", "pods", "pods/log", "pods/exec"]
  verbs: ["get", "list", "watch", "create", "update", "delete"]
- apiGroups: ["*"]
  resources: ["nodes"]
  verbs: ["get", "list", "watch"]
---
apiVersion: rbac.authorization.k8s.io/v1
kind: ClusterRoleBinding
metadata:
  name: networkperformance-cluster-rolebinding
roleRef:
  apiGroup: rbac.authorization.k8s.io
  kind: ClusterRole
  name: networkperformance-cluster-role
subjects:
- kind: ServiceAccount
  name: testkube-networkperformance
  namespace: default
---
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: kind-networkperformance-pvc
spec:
  accessModes:
    - ReadWriteOnce
  resources:
    requests:
      storage: 1Gi
---
apiVersion: v1
kind: Pod
metadata:
  name: reader
spec:
  containers:
  - name: reader
    image: busybox
    command:
      - /bin/sh
      - -c
      - sleep 3600
    volumeMounts:
    - name: data-volume
      mountPath: /share
  volumes:
  - name: data-volume
    persistentVolumeClaim:
      claimName: kind-networkperformance-pvc
EOF
```

Build images with bazel and run the network performance benchmark job:

- In a new terminal, build and push images to the kind registry:

```sh
bazel run //ns/network-performance/cmd/worker:image.load
docker tag ns/network-performance/cmd/worker:image localhost:5000/worker && docker push localhost:5000/worker

bazel run //ns/network-performance/cmd/orchestrator:image.load
docker tag ns/network-performance/cmd/orchestrator:image localhost:5000/orchestrator && docker push localhost:5000/orchestrator

bazel run //ns/network-performance/cmd/launcher:image.load
docker tag ns/network-performance/cmd/launcher:image localhost:5000/launcher && docker push localhost:5000/launcher
```

- Run the network performance job (inject Istio sidecar by adding to the args: `-inject`):

```sh
kubectl delete job kind-network-performance
kubectl apply -f - <<EOF
apiVersion: batch/v1
kind: Job
metadata:
  name: kind-network-performance
spec:
  template:
    spec:
      serviceAccountName: testkube-networkperformance
      containers:
      - image: localhost:5000/launcher
        imagePullPolicy: Always
        name: launcher
        args:
        - -benchmark
        # iperf3-tcp|netperf-tcp-rr|netperf-tcp-stream|qperf-tcp|fortio-tcp|fortio-http
        - netperf-tcp-rr
        - -orchImage
        - localhost:5000/orchestrator
        - -workerImage
        - localhost:5000/worker
        - -outputFileDirectory
        - /share/networkperformance_results
        - -namespace
        - netperf-test
        - -node-labels
        - ""
        - -timeout
        - "30s"
        - -deadline
        - "30m"
        volumeMounts:
        - mountPath: /share
          name: data-volume
      restartPolicy: Never
      volumes:
      - name: data-volume
        persistentVolumeClaim:
          claimName: kind-networkperformance-pvc
  backoffLimit: 2
EOF
```

for subsequent job runs, here is a shorter command:

```sh
kubectl get job kind-network-performance -o json | jq 'del(.spec.selector)' | jq 'del(.spec.template.metadata.labels)' | kubectl replace --force -f -
```

The results are stored in the directory `/share/networkperformance_results` in the PersistentVolume. To access the results, we use the `reader` Pod:

```sh
kubectl exec -it reader -- find /share/networkperformance_results/ -type f -exec cat {} \;
```

To clean up the created kind cluster, run the following command:

```sh
kind delete cluster
```
