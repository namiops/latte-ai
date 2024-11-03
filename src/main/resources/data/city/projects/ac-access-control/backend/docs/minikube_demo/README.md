# Demo Project for Minikube

This folder contains a sample project to run in the local `minikube` environment.
This project is intended for introducing a local Kubernetes environment and learning very basic operations.

*minikube is local Kubernetes, focusing on making it easy to learn and develop for Kubernetes.* (from [the link](https://minikube.sigs.k8s.io/docs/start/))

## Set up

### Tools

Install the following tools.

- [docker](https://docs.docker.com/desktop/install/linux-install/)
- [kubectl](https://kubernetes.io/docs/tasks/tools/install-kubectl-linux/#install-using-native-package-management)
- [k9s](https://k9scli.io/topics/install/)
  - k9s is not necessary, but very useful to operate Kubernetes clusters.

### Set up Minikube Environment

#### Install minikube

Install `minikube` to your local machine following to the [installation guide](https://minikube.sigs.k8s.io/docs/start/).  
After installation, you can access the minikube cluster using `kubectl`.

```bash
kubectl get po -A
# a pod list will be shown
```

#### Install Istio

`Istio` is a service networking layer that *extends Kubernetes to establish a programmable, application-aware network using the powerful Envoy service proxy*. ([original link](https://istio.io/))  
This demo app uses access control feature of this software.  

Here, configure `Istio` to the default namespace in your `minikube` cluster following to the [installation guide](https://istio.io/latest/docs/setup/getting-started/#download).

- Proceed the process until `Install Istio`

After installation, you can see some pods are running in `istio-system` namespace.

```bash
kubectl get po -n istio-system
NAME                                    READY   STATUS    RESTARTS        AGE
istio-egressgateway-6cf4f6489d-pk2dz    1/1     Running   4 (3h37m ago)   28d
istio-ingressgateway-7b84b459d6-5vszg   1/1     Running   4 (3h37m ago)   28d
istiod-8498fbd896-zcsjt                 1/1     Running   4 (3h37m ago)   28d
```

## Run Sample Project

1. Build images

```bash
cd k8s
docker compose build # build docker images
minikube image load acbe-minikube/server:local acbe-minikube/ubuntu:local # load local images so that minikube cluster can use them
```

2. Apply deployment files

The following command will create resources defined in `.yaml` files to the `default` namespace.

```bash
cd path/to/docs/minikube_demo
kubectl apply -k k8s/
# resources will be created
service/server created
deployment.apps/server created
deployment.apps/ubuntu created
authorizationpolicy.security.istio.io/server-policy created
```

Let's see the list of resources:

```bash
kubectl get all

NAME                         READY   STATUS    RESTARTS   AGE
pod/server-bbbff665f-rh9bs   2/2     Running   0          69s
pod/ubuntu-57f84b84b-sbzpb   2/2     Running   0          69s

NAME                 TYPE        CLUSTER-IP    EXTERNAL-IP   PORT(S)    AGE
service/kubernetes   ClusterIP   10.96.0.1     <none>        443/TCP    28d
service/server       ClusterIP   10.111.6.19   <none>        8080/TCP   69s

NAME                     READY   UP-TO-DATE   AVAILABLE   AGE
deployment.apps/server   1/1     1            1           69s
deployment.apps/ubuntu   1/1     1            1           69s

NAME                               DESIRED   CURRENT   READY   AGE
replicaset.apps/server-bbbff665f   1         1         1       69s
replicaset.apps/ubuntu-57f84b84b   1         1         1       69s
```

As you can see, there are an HTTP server named `server-xxx-yyy` and an ubuntu pod named `ubuntu-zzz-www`.

3. Update configurations

Next, let's change the settings.  
Here, we will add a new environment variable to the `ubuntu` pod as an example.  
Add the following definition to `./ubuntu/deployment.yaml`.

```yaml
            - name: POD_UID # EXISTING ITEM
              valueFrom:
                fieldRef:
                  fieldPath: metadata.uid
            - name: NEW_ENV # ADD THIS ITEM
              value: newValue
```

Now, the command `kubectl apply -k k8s/` again will reflect the changes.  
You can see the `ubuntu` app has been configured.

```bash
$ kubectl apply -k k8s/

service/server unchanged
deployment.apps/server unchanged
deployment.apps/ubuntu configured
authorizationpolicy.security.istio.io/server-policy unchanged
```

```bash
# enter the ubuntu pod
kubectl exec -it $(kubectl get pod -l app=ubuntu -o=jsonpath={.items[*].metadata.name}) -- /bin/bash
root@ubuntu-5549d59c56-wn5ks:/# echo $NEW_ENV
newValue # environment value is set
```

4. Delete resources

The following command will delete all resources.

```bash
kubectl delete -k k8s/
```

For more information on `kubectl`, you can refer to [kubectl cheat sheet](https://kubernetes.io/docs/reference/kubectl/cheatsheet/).

## Example of Experiments

This section shows some basic experiments on this project, to learn some behaviors or how to use Kubernetes.

### Call HTTP API From Your Local Machine

You can send requests to the HTTP pod by port-forwarding.

```bash
$ kubectl port-forward $(kubectl get pod -l app=server -o=jsonpath={.items[*].metadata.name}) 8888:8080
Forwarding from 127.0.0.1:8888 -> 8080
Forwarding from [::1]:8888 -> 8080
Handling connection for 8888
Handling connection for 8888
# another tab
$ curl localhost:8888/hello
{"hello":"kubenetes!"}
```

### Modify Access Control Policy

By default, the `ubuntu` pod can access the HTTP server.

```bash
kubectl exec -it $(kubectl get pod -l app=ubuntu -o=jsonpath={.items[*].metadata.name}) -- /bin/bash
root@ubuntu-57f84b84b-l8nth:/# curl server.default.svc.cluster.local:8080/hello
{"hello":"kubenetes!"}
```

Now, let's modify the policy to deny the request from `ubuntu`.

`./server/authorizationpolicy.yaml`

```yaml
  selector:
    matchLabels:
      app: server
  action: DENY # change this value from ALLOW to DENY
```

And apply it.

```bash
kubectl apply -k k8s/
```

After the update, you can see the HTTP API is no longer accessible from the ubuntu pod.

```bash
kubectl exec -it $(kubectl get pod -l app=ubuntu -o=jsonpath={.items[*].metadata.name}) -- /bin/bash
root@ubuntu-5549d59c56-wn5ks:/# curl server.default.svc.cluster.local:8080/hello
RBAC: access denied
```
