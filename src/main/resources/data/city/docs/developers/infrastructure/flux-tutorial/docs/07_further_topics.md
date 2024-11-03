# Further Topics

## Bootstrapping

Flux is installed into agora clusters using the
[bin/bootstrap](https://github.tri-ad.tech/cityos-platform/cityos/tree/main/infrastructure/k8s/local/bin/bootstrap)
script.
This script will kubectl apply the flux-system/base directory, create a secret
using the flux cli, and then reapply the flux system directory to bring up
flux inside the cluster, which will then apply all the remaining services that
are configured in the various kustomization.yaml files.

## File Structure

The Agora repository has the infrastructure deployment seperated per cluster,
with a common folder defining most of the manifests for a given app or system,
and then specifics defined within dev, lab, or learning(deprecated). All of the
applications running within that cluster are then placed within each of these
folders. The seperation of the kustomization files allows us to keep the
reconcilliation running even in the even of an error with a specific
kustomization. If instead the entire cluster definition was handled by a
single kustomization, the reconcilliation would stop in the event of any error.

## Tenants

Some of the services that will run on Agora are not located within the Agora
repository. To handle this flux allows tenants with their own polling of other
VCS that will update resources on the Agora cluster(s). However, this
effectively gives external sources root on your particular cluster since flux
needs to have significant permissions to be able to deploy resources within
the cluster. To mitigate this issue the two controllers that execute against
the kubernetes api (kustomize and helm) have their service account replaced
with a service account with no permissions and instead they execute actions
through account impersination. This allows the controllers to actually
execute the changes but we can create dedicated namespaces for customers
and control permissions when the specific account is used for impersonation.
