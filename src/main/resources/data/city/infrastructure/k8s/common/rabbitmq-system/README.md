# RabbitMQ Operators

This document describes the process of updating RabbitMQ management operators.

Please note that the references to the operators' versions also need to be
updated in appropriate higher-level `kustomization.yaml` files in the respective
cluster-specific infrastructure directories.

## Cluster Operator

GitHub Releases: <https://github.com/rabbitmq/cluster-operator/releases>

Download the YAML manifest and save it in a new subdirectory named
`cluster-operator-<version>` as `cluster-operator.yaml`. The
`kustomization.yaml` file can be regenerated using the `kustomize create
--autodetect` command. However, due to the necessary patches, it is recommended
to use the `kustomization.yaml` and `patch-ns.yaml` from the previously used
version of the operator.

## Messaging Topology Operator

GitHub Releases: <https://github.com/rabbitmq/messaging-topology-operator/releases>

Create a new subdirectory named `messaging-topology-operator-<version>` to store
the resource manifests.

After downloading the YAML manifest, split it into individual resource manifests
using the `kubectl split` command. The `-f` flag is used to specify the source
download file, and the `-o` flag is used to specify the output directory. You
may need to install this command the first time you perform this action, as it
is not available in `kubectl` by default. More information is available in this
repository: <https://github.com/patrickdappollonio/kubectl-slice>.

A `kustomization.yaml` file can be generated using the `kustomize create
--autodetect` command. Patches from the previously used version of the operator
should be carried over from its `kustomization.yaml`. Most notably, the
`Namespace` resource YAML file should be commented out in the newly created
`kustomization.yaml`, and all inline patches should be copied from the old
`kustomization.yaml` into the new one.

Lastly, format the new YAML files using the `yamlfmt ./*.yaml` command. If you
do not have this command available on your system, you can install it by running
`go install github.com/google/yamlfmt/cmd/yamlfmt@latest`.

## Semgrep

There is a possibility that a [semgrep](https://semgrep.dev/) check will fail in
CI as a result of some undesirable configuration in the manifests. In that case,
you should be able to fix it by running the following command. Please not,
however, that this sometimes results in malformed YAML files with improper
indentation, and therefore invalid syntax. It is necessary to manually check
that the changes made by running the command did not have a negative effect on
the manifests.

```sh
semgrep --config auto ./infrastructure/k8s/common/rabbitmq-system/messaging-topology-operator-<version>/ --autofix
```
