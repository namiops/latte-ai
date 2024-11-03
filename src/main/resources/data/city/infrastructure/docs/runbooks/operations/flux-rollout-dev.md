# Flux roll-out in DEV

## temp suspend tenants during rollout.
As a temporarily work-around, we agreed that it is acceptable to suspend the
tenants during a platform roll-out.

Please follow these steps:

  - Announce the start of the operation in slack.
  - suspend the tenants:
    ```sh
    for n in $(k get kustomizations.kustomize.toolkit.fluxcd.io -A --output=custom-columns=:.metadata.namespace --no-headers | sort | uniq | grep -v flux-system); do
        flux suspend -n "${n}" --all
    done
    ```
  - resume the tenants:
    ```sh
    for n in $(k get kustomizations.kustomize.toolkit.fluxcd.io -A --output=custom-columns=:.metadata.namespace --no-headers | sort | uniq | grep -v flux-system); do
        flux suspend -n "${n}" --all
    done
    ```
  - Announce the end of the operation in slack.
