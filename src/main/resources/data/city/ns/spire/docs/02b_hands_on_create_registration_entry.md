# Hands-on: Create Registration Entry

In this page, we'll create the [registration entry](/docs/default/Component/spire/00_whats_spiffe_spire#registration-entry) for the workload you are going to deploy in the following pages.

Registration entries are created on Spire server. Agora runs [SPIRE Kubernetes Workload Registrar](https://pkg.go.dev/github.com/spiffe/spire/support/k8s/k8s-workload-registrar) and operates on workload registrations based on `Spiffeid` custom resources.

!!! warning

    The SPIRE Kubernetes Workload Registrar is officially deprecated and this way of workload registration is subject to change.

## Procedure


1. In the city repo, edit [ns/spire/docs-k8s/registration/spiffe-id.yaml](https://github.com/wp-wcm/city/tree/main/ns/spire/docs-k8s/registration/spiffe-id.yaml)
    ```sh
    # Replace `<YOUR_AGORA_NAMESPACE>`, `<JOIN_TOKEN>` to the actual value.
    vim <city-repo-dir>/ns/spire/docs-k8s/registration/spiffe-id.yaml
    ```
2. Deploy that file in Agora following your usual workflow. This typically includes placing `spiffe-id.yaml` at your Agora k8s manifest folder and add the entry under `kustomization.yaml`, raise PR for the change and get it merged.
    ```sh
    cp <city-repo-dir>/ns/spire/docs-k8s/registration/spiffe-id.yaml <city-repo-dir>/<your-k8s-manifest-folder>

    # Add `- spiffe-id.yaml` under `resources:`
    vim <city-repo-dir>/<your-k8s-manifest-folder>/kustomization.yaml

    # Commit, raise PR, merge.
    ```
3. Confirm `SpiffeID` is deployed properly by Flux.
    ```sh
    kubectx dev

    kubectl -n <YOUR_AGORA_NAMESPACE> get spiffeid
    ```

    Expected Output.
    ```
    NAME                   AGE
    workload-mtls-sample   3m
    ```
