# Source Controller

The source controller is responsible for interacting with a variety of version
control systems to get configuration that flux will process. The controller is
fed change requests by polling the configured source on regular intervals and
then executes a reconcilliation loop to update the cluster based on the
configuration.

The loop executes as follows: read data, render kustomization.yaml, checking
resources on the server to find differences, correcting the differences.

!!! Warning
    Any manual changes (e.g. `kubectl apply`) done to the cluster will be repaired
    by flux.

You can use the flux command line tool to view sources on the cluster. This will output the repository and the revision flux has last processed for that repository, along with some status information. Secondly it will list more detail about the helm repositories and the helm charts that it has pulled.

```shell
$ flux get sources all
NAME                 REVISION     SUSPENDED READY MESSAGE
gitrepository/cityos main/14a9507 False     True  stored artifact for revision 'main/14a9507a943b5d5bbe7271d02edad56b9a199558'

NAME                                REVISION                                                         SUSPENDED READY MESSAGE
helmrepository/grafana              787c35497d7d0c32f064e0f3a568fa6e06e15c00286d2e1076ac5135c6e99341 False     True  stored artifact for revision '787c35497d7d0c32f064e0f3a568fa6e06e15c00286d2e1076ac5135c6e99341'
helmrepository/kiali                bad26ea7c24b2d3b14f999bf8a026836f90777b6d198b8da167968cd47c0597a False     True  stored artifact for revision 'bad26ea7c24b2d3b14f999bf8a026836f90777b6d198b8da167968cd47c0597a'
helmrepository/prometheus-community 80883cd36ee3504196250a456954849ee838a46ea5624371139aa1f5455fdb60 False     True  stored artifact for revision '80883cd36ee3504196250a456954849ee838a46ea5624371139aa1f5455fdb60'
helmrepository/wit-artifactory      c208f94b1570865bf3b1eb252f1b65fa6696464621f616044b2ddc896741a47d False     True  stored artifact for revision 'c208f94b1570865bf3b1eb252f1b65fa6696464621f616044b2ddc896741a47d'

NAME                                 REVISION       SUSPENDED READY MESSAGE
helmchart/flux-system-grafana        6.20.5         False     True  pulled 'grafana' chart with version '6.20.5'
helmchart/flux-system-id-secure-kvs  0.5.1+a81c9166 False     True  pulled 'secure-kvs' chart with version '0.5.1+a81c9166'
helmchart/flux-system-kiali          1.52.0         False     True  pulled 'kiali-server' chart with version '1.52.0'
helmchart/flux-system-pds-secure-kvs 0.3.8+90be6e82 False     True  pulled 'secure-kvs' chart with version '0.3.8+90be6e82'
helmchart/flux-system-prometheus     13.6.0         False     True  pulled 'prometheus' chart with version '13.6.0'
```

However, flux uses kubernetes namespaces and by default will only check things in the flux-namespace system. To get sources in all namespaces we can append the -A flag
