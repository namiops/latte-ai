# CityService Operator

## Description
It is used by service developers to set up an environment to run services on CityOS. 
At that time, they specify which features provided by CityOS they want to use, and how they want to use the features.

- Traffic settings (Ingress/Egress)
- Security settings (AuthorizationPolicy)
- Keycloak client settings
- External Authorizer settings per endpoint

## Referenses
- [TN-0047 CityService Operator](https://docs.google.com/document/d/1OBsOpsRAs5hxhOM2z7hjPVsB5TVNprYPght6iC7v3Zg/edit#)

## Supported Custom Resources(CR)
| *CustomResourceDefinition*                                            | *Description*                                            |
| --------------------------------------------------------------------- | -------------------------------------------------------- |
| [CityService](./config/crd/bases/woven-city.global_cityservices.yaml)             | Configure Namespace and base settings on the cluster |

### CityService Properties
|Path|Mandatory|Type|Default|Description|
|----|----|----|----|----|
|idProviderAccess|N|boolean|true|create a keycloak client and get the client id and secret|
|idP|N|object[]|-||
|idP.usePpid|N|boolean|true|Enable/Disable PPID feature|
|idp.Roles|N|string[]|-|(experimental)custom roles|
|host|N|string|-|(experimental)|
|agoraVersions|N|object[]|-||
|agoraVersions.auth|N|string|-|external authorizer provider name|
|paths|Y|object[]|-|Prefer longest match when duplicate Paths are set|
|paths[path]|Y|string|-||
|paths[path].pathType|N|enum|Prefix|(Exact\|Prefix)|
|paths[path].service|Y|string|-|k8s service name|
|paths[path].auth|N|boolean|true|requests are authorized|
|paths[path].principals|N|string[]|-|allow ingress traffic from these principals. format is cluster.local/ns/<namespace>/sa/<serviceaccount>|
|paths[path].endpointConfig|N|string|default|reference to endpointConfig name|
|endpointConfig|N|object[]|-|external-authorizer configuration. If not specified, use default settings.|
|endpointConfig[name].extends|N|string|default|inherit from this config|
|endpointConfig[name].method|N|string|-|HTTP Method values,If not specified, all methods are targeted|
|endpointConfig[name].redirectUrlForAccessDenied|N|string|-|redirectUrl for access denied|
|endpointConfig[name].accessToken.addAccessTokenToHeader|N|boolean|-|add accessToken to responseHeader or not|
|endpointConfig[name].accessToken.headerName|N|string|x-auth-request-access-token|if addAccessTokenToHeader is true, you can specify headerName|
|endpointConfig[name].optionalScopes|N|string[]|-|add optionalScopes to authentication request|
|endpointConfig[name].optionalHeaders.x-user-id|N|boolean|true|add authenticated user-id as “x-user-id” to response header|
|endpointConfig[name].optionalHeaders.x-user-name|N|boolean|true|add authenticated username as “x-user-name” to response header|
|endpointConfig[name].optionalHeaders.x-email|N|boolean|true|add authenticated email as “x-email” to response header|
|traffic.egressHosts|N|object[]|-|Allow egress traffic to these hosts. Currently dest port 443 only is supported|
|traffic.egressHosts[].host|N|string|-|fqdn. (ex. www.woven-planet.global)|

### CityService Status

|Keyname|Description|
|----|----|
|Status.Conditions[Type="Succeeded"]|Show Status of Reason="Reconciling"|
|Status.Conditions[Type="Degraded"]|Show Status of Reason="Finalinzing"|
|Status.Invenotry.Entries[]|List of objects generated from this CityService CR (*1)|
|Status.Inventory.Entries[].ID|object name: `<namespace>_<name>_<group>_<kind>`|
|Status.Inventory.Entries[].Status|the above object's status|

- *1: Referenced the usage of flux2's Status.Inventory

### Note
- Before CRD v1alpha3, they were supported with CityService Operator(previous version, created with operator-sdk ansible)
- After CRD v1alpha3, they are supported with this CityService Operator(created with kubebuilder)

- AdditionalKeycloakClients(not yet)

## For Developer
### develop in local pc

1. Run local kubernetes using bootstarp
    edit infrastructure/k8s/local/cityos-system to change the name of deployment to the same as that made by Tilt
    ```
    -   - ../../common/cityos-system/cityservice-operator-0.2.0
    +   #- ../../common/cityos-system/cityservice-operator-0.2.0
    -   #- ../../common/cityos-system/cityservice-operator-0.3.0
    +   - ../../common/cityos-system/cityservice-operator-0.3.0
    ```
    Do not forget `git push` for Flux to notice that change.
    make the local cluster by bootstrap
    ```
    ../../../infrastructure/k8s/local/bin/bootstrap
    ```
1. Tilt up
    ```
    tilt up --host=xxx.xxx.xxx.xxx
    ```
1. Browse Tilt dashbaord
    ```
    http://xxx.xxx.xxx.xxx:10350/r/(all)/overview
    ```
1. Coding
1. Stop local kubernetes
    ```
    make down
    ```

### build and deploy in local cluster managed by flux

1. set environment to use minikube's docker
    ```
    eval $(minikube docker-env)
    ```
1. build with bazel and import
    ```
    bazelisk build ns/cityos-system/cityservice-operator-go:image
    docker import bazel-out/k8-fastbuild-XXXXXXXX/bin/ns/cityos-system/cityservice-operator-go/image-layer.tar controller:latest
    ```
1. suspend flux
    ```
    flux suspend kustomization cityos-system
    ```
1. change image in Deployment
    ```
    kubectl edit deployment -n cityos-system cityservice-operator-go-controller
    ...
    -     image: docker.artifactory-ha.tri-ad.tech/wcm-cityos/city-operator/....
    +     image: controller:latest
    ...
    ```
1. restore
    ```
    flux resume kustomization cityos-system
    ```

### create CRD

1. update to avoid error (because `controller-gen` does not support `map[string]interface{}` type)
    ```
    $ vi api/v1xxxx/cityservce_types.go
    -       EndpointConfig map[string]interface{} `json:"endpointConfig,omitempty"`
    -       // EndpointConfig map[string]CityServiceEndpointConfigSpec `json:"endpointConfig,omitempty"`
    +       // EndpointConfig map[string]interface{} `json:"endpointConfig,omitempty"`
    +       EndpointConfig map[string]CityServiceEndpointConfigSpec `json:"endpointConfig,omitempty"`
    ```
1. generate CRD
    ```
    make crd
    ```
1. get a generated file in ./config/crd/bases/woven-city.global_cityservices.yaml

### run integration tests

1. install envtest with Envtest Binaries Manager
    Install envtest binaries manager, which installs envtest in your machine
    ```
    go install sigs.k8s.io/controller-runtime/tools/setup-envtest@latest
    ```
    Install envtest binarie.
    You should the version in line with dev cluster(1.21 as of 2022/12).
    But you need to use 1.24 or higher if you use arm64 cpu.
    ```
    setup-envtest use -p path 1.21.x!
    ```
    Switch to the most recent 1.21 envtest on disk

    ```
    source <(setup-envtest use -i -p env 1.21.x)
    ```
    If you can not run `setup-envtest` command, please check if `$PATH` includes `$GOROOT/bin".
1. run integration tests on /controller directory
    ```
    cd controller
    export CITY_DOMAIN="cityos-dev.woven-planet.tech"
    go test  # run all tests under controller directory

    go test -run TestAPIs  # run only the integration test in "suite_test.go"
    ```
1. (if you want to) debug the test when the test failed
    ```
    DEBUG=true go test -run TestAPIs
    ```
    When you run the test with DEBUG environment variables, the test runner does not stop
    the "envtest" after the test finished so that you can investigate the status of kubeapi server.
    You will get the message like the follwoing after the test finished.
    ```
          You can use the following command to investigate the failure:

          kubectl --kubeconfig=/var/folders/xj/pwxfc5mx1tx6fbcylv81zz4r0000gq/T/k8s_test_framework_1272441080/1097588592.kubecfg

          When you have finished investigation, clean up with the following commands:

          pkill kube-apiserver
          pkill etcd
          rm -rf /var/folders/xj/pwxfc5mx1tx6fbcylv81zz4r0000gq/T/k8s_test_framework_1272441080
    ```
    You can get the resource from kube api server with kubectl command like this.
    ```
    $ kubectl --kubeconfig=/var/folders/xj/pwxfc5mx1tx6fbcylv81zz4r0000gq/T/k8s_test_framework_1272441080/1097588592.kubecfg get ns
    NAME              STATUS   AGE
    default           Active   92s
    id                Active   91s
    kube-node-lease   Active   93s
    kube-public       Active   93s
    kube-system       Active   93s
    ns1               Active   91s

    ```
    After you finished the investifation, please ensure to crean up the environment with the shown commands.
    ```
    $ pkill kube-apiserver
    $ pkill etcd
    $ rm -rf /var/folders/xj/pwxfc5mx1tx6fbcylv81zz4r0000gq/T/k8s_test_framework_1272441080
    ```
