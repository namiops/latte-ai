# cityservice-operator


This folder contains the latest release of the cityservice-operator k8s API. It
does not contain the custom image versions we currently use.

Therefore, when importing this in your kustomize file remember to map the image.

For example:

```yaml
images:
- name: cityservice-operator-placeholder
  newName: docker.artifactory-ha.tri-ad.tech/wcm-cityos/city-operator/cityservice-operator
  newTag: "main-5388df22-4113"
```
