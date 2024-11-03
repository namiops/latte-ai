# Propy

## About
Propy is named after Greek word [propylaea](https://en.wikipedia.org/wiki/Propylaea) that served as the entrance to an ancient citadel.

In the Woven City network, devices not associated with individual users—such as shared terminals, robots, and IoT devices—require authentication. 

Propy(one of the core service in Agora), serves as an entrance to Agora network. It is a LDAP gateway between Woven City network and Agora network that help authenticating such devices.

For more detail on Propy background and design, please refer [go/tn-0479](go/tn-0479).

If you are interested learning about network authentication at Woven City network, the details are available [here](https://go/tn-0352).

## Development in local environment

### Setup environment
While developing you might want to run the code against a real environment. 

* The closest we have to it is the `local` cluster under minikube (gen1). Follow [this guide](../docs/int/onboarding/gen1/local_cluster_deployment.md) for setup.

* Another option is to use Speedway/local cluster currently developed and maintained by Identity team.

  * This is recommended, you will get argocd for deployments here which is what we will use in Speedway dev/prod environments.
  * Follow [this guide](../docs/int/onboarding/speedway/local_cluster_deployment.md) for cluster setup.
  * How to access argocd in local, refer [this guide](../docs/int/onboarding/speedway/local_cluster_access.md).

### Build image

> :note: If you make changes to the source code, you need to re-run these steps again.

#### Build the docker image

```shell
bazel run ns/id/propy:image.load
```

Check created image:

```shell
docker images | grep propy
```
    
### Deploy image

Update image:

In your branch, change the image specified in [infra/k8s/agora-id/speedway/local/3-propy/kustomization.yaml](/infra/k8s/agora-id/speedway/local/3-propy/kustomization.yaml) with the new name and new tag you obtained in step 2.

```yaml
- name: docker.artifactory-ha.tri-ad.tech/wcm-cityos/id/propy
  newName: <image-name> # e.g., ns/id/propy
  newTag: <image-tag> # e.g., image
```

Commit and push your changes to the remote branch.

> :note: It will take some time for [ArgoCD](https://argocd.woven-city.local/) to apply the latest hash commit in your remote branch to the current deployment configuration.
Wait for the synchronization to complete.
At this point, propy pod is expected to have `ErrImagePull` because the image is not yet loaded to kind.

Load your image to kind:

```shell
kind load docker-image <image-name>:<image-tag> --name agora-speedway-local
```

> Note:

If the propy's pod is not restarted after the image is loaded, restart the pod to run the recently-built image.

```shell
kubectl rollout restart deployment propy -n agora-id-local
```

Congratulations 🎉 You have successfully deployed your recently-built image to the propy service.

If you want to push image to artifactory instead, the detailed documentation is available [here](https://github.com/wp-wcm/city/tree/main/tools/k8s-tools#how-to-build-and-push-to-artifactory).

## Usage
In your vm, you now are ready to use propy. This section includes sample requests.

#### Pre-requisite
1. Add propy hostname(` propy.woven-city.local`) to `/etc/hosts` file located in your EC2 instance.

```
<IP address> id.woven-city.local argocd.woven-city.local dev-echo-id-test.woven-city.local propy.woven-city.local
```

2. To test propy in local with servicenow dev, please use own credentials for now.
    > [!WARNING] Please do not commit secret or credentials to github.

    * Update `SERVICENOW_USERNAME` and `SERVICENOW_PASSWORD` in the secret `credential-snow`. 

    * Update `BIND_DN` and `BIND_DN_PASSWORD` in the secret `credential-bind-dn`. 

#### Request
Inside your vm, you can use below sample requests.

1. Search by MAC address

Replace bind dn `-D` and password `-w` values as per the secret stored in `credential-bind-dn`.

```shell
ldapsearch -h propy.woven-city.local -p 1636 \
  -x \
  -D 'placeholder' \
  -w 'placeholder' \
  -s one \
  -b 'ou=devices,dc=iot,dc=woven-city,dc=local' \
  '(&(objectClass=WovenDevice)(cn=f2:1e:8d:0c:3b:4a))'
```

2. Search by CN (Common Name)

```shell
ldapsearch -h propy.woven-city.local -p 1636 \
  -x \
  -D 'placeholder' \
  -w 'placeholder' \
  -s one \
  -b 'ou=devices,dc=iot,dc=woven-city,dc=local' \
  '(&(objectClass=WovenDevice)(cn=robot1.group-a.team-a.iot.woven-city.local))'
```

## Run Tests and Test coverage
#### Run tests:

```shell
bazel run :propy_test
```

while seeing their output and detailed report.

#### Test coverage:

Pre-requisite: You may need to install [lcov](https://github.com/linux-test-project/lcov) first. 

* Run [the coverage report script](./coverage_report.sh).

```shell
./coverage_report.sh
```

* Getting HTML output: Run below command from the root of `city` repository.

```shell
genhtml --output genhtml "$(bazel info output_path)/_coverage/_coverage_report.dat"
```

The auto-generated HTML file `genhtml/index.html` will be generated at the root of `city` repository.

## Appendix
* [Propy Design](go/tn-0479)
* [Network authentication at Woven City network](https://go/tn-0352)
* [Gen1/local cluster deployment](../docs/int/onboarding/gen1/local_cluster_deployment.md)
* [Speedway/local cluster deployment](../docs/int/onboarding/speedway/local_cluster_deployment.md)
* [Argocd local setup](../docs/int/onboarding/speedway/local_cluster_access.md)
* [Push image to artifactory](https://github.com/wp-wcm/city/tree/main/tools/k8s-tools#how-to-build-and-push-to-artifactory).
