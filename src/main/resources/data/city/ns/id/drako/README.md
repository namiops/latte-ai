# Drako


## About

Drako, named after the [Athens' legislator](https://en.wikipedia.org/wiki/Draco_(lawgiver)), is
Agora's Policy Decision Point system.

At this stage (December of 2022) this is only a POC code to demonstrate how we 
can implement this using rust.

## Developing using local

While developing you might want to run the code against a real environment. The
closest we have to it is the `local` cluster under minikube. 

### Loading image into minikube

To build the image and load it into your docker daemon, run:

```shell
bazel run ns/id/drako:image.tar
```

After that, you can load it into minikube by running:

```shell
minikube image load ns/id/drako:image
```
After your local image is up to date, you can load the manifests or reload
the pod.

### Load manifests

Make sure that flux is suspended
```shell
flux suspend kustomization id
```

There are 2 groups of manifests you neet to load - CRDs and then the actual 
example manifests. CRDs must be loaded before or else it will fail.

With your minikube running and in the current kubectl contexts, run:

```shell
bazel run //ns/id/drako_data:drako_data_crd_gen | kubectl -n id apply -f -
kubectl apply -k manifests/test
```

This will create the `drako-test` namespace, where httpbin will be setup with
drako support, and also deploys the local drako image we loaded into minikube
inside the id namespace.

### Test it

There are two things that might interest you

1. the log output of `kubectl -n id logs svc/drako` and
1. accessing https://drako-test.woven-city.local through your browser and running
some tests.


Don't forge to add the `drako-test.woven-city.local` and `id.woven-city.local` to your 
localhosts file first or drako won't launch.


### Update after changes

Whenever you make changes you want to test, you need to do 2 things:


1. Load the new image into minikube (see [details](#loading-image-into-minikube))
2. Restart the deployment, so the new image gets used:
   ```shell
   kubectl -n id rollout restart deployment drako
   ```

## Building and Testing (as in unit-tests)

This can only be built using bazel. Building via cargo is not supported.

To build use `bazel build :drako`. And use `bazel run :drako_test` to run tests
while seeing their output and detailed report.

### Coverage report

You may need to install [lcov](https://github.com/linux-test-project/lcov) first. Run [the coverage report script](./coverage_report.sh) from this folder to get the coverage report using this command.

```shell
./coverage_report.sh
```

You can also get the details of which lines and functions that have been covered by existing unit test in each file by running this command from the root of `city` repository.

```shell
genhtml --output genhtml "$(bazel info output_path)/_coverage/_coverage_report.dat"
```

Access the auto-generated `genhtml/index.html` available at the root of `city` repository file to see the UI of the coverage report.

## Architecture

> **NOTE:** none of this is implemented yet.

drako acts as an external authorizer (istio configuration, leveraging envoy
filters) and proxy the requests to other authorizers.

It loads its configuration directly from custom resource definitions of CityService.

caching is accomplished by embedding a sha384 hash of the granted authorization
to the jwt token.

authorization requests are represented in json; for that, drako supports PAR and
RAR.


## Releasing

To release Drako, you can do the following:

1. version bump the service binary ([more](#version-bump)).
2. publish the docker image containing the service binary ([more](#publishing-docker-image)).
3. publish Kubernetes manifests ([more](#publishing-kubernetes-manifests)).
4. deploy service to environment ([more](#deploying-environment)).

### Version Bump

To bump up Drako, you can to do the following:

1. Create a new branch from `main`.
2. Update the `version` field for `[package]` in [Cargo.toml](Cargo.toml).
3. Update the `version` field for `rust_binary(name="drako")` in [BUILD](BUILD).
4. Update [Cargo.lock](/Cargo.lock) and [cargo.bazel.lock](/cargo.bazel.lock):

    ```shell
    CARGO_BAZEL_REPIN=true bazel sync --only=crate_index
    ```

5. Send a PR with your changes to `main` ([example][bump-pr]).
6. Get it approved and merged.

### Publishing Docker Image

This is automated: whenever a change to Drako is merged to the `main` branch,
the [continuous delivery workflow][CD workflow] will build the docker image for
it, tag it, and push it to the corresponding [Artifactory registry].

The image is tagged as
`docker.artifactory-ha.tri-ad.tech/wcm-cityos/id/drako:main-[commit-id]-[build-time]`
where `commit-id` is the short reference of the commit merged to `main` and
`build-time` is the [Unix time](https://en.wikipedia.org/wiki/Unix_time) when
the workflow was run.

To look for the new docker image tag, you can do the following:

1. Visit the [version bump](#version-bump) PR page.
2. At the bottom of the page, click the `[commit-id]` URL in the message:
   `Merged via the queue into main with commit [commit-id]`.
3. Copy the first 8 characters of the URL (e.g.
   `https://github.com/wp-wcm/city/commit/12345678...`) as the `[commit-id]`.
4. Access the [Artifactory registry] page which contains the list of all docker
   images.
5. Look for the image tag starting with `main-[commit-id]-`.
6. Please take note of the full image tag which should follow the format
   `main-[commit-id]-[build-time]`. This will be useful later.

### Publishing Kubernetes Manifests

In order to run Drako on Agora's kubernetes clusters, you need to create
manifests for the pod, the service, the deployment, the CRDs and so on.

To publish new manifests, you can use an existing script that does most of the
job:

1. Open a console in the city repository directory.
2. Create a new branch from `main`.
3. Run the following command to generate the new manifests directory:
   ```shell
   bazel run //infrastructure/k8s/common/id/bazel/gen-drako-manifests:cmd -- \
       --app-version=[new-drako-version] \
       --manifests-version=[new-manifest-version] \
       --image-tag=main-[commit-id]-[build-time]
   ```
> **Warning**
> You need to replace `new-drako-version` and `new-manifest-version`.
> You also need to replace `[commit-id]` and `[build-time]` with the values you got
> from Artifactory ([more](#publishing-docker-image)).
4. Run the following command to generate BUILD files:
   ```shell
   bazel run //:gazelle
   ```
5. Send a PR with your changes to `main` ([example][manifests-pr]).
6. Get it approved and merged.

### Deploying Environment

In order to deploy to an environment, you need to do the following:

1. Go to the infrastructure directory `infrastructure/k8s/[environment]/id`
   (e.g. [infrastructure/k8s/local/id](/infrastructure/k8s/local/id) for
   the Identity infrastructure in the `local` environment).
2. Edit the `kustomization.yaml` file as follows:
  - In `resources`, update the version of the manifests:

    ```yaml
    - ../../common/id/drako-[new-drako-version]-manifest-[new-manifest-version]`
    ```

    > **Warning**
    > You need to replace `[new-drako-version]` and `[new-manifest-version]` with the values you
    > used when you published the manifests ([more](#publishing-kubernetes-manifests)).

  - In `images`, update the docker image tag for `drako`:

    ```yaml
    - name: docker.artifactory-ha.tri-ad.tech/wcm-cityos/id/drako
      newTag: main-[commit-id]-[build-time] # {"$imagepolicy": "flux-system:drako:tag"}
    ```

    > **Warning**
    > You need to replace `[commit-id]` and `[build-time]` with the values you got
    > from Artifactory ([more](#publishing-docker-image)).

3. Send a PR with your changes to `main` ([example][env-pr]).
4. Get it approved but DO NOT MERGE yet.
5. Follow the [deployment schedule guidelines](http://go/tn-0293).
6. Merge the PR to initiate the deployment.

[Artifactory registry]: https://artifactory-ha.tri-ad.tech/ui/native/docker/wcm-cityos/id/drako/
[CD workflow]: https://github.com/wp-wcm/city/actions/workflows/continuous_delivery.yaml
[bump-pr]: https://github.com/wp-wcm/city/pull/9614
[krew]: https://krew.sigs.k8s.io
[kubectl-slice]: https://github.com/patrickdappollonio/kubectl-slice
[manifests-pr]: https://github.com/wp-wcm/city/pull/9622
[env-pr]: https://github.com/wp-wcm/city/pull/9651
