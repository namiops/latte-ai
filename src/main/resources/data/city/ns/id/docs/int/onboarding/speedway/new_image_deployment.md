# New Image

## Deploy Image

This tutorial might vary depending on which service you want to deploy your changes to. For example, you want to update your changes to drako service.

1.  Build the image.
    ```shell
    bazel run ns/id/drako:image.load
    ```
2.  Check your image name and tag by running this docker command. First column shows the image name and second column shows the image tag.
    ```shell
    docker images | grep drako
    ```
3.  In your branch, change the image specified in [infra/k8s/agora-id/speedway/local/3-drako/kustomization.yaml](/infra/k8s/agora-id/speedway/local/3-drako/kustomization.yaml) with the new name and new tag you obtained in step 2.
    ```yaml
    - name: docker.artifactory-ha.tri-ad.tech/wcm-cityos/id/drako
      newName: <image-name> # e.g., ns/id/drako
      newTag: <image-tag> # e.g., image
    ```
4.  Commit and push your changes to the remote branch.
    It will take some time for [ArgoCD](https://argocd.woven-city.local/) to apply the latest hash commit in your remote branch to the current deployment configuration.
    Wait for the synchronization to complete.
    At this point, drako pod is expected to have `ErrImagePull` because the image is not yet loaded to kind.
5.  Load your image to kind.
    ```shell
    kind load docker-image <image-name>:<image-tag> --name agora-speedway-local
    ```
6.  If the pod of drako service is not restarted after the image is loaded, restart the pod to run the recently-built image.
    ```shell
    kubectl rollout restart deployment drako -n agora-id-local
    ```
7.  Congratulations 🎉 You have successfully deployed your recently-built image to the drako service.

## Push Image to Artifactory

If you want to push image to artifactory instead, the detailed documentation is available [here](https://github.com/wp-wcm/city/tree/main/tools/k8s-tools#how-to-build-and-push-to-artifactory).

Now, this image can be used in the manifests for deployments.
