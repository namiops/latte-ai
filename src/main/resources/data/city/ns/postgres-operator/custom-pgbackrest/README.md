Customized container image of crunchy-pgbackrest
---

### What's customized

This is the custom image of [crunchy-pgbackrest](https://access.crunchydata.com/documentation/crunchy-postgres-containers/4.7.4/container-specifications/crunchy-pgbackrest/), which is the image used by the PGO's K8S Jobs, to apply the following modifications.

#### End the Istio sidecar container after finishing the K8S Job process

Swap the original entrypoint shell script to add a post process to call `quitquitquit` end point of the Istio sidecar container.

### Base image

registry.developers.crunchydata.com/crunchydata/crunchy-pgbackrest:centos8-2.36-0
