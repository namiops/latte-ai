# Using the EKS cluster

## Kubeconfig

### Auto-way
Run `terraform apply`. This will create a `kubeconfig.yaml` configuration file
for the sandbox cluster.

Set the `$KUBECONFIG` environment variable like this (assuming you are in the
directory where the file is).

```sh
export KUBECONFIG=${PWD}/kubeconfig.yaml
```

Now access with `kubectl` should work.

The token expires after 15 minutes. If you want just to recreate the token, but
not run a full apply you can run:
```sh
terraform apply -target=local_file.kubeconfig;
```
or even better, run:
```sh
aws eks update-kubeconfig --region ap-northeast-1 --name eks --profile sandbox
```

## status check
After you freshly deployed the EKS cluster using terraform you can check it's status.
Here an example of how things should look like.

### Example ###
```sh
$ kubectl get all -A
NAMESPACE     NAME                           READY   STATUS    RESTARTS   AGE
kube-system   pod/aws-node-sg246             1/1     Running   0          103m
kube-system   pod/aws-node-zljfc             1/1     Running   0          103m
kube-system   pod/coredns-5b6d4bd6f7-8tzc2   1/1     Running   0          112m
kube-system   pod/coredns-5b6d4bd6f7-cgh67   1/1     Running   0          112m
kube-system   pod/kube-proxy-8hwzp           1/1     Running   0          103m
kube-system   pod/kube-proxy-c2pvv           1/1     Running   0          103m

NAMESPACE     NAME                 TYPE        CLUSTER-IP    EXTERNAL-IP   PORT(S)         AGE
default       service/kubernetes   ClusterIP   172.20.0.1    <none>        443/TCP         112m
kube-system   service/kube-dns     ClusterIP   172.20.0.10   <none>        53/UDP,53/TCP   112m

NAMESPACE     NAME                        DESIRED   CURRENT   READY   UP-TO-DATE   AVAILABLE   NODE SELECTOR   AGE
kube-system   daemonset.apps/aws-node     2         2         2       2            2           <none>          112m
kube-system   daemonset.apps/kube-proxy   2         2         2       2            2           <none>          112m

NAMESPACE     NAME                      READY   UP-TO-DATE   AVAILABLE   AGE
kube-system   deployment.apps/coredns   2/2     2            2           112m

NAMESPACE     NAME                                 DESIRED   CURRENT   READY   AGE
kube-system   replicaset.apps/coredns-5b6d4bd6f7   2         2         2       112m
```
```sh
$ kubectl get nodes
NAME                                              STATUS   ROLES    AGE    VERSION
ip-10-25-232-63.ap-northeast-1.compute.internal   Ready    <none>   104m   v1.22.17-eks-a59e1f0
ip-10-25-234-79.ap-northeast-1.compute.internal   Ready    <none>   104m   v1.22.17-eks-a59e1f0
```

