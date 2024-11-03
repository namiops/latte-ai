CityOS Kubernetes Manifests
===========================

This directory is the root of all production kubernetes manifests owned by the CityOS team.

## Directory Layout

Files in this directory should be organized according to the following template.

<cluster_name>/<namespace>/

A [Kustomization](https://kubectl.docs.kubernetes.io/references/kustomize/kustomization/) object MUST be present in the root of each cluster directory.
This object serves as the main entry point that FluxCD will use to deploy manifests to the cluster.
This root kustomization SHOULD only contain resource entries pointing to namespace subdirectories.

## Cluster Registry

| Cluster Name | Account ID   | Description                                    |
| ------------ | ------------ | -----------------------------------------------|
| ci           | 093116320723 | Cluster to host ci related service             |
| dev          | 835215587209 | Agora dev environment                          |
| example      | N/A          | examples used in this README                   |
| lab          | 370564492268 | Agora lab environment                          |
| local        | N/A          | A local cluster using minikube on your dev env / laptop |

## Global reserved namespaces 

The following namespaces are reserved and should not be added to the top-level kustomization entry point.
Objects contained in these namespaces are applied to the cluster by means other than FluxCD

* flux-system

Additional namespaces MAY be reserved on a per cluster basis.
These reserved namespaces MUST be documented in a README.md in the root of the cluster directory

## Common Tasks

### Use script to update kubeconfig

Run the following script to add the existing kubeconfigs to your machine

```bash
./bin/merge_kubeconfig environments/lab2/clusters/mgmt-east/kubeconfig.yaml
./bin/merge_kubeconfig environments/lab2/clusters/worker1-east/kubeconfig.yaml
./bin/merge_kubeconfig environments/lab2/clusters/worker1-west/kubeconfig.yaml
```

### Adding a new namespace directory

Scenario: As a service developer I would like to create a new namespaced and have FluxCD ship the resources I specify

```sh
$ cd <cluster_name> # e.g.: example, lab, dev...
$ mkdir new_namespace
$ cd new_namespace
$ kustomize create --autodetect 
$ cd ../
```

This will create a `kustomization.yaml` file under the new namespace's folder, and add any manifests that exist in the folder to it.
Next, edit [<cluster_name>/kustomization.yaml](example/kustomization.yaml) and add an entry for `new_namespace` in the `resources` field:

```yaml
apiVersion: kustomize.config.k8s.io/v1beta1
kind: Kustomization
resources:
- mynamespace
- new_namespace
```

You can validate your changes locally before commiting by having kustomize render the resulting manifest

```sh
$ kustomize build <cluster_name>/
```
