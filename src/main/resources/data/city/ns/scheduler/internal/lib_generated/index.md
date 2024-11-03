# Generate code from Open API definition

Once the Open API definition is updated, we'll need to regenerate the code referenced in other parts of the monorepo (this will be ideally done via CI, but it might be necessary to do it manually for development purposes).

From the `ns/scheduler` folder:

=== "Server"

    ```shell
    oapi-codegen --config ./generator-configs/server.yaml api/api.yaml > ./internal/lib_generated/server.stub.go
    ```

=== "Client"

    ```shell
    oapi-codegen --config ./generator-configs/client.yaml api/api.yaml > ./internal/lib_generated/client.stub.go
    ```

=== "Types"

    ```shell
    oapi-codegen --config ./generator-configs/types.yaml api/api.yaml > internal/lib_generated/types.go
    ```


### Wrapping the client

In order to make the scheduler client available outside the monorepo, a separate module has been extracted. If the code using the client stub lives in the monorepo, it's possible to use directly the [stub code](https://github.com/wp-wcm/city/blob/main/ns/scheduler/internal/lib_generated/client.stub.go). An example of wrapper can be found [here](https://github.tri-ad.tech/cityos-platform/scheduler/blob/main/scheduler.go)