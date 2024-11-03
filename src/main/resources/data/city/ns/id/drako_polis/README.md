# Drako Polis

## About

Polis, named after [polis](https://en.wikipedia.org/wiki/Polis) means "city" in
Greek.

Polis is a RESTFul API over HTTP for:

- managing drako groups
- managing users in drako groups

## Local Environment Development

### Updating OpenAPI Docs

> [!WARNING]
> If you modify the REST API, you need to follow these steps.

1. update the [OpenAPI generator](src/bin/openapi_generator.rs)
2. update the [generated OpenAPI doc](api/v1alpha.yaml) by running:

   ```console
   bazel run //ns/id/drako_polis/api:v1alpha.copy
   ```

3. confirm that the generated OpenAPI doc is valid by running:

   ```console
   docker run --rm -it \
      -v $(bazel info workspace)/ns/id/drako_polis/api:/spec \
      redocly/cli lint v1alpha.yaml
   ```

4. use [Swagger UI](https://github.com/swagger-api/swagger-ui) to confirm
   the changes are applied as you intended by running the following command and
   using checking the result on your browser (http://your-dev-pc-hostname:8080):

   ```console
   docker run --rm -it \
       -p 8080:8080 \
       -e SWAGGER_JSON=/api/v1alpha.yaml \
       -v $(bazel info workspace)/ns/id/drako_polis/api:/api \
       swaggerapi/swagger-ui
   ```

5. commit your changes

### Applying Manifests

> :warning: Run `kubectl config get-contexts` and ensure your local enviroment
is currently in use.

Apply the manifests to the local environment:

```console
kubectl apply -k manifests
```

### Building Image

> :note: If you make changes to the source code, you need to re-run these steps
again.

Build the docker image:

```console
bazel run //ns/id/drako_polis:image.tar
```

Load it image into `minikube` (or equivalent):

```console
minikube image load ns/id/drako_polis:image
```

Restart the deployment so the new image is used:

```console
kubectl rollout restart -n id deployment/drako-polis
kubectl rollout status -n id deployment/drako-polis
```

## Releasing

When a change is merged to the `main` branch, the [continuous delivery
workflow][CD workflow] will build the docker image for Polis, tag it, and
push it to the corresponding [Artifactory registry].

The image is tagged as
`docker.artifactory-ha.tri-ad.tech/wcm-cityos/id/drako_polis:main-[commit-id]-[build-time]`
where `commit-id` is the short reference of the commit merged to `main` and
`build-time` is the [Unix time](https://en.wikipedia.org/wiki/Unix_time) when
the workflow was run.

## Troubleshooting

> :warning: Run `kubectl config get-contexts` and ensure the desired cluster is
in use.

On a separate console, create a tunnel from `http://localhost:8080` to the
service running in kubernetes:

```console
kubectl port-forward -n id service/drako-polis-v1 8080:8080
```

Now, you call any API (e.g. create group):

```console
curl -v -X POST -H "Content-Type: application/json" -d '{"name":"test"}' \
    http://localhost:8080/v1/namespaces/test/groups
```

It may also be useful to check the service logs:

```console
kubectl logs -n id service/drako-polis-v1
```

[Artifactory registry]: https://artifactory-ha.tri-ad.tech/ui/native/docker/wcm-cityos/id/drako_polis/
[CD workflow]: https://github.com/wp-wcm/city/actions/workflows/continuous_delivery.yaml
