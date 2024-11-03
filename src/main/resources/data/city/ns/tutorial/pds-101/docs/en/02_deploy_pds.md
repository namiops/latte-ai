# Step 1: Setting up PDS

In this sections we will be setting up the services required to have a working PDS environment.

## Deploy PDS and Privacy

Our PDS and Privacy deployments are provided based on a local Kubernetes deployment. The source this is based on can be found [**here**](https://github.tri-ad.tech/cityos-platform/cityos/tree/main/infrastructure/k8s/local) under the pds and privacy folders.

The actual kubernetes manifests we will be deploying is in this project location [**here**](https://github.tri-ad.tech/cityos-platform/cityos/tree/main/ns/tutorial/pds-101/faceapp/kubernetes)

If you're not already in the **local infrastructure root** lets go there, from the **project root** `city os`:

```shell
cd infrastructure/k8s/local
```

We will first set up a local minikube using the bootstrap script. Set your GITHUB_USERNAME and GITHUB_TOKEN as detailed in the k8s/local [README.md](https://github.tri-ad.tech/cityos-platform/cityos/tree/main/infrastructure/k8s/local)

```shell
$ export GITHUB_USERNAME=<your_username>
$ export GITHUB_TOKEN=<your_token>
```

Now run the bootstrap script itself

```shell
$ ./bin/bootstrap
```

You should see many kubernetes resources being created.

Let's check the pods to see our cluster status

```shell
kubectl get pods --all-namespaces
NAMESPACE       NAME                                                     READY   STATUS      RESTARTS   AGE
cert-manager    cert-manager-6d8d6b5dbb-8lvht                            1/1     Running     0          8m11s
cert-manager    cert-manager-cainjector-d6cbc4d9-c6m9n                   1/1     Running     0          8m11s
cert-manager    cert-manager-webhook-85fb68c79b-lp6pj                    1/1     Running     0          8m11s
city-ingress    ingressgateway-6c88bfcfc7-2l5zn                          1/1     Running     0          6m44s
cityos-system   cityservice-operator-controller-555774847f-v4k82         3/3     Running     1          6m38s
cityos-system   keycloak-operator-6f89879454-gbmsw                       2/2     Running     1          6m38s
cityos-system   postgres-operator-79546674d5-8t5w4                       2/2     Running     1          6m38s
flux-system     helm-controller-5cb79d8486-zh72s                         1/1     Running     0          9m10s
flux-system     kustomize-controller-764db65447-qglnh                    1/1     Running     0          9m10s
flux-system     notification-controller-7754494c4d-bkht6                 1/1     Running     0          9m10s
flux-system     source-controller-69dd4bb77c-zgj8c                       1/1     Running     0          9m10s
id              db-agora-kvs-id-id-secure-kvs-0                          3/3     Running     0          4m41s
id              db-agora-kvs-id-id-secure-kvs-1                          3/3     Running     0          4m41s
id              db-agora-kvs-id-id-secure-kvs-2                          3/3     Running     0          4m41s
id              keycloak-0                                               2/2     Running     0          4m7s
id              postgresql-instance1-hb6g-0                              5/5     Running     0          4m44s
id              postgresql-repo-host-0                                   2/2     Running     0          4m44s
id              secret-sync-helper-hmk6d                                 0/1     Completed   0          4m44s
id              security-token-service-5d4f965ccd-jfknv                  2/2     Running     0          4m44s
id              security-token-service-v2-84b77cb999-sfwxs               2/2     Running     0          4m44s
id              steelcouch-agora-kvs-id-id-secure-kvs-6b5f8c9b84-hjvmp   2/2     Running     0          4m41s
id              sts-init-helper-65ndg                                    0/1     Completed   0          4m44s
id              ums-77876697f6-f6d2h                                     2/2     Running     0          4m44s
istio-system    istiod-1-14-1-5874b9b947-plp7g                           1/1     Running     0          7m12s
kube-system     coredns-558bd4d5db-bx4hs                                 1/1     Running     0          9m10s
kube-system     etcd-minikube                                            1/1     Running     0          9m19s
kube-system     kube-apiserver-minikube                                  1/1     Running     0          9m24s
kube-system     kube-controller-manager-minikube                         1/1     Running     0          9m19s
kube-system     kube-proxy-m24qs                                         1/1     Running     0          9m10s
kube-system     kube-scheduler-minikube                                  1/1     Running     0          9m19s
kube-system     storage-provisioner                                      1/1     Running     0          9m21s
observability   grafana-7f6c97c4f4-h4mjd                                 3/3     Running     0          6m34s
observability   jaeger-9dd685668-zskmx                                   1/1     Running     0          6m42s
observability   kiali-86d69768d4-4zr6p                                   1/1     Running     0          6m37s
observability   nginx-775d76557-nzztl                                    2/2     Running     0          6m42s
observability   prometheus-55b654b7cb-4tbwq                              2/2     Running     0          6m34s
```

`Hey! My containers don't look like that!`

You might see something closer to the following

```shell
NAMESPACE     NAME                                       READY   STATUS              RESTARTS   AGE
flux-system   helm-controller-5cb79d8486-zh72s           0/1     Pending             0          1s
flux-system   kustomize-controller-764db65447-qglnh      0/1     Pending             0          1s
flux-system   notification-controller-7754494c4d-bkht6   0/1     Pending             0          1s
flux-system   source-controller-69dd4bb77c-zgj8c         0/1     Pending             0          1s
kube-system   coredns-558bd4d5db-bx4hs                   0/1     Pending             0          1s
kube-system   etcd-minikube                              1/1     Running             0          10s
kube-system   kube-apiserver-minikube                    1/1     Running             0          15s
kube-system   kube-controller-manager-minikube           1/1     Running             0          10s
kube-system   kube-proxy-m24qs                           0/1     ContainerCreating   0          1s
kube-system   kube-scheduler-minikube                    1/1     Running             0          10s
kube-system   storage-provisioner                        0/1     Pending             0          12s
```

That just means you're following along very quickly, the fully completed cluster may take 5 to 10 minutes to spin up in some cases. Wait a bit for all the pods to be ready. In certain cases it is also related to poor network connectivity and/or flux trying to bring up multiple services.
You can check whether flux is associated with repository in GitHub by following command. If not, check the setting according to k8s/local [README.md](https://github.tri-ad.tech/cityos-platform/cityos/tree/main/infrastructure/k8s/local)

```Shell
$ flux get sources git cityos
NAMESPACE       NAME    REVISION                     SUSPENDED       READY   MESSAGE
flux-system     cityos  <branch_name>/<commit_id>      False           True    stored artifact for revision '<branch_name>/<commit_id>'
```

Once your pods look like the first list let's now deploy the pds and privacy kubernetes manifests

```shell
$ kubectl apply -k ./pds/
namespace/pds created
service/pds created
deployment.apps/pds created
helmrelease.helm.toolkit.fluxcd.io/pds-secure-kvs created
virtualservice.networking.istio.io/pds-service created

$ kubectl apply -k ./data-privacy/
namespace/data-privacy created
service/data-privacy-admin created
service/data-privacy-be created
service/data-privacy-fe created
deployment.apps/data-privacy-service created

$ kubectl get pods -n data-privacy
NAME                                    READY   STATUS    RESTARTS   AGE
data-privacy-service-7845747684-j48qr   2/2     Running   0          38s 

$ kubectl get pods -n pds
NAME                                                       READY   STATUS    RESTARTS   AGE
db-agora-kvs-pds-pds-secure-kvs-0                          3/3     Running   0          77s
db-agora-kvs-pds-pds-secure-kvs-1                          3/3     Running   0          77s
db-agora-kvs-pds-pds-secure-kvs-2                          3/3     Running   0          77s
pds-758784c57c-x44pp                                       2/2     Running   2          79s
steelcouch-agora-kvs-pds-pds-secure-kvs-5b9d4c76dd-7pxpg   2/2     Running   0          77s
```

Great! We now have pds and privacy running in our local cluster.
