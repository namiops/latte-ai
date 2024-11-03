# Developer Portal in Local Cluster

This README describes how to run Developer Portal in your local cluster.

If you are working on just the Backstage portion of Developer Portal, it is recommended to follow [this README](https://github.com/wp-wcm/city/blob/main/ns/developer/backstage/README.md) to them as Node processes, because it provides a more swift dev environment (e.g. hot reloading).
If you are working on some features that require interactions between containers (e.g. openapi-generator, kroki-server etc), you are at the right place.

## Getting Developer Portal Running in Local Cluster

!!! Warning
    This documentation is meant for Agora developers only.

1. Go through [infrastructure/k8s/local/README.md](https://github.com/wp-wcm/city/blob/main/infrastructure/k8s/local/README.md) to get your local cluster running with Flux.
2. Change to the branch that you chose in the above step, and move to the working directory.

    ```sh
    git checkout <YOUR_BRANCH>
    cd <THE_DIRECTORY_THIS_README_IS_PLACED>
    ```

3. Prepare Git credentials under `./secrets` folder.
    1. Follow `Prepare Git credentials under ./secrets folder.` step in [this instruction](https://github.com/wp-wcm/city/blob/main/ns/developer/backstage/README.md#runing-backstage-locally). `./secrets` folder there means `infrastructure/k8s/local/developer-portal/secrets`.
4. Run the following commands to create the k8s secrets.

    ```sh
    # cd <THE_DIRECTORY_THIS_README_IS_PLACED>
    kubectl create ns developer-portal && kubectl create secret generic github-secrets -n developer-portal --from-file=./secrets/secret-github-credentials.yaml --from-file=./secrets/secret-github-oauth-credentials.yaml
    ```

5. Uncomment `- developer-portal.yaml` line from [kustomization.yaml](../flux-system/kustomizations/services/kustomization.yaml), git commit the change and push to remote.
6. `watch flux get kustomization` to watch your deployment to complete.
7. Once it's ready, open the port-forward to `developer-portal`

    ```sh
    kubectl -n developer-portal port-forward svc/developer-portal 3000:7007 7007:7007
    ```

8. Go to `http://localhost:3000` to open the dev-portal.

## Updating Your Image

The above steps will grab the Backstage image (that exists) in the remote registry and deploy it to your local cluster. Because you develop the dev-portal, you should want to update the image in the local cluster as you change the code locally.

1. Switch to the minikube docker env

    ```sh
    eval $(minikube docker-env)
    ```

2. Build your Docker image

    ```sh
    # cd ns/developer/backstage/app/
    yarn install
    yarn tsc && yarn build && DOCKER_BUILDKIT=1 docker build . -f packages/backend/Dockerfile -t my-backstage
    ```

3. Do one of these
    - If you're trying to run with your own image for the first time
        1. Update the image specifier in [kustomization.yaml](./kustomization.yaml)
            - `newName` => `my-backstage`
            - `newTag` => `latest`
        2. git commit the change and push to remote.
    - If you've updated the image specifier in [kustomization.yaml](./kustomization.yaml) already
        1. Restart your Pod to use the new image

            ```sh
            kubectl rollout restart deploy developer-portal
            ```
