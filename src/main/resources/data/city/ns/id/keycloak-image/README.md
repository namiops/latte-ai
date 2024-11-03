# Custom Keycloak build script
We add several extensions to original image. This directory includes those extension codes.

## Custom theme
> current `themes` folder is only for keycloak v18 image. For v20 and above please refer to `ns/id/keycloak-theme-woven` folder
Custom theme includes templates for login page, user account page and so on.

# Future considerations
Keycloak operator supports "extensions" configuration that allow us to inject jar files into the Keycloak instance without building image.
https://github.com/keycloak/keycloak-operator/blob/main/deploy/examples/keycloak/keycloak.yaml#L9-L10


# How to test the images
Image repository please refer to this URL
```
https://artifactory-ha.tri-ad.tech/ui/native/docker/wcm-cityos/id/keycloak-24/
```
or checkout 
[service page](/ns/service-page/docs/id/keycloak/README.md#infrastructure)

## With docker 

build the image and load to local docker image by this bazel target
by running this command
```
bazel run /ns/id/keycloak-image:image{major_version}.load
```

### for v18 WildFly
```bash
#!/bin/bash
bazel run //ns/id/keycloak-image:image18.load
docker run --rm -p 8080:8080 -e KEYCLOAK_USER=admin -e KEYCLOAK_PASSWORD=admin ns/id/keycloak-image:image18

```
### for v24 Quarkus 
```bash
#!/bin/bash
bazel run //ns/id/keycloak-image:image24.load
docker run -p 8080:8080 -e KEYCLOAK_ADMIN=admin -e KEYCLOAK_ADMIN_PASSWORD=admin ns/id/keycloak-image:image24 start-dev
```
