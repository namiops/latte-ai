# Overview
Integration test for OTA. It call the OTA endpoints to test the integration of OTA functionality.

## Development
### Setup
* Install ginkgo and gomega
  * https://onsi.github.io/ginkgo/#installing-ginkgo
* Create `tests_suite_test.go` (Already done)
```sh
cd <workspace>/ns/iot/iota-ota/integration
ginkgo bootstrap
```

### Create a new test spec
* run 
```sh
ginkgo generate <spec name>
```

### Running a test
* Locally, run ginkgo. 

```sh
export OTA_ENDPOINT=http://localhost:8080
ginkgo
```

* Run local ginkgo against lab endpoint
```sh
kubectx lab|dev
```

```sh
kubectl port-forward -n knative-serving svc/activator-service 8888:80
export OTA_REVISION_FOR_LOCAL_DEV=$(kubectl get revisions -n iot -l serving.knative.dev/service=iota-ota --sort-by=.metadata.creationTimestamp -o jsonpath='{.items[-1:].metadata.name}')
export OTA_ENDPOINT=http://localhost:8888
ginkgo
```

* Run specific test by using --focus option e.g.
```sh
ginkgo --focus="Upload/Download with presigned URL"
```

* Run in testkube (CICD)
Tests are running on schedule, see 

Lab - https://testkube.agora-lab.woven-planet.tech/tests/iota-ota-e2e
Dev - https://testkube.cityos-dev.woven-planet.tech/tests/iota-ota-e2e
