# Generate code from Open API definition

From the `ns/cvm` folder:

=== "Server"

    ```shell
    oapi-codegen --config ./generator-configs/server.yaml api/cvm.yaml > ./internal/lib_generated/server.stub.go
    ```

=== "Types"

    ```shell
    oapi-codegen --config ./generator-configs/types.yaml api/cvm.yaml > internal/lib_generated/types.go
    ```
