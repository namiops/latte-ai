# PDS schema artifact

## How to register a new schema

1. Put a new json file under `json` directory
2. Add the schema info to `pds-schema-artifact-values.yaml`
3. Run the following :zebra: command to generate `z-pds-schema-artifact.yaml`

    ```shell
    bazel run //infrastructure/k8s/common/pds/schemas:pds_schema_artifact.copy
    ```

4.  Run the following :zebra: command to generate `ns/pds/api/z-pds-schema-async-api.yaml`

    ```shell
    bazel run //ns/pds/api:schema_asyncapi.copy
    ```

## How to update the schema

1. Update the new json file under `json` directory
2. Update the schema version in `pds-schema-artifact-values.yaml`
3. Run the :zebra: commands


