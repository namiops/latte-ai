# What is Deployment Preview
Deployment preview is a feature in Agora Platform that allows developers to preview and test changes their service from pull request before merging to main. It provides a way to see how the service will behave in a real-world environment without impacting the live development system.

# How to use Deployment Preview

## Prerequisite
Before you can start using the Deployment Preview feature, your service has to meet the following prerequisites:
- This feature is only for **wp-wcm/city monorepo users**. If you use Agora Platform but use another repository to store your code, you **cannot** use this feature
- Your application is deployed on an Agora Kubernetes cluster and has these following components:
  - Deployment (currently only deployment is supported)
  - Service
  - VirtualService
    - If you use the CityService, you will automatically have a VirtualService
    - See [FAQ](#faq) if your service is not expose to the gateway

### Configure permission
Create a `PreviewUser` custom resource to list github usernames that are allowed to create a preview in the namespace.
This custom resource needs to be present on the `main` branch, so that it's applied to the cluster before you can use Deployment Preview.
In other words, you need to create or update this custom resource for your namespace in a separate PR.

Example:

```yaml
apiVersion: chameleon.woven.toyota/v1alpha1
kind: PreviewUser
metadata:
  name: preview-user
spec:
  github_users:
  - hunter-chen_stargate
  - nico-natalie_stargate
  - spencer-cramm_stargate
```

## Use /preview command on pull request
In the pull request that contains the code changes to preview, post following command format to initiate a Deployment Preview:
```
/preview {namespace} {deployment} {bazel-push-target}
```
Replace the placeholders with the following information:

- namespace: The Kubernetes namespace where you want to deploy your changes. This should match the namespace you configured in the prerequisites.
- deployment: The name of the kubernetes deployment to preview
- bazel-push-target: The Bazel target responsible for pushing your image to artifactory and the one that the deployment use

#### How to find deployment name and bazel push target
In your terminal run the following command:
```bash
kubectl get deployment -n {your_namespace}
```

Using foodagri as an example:
```
kubectl get deployment -n foodagri

NAME                            READY   UP-TO-DATE   AVAILABLE
foodagri-api                    2/2     2            2        
foodagri-frontend               2/2     2            2         
```

To find the `bazel-push-target` that `foodagri-frontend` use:
```bash
kubectl get deployment -o yaml foodagri-frontend -n foodagri | grep "image:"

image: {artifactory-url}/wcm-backend/foodagri-frontend/frontend/yott-app:{tag}
```

Find the `city_original_oci_push` or `city_oci_push` (`container_push` or `agora_container_push` if you haven't migrate to OCI) 
on `BUILD` file located on project directory, that push to `wcm-backend/foodagri-frontend/frontend/yott-app`

```yaml
projects/foodagri-frontend/frontend/yott-app/BUILD # the directory of the build file

city_oci_push(
    name = "push_image", # the name of target might be different per project, which is okay
    repository = "foodagri-frontend/frontend/yott-app",
    ...
)
```

From above information, this is the full command to preview `foodagri-frontend` deployment
```
/preview foodagri foodagri-frontend //projects/foodagri-frontend/frontend/yott-app:push_image
```

If you get a successful reply from the bot, your deployment preview is being created. It will take a while to build your container image and provision the deployment. If you get a non-ok reply, see [Troubleshooting](#troubleshooting).

## Monitoring preview progress
When you submit the preview request, it will do the following things:
- Build the container image and push it to the artifactory with `preview-{short-sha}` as the tag. It will use the latest commit on your pull request as the source.
  
  To check this :eyes: Make sure the job on [Preview Delivery](https://github.com/wp-wcm/city/actions/workflows/preview_delivery.yaml) that is associated with your pull request is successfully building and pushing without any error .

- Create a copy of your k8s deployment with `-preview-{PR number}` suffix.

  To check this :eyes: Make sure the deployment is created and the pod is ready
  ```bash
  kubectl get deployment -n foodagri

  NAME                            READY   UP-TO-DATE   AVAILABLE
  foodagri-frontend-preview-888   1/1     1            1        
  ```
- Create a copy of your k8s service with `-preview-{PR number}` suffix.

  To check this :eyes: Make sure the service is created
  ```bash
  kubectl get service -n foodagri

   NAME                           TYPE       CLUSTER-IP       EXTERNAL-IP   PORT(S)
  foodagri-frontend-preview-888   ClusterIP  172.20.190.70    <none>        80/TCP   
  ```
- Create new virtual service with `preview-` prefix to the existing host
  
  :warning: On the next gen environment, `preview-` prefix is no longer necessary :warning:

  To check this :eyes: Make sure the virtual service is created
  ```bash
  kubectl get virtualservice -n foodagri

  NAME                           GATEWAYS                          HOSTS                   
  # newly created virtual service
  foodagri-frontend-preview-vs   ["city-ingress/ingressgateway"]   ["preview-foodagri.cityos-dev.woven-planet.tech"]
  # existing virtual service
  foodagri-frontend              ["city-ingress/ingressgateway"]   ["foodagri.cityos-dev.woven-planet.tech"]
  ```

## Accessing preview service
:warning: This will change on new next gen environment. It can be accessed without `preview-` prefix and will only need the header :warning:

You can access the preview by accessing the url on the hosts of the newly created virtual service with additional header.

The header will be `X-{deployment-name}-preview-{PR Number}: true`.

By following above example, this is how we can access the `foodagri-frontend` preview
```bash
curl -H "X-foodagri-frontend-preview-6130: true" https://preview-foodagri.cityos-dev.woven-planet.tech/
```

:bulb: If your service is a web app that can be accessed from browser, you can use the [Modheader](https://chrome.google.com/webstore/detail/modheader-modify-http-hea/idgpnmonknjnojddfkpgkljpfnnfcklj) extension on chrome browser.


## Taking down the deployment preview

All deployment previews will be deleted after 12 hours of creation (can be adjusted on a special occasion, like demo day). If you still need your preview, please re-request it.

## Deployment Spec
The preview will be exact replica of the existing deployment, except:
- It will only have 1 replica
- It will have new env variable with `AGORA_DEPLOYMENT_PREVIEW` as the key and `1` as the value  

## Known issues

#### ErrImagePull or ImagePullBackOff error when starting the preview pod

When we process the request to create the preview, it does 2 things in **parallel**
1. Run [Preview Delivery](https://github.com/wp-wcm/city/actions/workflows/preview_delivery.yaml) to build the image and push it to artifactory. It will use a pre-defined tag `preview-{short-sha}` to tag the image
2. Create the copy of the deployment and telling it to use the container image with `preview-{short-sha}` tag, which might not be available at the time, hence the error.

Step 1 will take time (depending on how long your service is built normally). Some services take less than 10s and some service needs more than 5 minutes to build.

Kubernetes will keep re-trying to start the pod, so once the step 1 is finsihed, the error should be gone and the pod should start without problem.

## Troubleshooting

#### Invalid Input
Make sure to follow the correct format for the `/preview` command. See [Use /preview command on pull request](#use-preview-command-on-pull-request) section.

#### Permission error
If the bot reply indicates that you don't have access to create deployment preview, refer to [Configure permission](#configure-permission) section.

#### Error when running the preview delivery pipeline
If the job on [Preview Delivery](https://github.com/wp-wcm/city/actions/workflows/preview_delivery.yaml) is failing:
- The pipeline is will build your code on the pull request, first make sure there is no error in your code
- Make sure you pass the correct input for the `bazel-push-target`. See [How to find deployment name and bazel push target](#how-to-find-deployment-name-and-bazel-push-target) section
- See the details error on the pipeline, it will show you what is the error about

If you still can't figure out the problem, please reach out to @wcm-cicd on #wcm-cicd-support and provide the link to the pull request and the failing pipeline to help us investigate.

### 

## FAQ
- **My service doesn't has virtual service because it's internal only service, can I use this feature?**

  You can still use this feature, but only k8s service and deployment will be created and **not** virtual service. In order to test your service, you have to create a preview for the downstream service, and modify the service url to use the preview url.

- **When I update my pull request, will the deployment preview updated automatically?**
  
  No, you have to re-request the preview.

- **Can I request preview for multiple services in the same pull request?**

  Yes, as long as you have the permission to do it.

- **Can I request preview for same service in different pull request?**
  
  Yes, you can do it.

- **How about the limit? How many preview can I create?**

  Currently we don't limit it, use it as you need it, but also please be considerate (because this is costing money).

- **Can I chain deployment previews?**

  `serviceA` has dependencies to `serviceB`, I want to create preview for `serviceA` that use preview version of `serviceB`, can I do this?

  Yes, but it's not straight forward. Please create preview for `serviceA` and `serviceB`, then you have to modify `serviceA` to add the appropriate header when calling `serviceB`.

  We are still looking for a way to make this process easier.

## Feedback, bug report and feature request
We love to hear your feedback about your experience when using this feature, we will keep improving and add new features based on your feedback, so don't hold back! 

Please reach out to @agora-build on #wcm-cicd-support channel.
