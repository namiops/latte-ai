# external-snapshotter-6.1-agora1

This directory hosts the yamls for [Snapshot Controller](https://kubernetes-csi.github.io/docs/external-snapshotter.html#csi-external-snapshotter). 

## Manual Operation

```sh
curl -s https://raw.githubusercontent.com/kubernetes-csi/external-snapshotter/release-6.1/client/config/crd/snapshot.storage.k8s.io_volumesnapshotclasses.yaml --output ./tmp-snapshot.storage.k8s.io_volumesnapshotclasses.yaml
kubectl slice -f ./tmp-snapshot.storage.k8s.io_volumesnapshotclasses.yaml -o ./

curl -s https://raw.githubusercontent.com/kubernetes-csi/external-snapshotter/release-6.1/client/config/crd/snapshot.storage.k8s.io_volumesnapshotcontents.yaml --output ./tmp-snapshot.storage.k8s.io_volumesnapshotcontents.yaml
kubectl slice -f ./tmp-snapshot.storage.k8s.io_volumesnapshotcontents.yaml -o ./

curl -s https://raw.githubusercontent.com/kubernetes-csi/external-snapshotter/release-6.1/client/config/crd/snapshot.storage.k8s.io_volumesnapshots.yaml --output ./tmp-snapshot.storage.k8s.io_volumesnapshots.yaml
kubectl slice -f ./tmp-snapshot.storage.k8s.io_volumesnapshots.yaml -o ./

curl -s https://raw.githubusercontent.com/kubernetes-csi/external-snapshotter/release-6.1/deploy/kubernetes/snapshot-controller/rbac-snapshot-controller.yaml --output ./tmp-rbac-snapshot-controller.yaml
sed -Ei '/namespace:/{s/:.+/: toolings/}' ./tmp-rbac-snapshot-controller.yaml
kubectl slice -f ./tmp-rbac-snapshot-controller.yaml -o ./

curl -s https://raw.githubusercontent.com/kubernetes-csi/external-snapshotter/release-6.1/deploy/kubernetes/snapshot-controller/setup-snapshot-controller.yaml --output ./tmp-setup-snapshot-controller.yaml
sed -Ei '/namespace:/{s/:.+/: toolings/}' ./tmp-setup-snapshot-controller.yaml
kubectl slice -f ./tmp-setup-snapshot-controller.yaml -o ./

rm -f ./tmp-*.yaml ./kustomization.yaml ./-.yaml
kustomize create --autodetect
yamlfmt ./*.yaml
```

TODO: Write the script to generate these manifests.
