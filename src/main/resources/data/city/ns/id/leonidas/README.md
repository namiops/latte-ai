# Leonidas

WIP

A RESTFul API over HTTP for managing  Woven ID Oauth2 Clients

## Development

### Updating OpenAPI Docs

> [!WARNING]
> If you modify the REST API, you need to follow these steps.

1. update the [OpenAPI generator](src/bin/openapi_generator.rs)
2. update the [generated OpenAPI doc](api/v1alpha.yaml) by running:

   ```console
   bazel run //ns/id/leonidas/api:v1alpha.copy
   ```

3. confirm that the generated OpenAPI doc is valid by running:

   ```console
   docker run --rm -it \
      -v $(bazel info workspace)/ns/id/leonidas/api:/spec \
      redocly/cli lint v1alpha.yaml
   ```

4. use [Swagger UI](https://github.com/swagger-api/swagger-ui) to confirm
   the changes are applied as you intended by running the following command and
   checking the result on your browser (http://your-dev-pc-hostname:8080):

   ```console
   docker run --rm -it \
       -p 8080:8080 \
       -e SWAGGER_JSON=/api/v1alpha.yaml \
       -v $(bazel info workspace)/ns/id/leonidas/api:/api \
       swaggerapi/swagger-ui
   ```

5. commit your changes
