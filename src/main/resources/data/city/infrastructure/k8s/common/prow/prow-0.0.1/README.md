# Prow Starter S3 0.0.1

## Initial import

```
./bin/import
```

All upstream files used for this revision were pulled from revision [fa035fdc146ad7f8905dbe7b509143b4a7e9c366](https://github.com/kubernetes/test-infra/commit/fa035fdc146ad7f8905dbe7b509143b4a7e9c366) 

The starter-s3.yaml has been split into individual files using [kubectl-slice](https://github.com/patrickdappollonio/kubectl-slice/releases/tag/v1.2.3).
These files have been reorganized by individual components by the [import](bin/import) script.

Additional modifications to each component have been made and documented below, all of these modifications have been made to the CI cluster root either by patching or shipping additional objects with kustomize

## generic issues

- [x] make sure we can use GHE. [start here](https://github.com/kubernetes/test-infra/tree/master/prow/git/v2). [read this](https://github.com/kubernetes/test-infra/blob/master/prow/getting_started_deploy.md#deploying-with-github-enterprise)
- [] fix bug in image overlay generation code
- [x] disable image overlay generation in import
- [x] move image overlays to ci cluster_root

## crier

- [x] fix github endpoints
  - [x] github-endpoint
  - [x] github-graphql-endpoint

## deck

- [x] fix github endpoints
  - [x] github-host
  - [x] github-endpoint
  - [x] github-graphql-endpoint

## ghproxy

- [x] fix github endpoints
  - [x] set `--upstream`
- [x] fix address to pushgateway.observability.svc.cluster.local
- [x] patch PVC to use a dedicated storageclass

## hook

- [x] fix github endpoints
  - [x] github-host
  - [x] github-endpoint
  - [x] github-graphql-endpoint

## horologium

## minio

- [x] patch minio deployment to use a secret for root user name and pw
- [x] patch PVC to use a dedicated storage class
- [x] patch image to use a tag other than `latest`
- [x] patch alpine based init container to not use `latest`
- [] investigate whether MINIO_ROOT_USER is deprecated, seen some hints that it is
- [] replace this with real S3 buckets at some point

## prow

- [x] fix github endpoints
  - [x] set `github.link_url` in prod config configmap
  - [x] add github.tri-ad.tech SSH pubkeys to `plank.default_decoration_config_entries[].ssh_host_fingerprints`
- [x] disabled ingress-prow.yaml from kustomize in the import script
- [x] disable the default configmaps from starter and replace them with a configMapGenerator in the `ci` cluster_root
- [x] add istio virtualservice to expose deck and hook on city-ingress in ci cluster_root

## prow-controller-manager

- [x] fix github endpoints
  - [x] github-host
  - [x] github-endpoint
  - [x] github-graphql-endpoint

The prowjob CRD CANNOT be shipped with kubectl even for manual testing as it is close to the object limit size for etcd, which is not tuneable in EKS (according to wojtek)
Flux works. Use Flux.

## sinker

## statusreconciler

- [x] fix github endpoints
  - [x] github-endpoint
  - [x] github-graphql-endpoint

## tide

- [x] fix github endpoints
  - [x] github-host
  - [x] github-endpoint
  - [x] github-graphql-endpoint
- [X]  prow config needs to be tweaked with org specific stuff for tide

## secrets generation

Follow this (guide)[https://github.com/kubernetes/test-infra/blob/master/prow/getting_started_deploy.md#create-the-github-secrets]

* [] HMAC token for cookies [doc]()
* [] github-token
* [] s3-credentials-minio
* [] s3-credentials
* [] git oauth app for deck
