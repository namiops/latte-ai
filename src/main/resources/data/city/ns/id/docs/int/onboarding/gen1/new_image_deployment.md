# New Image

## Deploy Image

This tutorial might vary depending on which service you want to deploy your changes to. For example, you want to update your changes to drako service.

1.  Build the image.
    ```shell
    bazel run ns/id/drako:image.load
    ```
2.  Check your image name and tag by running this docker command. First column shows the image name and second column shows the image tag. Save the digest shown in the third column, this will be needed to clarify that you have successfully loaded your recently-built image to minikube (step 6).
    ```shell
    docker images | grep drako
    ```
3.  In your branch, change the image specified in [infrastructure/k8s/local/id/kustomization.yaml](/infrastructure/k8s/local/id/kustomization.yaml) with the new name and new tag you obtained in step 2.
    ```yaml
    - name: docker.artifactory-ha.tri-ad.tech/wcm-cityos/id/drako
      newName: <image-name> # e.g., ns/id/drako
      newTag: <image-tag> # e.g., image
    ```
4.  Commit and push your changes to the remote branch. It will take some time for flux to apply the latest hash commit in your remote branch to the current deployment configuration. You can check the `REVISION` applied by running `flux get kustomizations`.
5.  Load your image to minikube.
    ```shell
    minikube image load <image-name>:<image-tag>
    ```
6.  Make sure the image that is loaded to minikube is your recently-built image. SSH to your minikube and match the digest of the image loaded (third column shown when running docker command below) with the image built in your EC2 instance (step 2). At this point, drako pod is expected to have `ErrImagePull` because the image is not yet loaded to minikube.
    ```shell
    minikube ssh
    docker images | grep drako
    ```
    If the digests do not match, it means you still have your old image in minikube. SSH to your minikube and run the following command to force delete the old image.
    ```shell
    docker rmi --force <image-name>:<image-tag>
    ```
7.  If the pod of drako service is not restarted after the image is loaded, restart the pod to run the recently-built image.
    ```shell
    kubectl rollout restart deployment drako -n id
    ```
8.  Congratulations 🎉 You have successfully deployed your recently-built image to the drako service.

## Push Image to Artifactory

If you want to push image to artifactory instead, the detailed documentation is available [here](https://github.com/wp-wcm/city/tree/main/tools/k8s-tools#how-to-build-and-push-to-artifactory).

Now, this image can be used in the manifests for deployments.
