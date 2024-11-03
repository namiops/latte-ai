# Person detector with Vault secret injection

The ServingRuntime for this InferenceService is set to `vai-torchserve-s3-vault`.
Please deploy the ServingRuntime from [../serving-runtime/vai-torchserve-s3-vault-0.1.0](../serving-runtime/vai-torchserve-s3-vault-0.1.0) first or update it with another available runtime.

## Service account and Cluster Role Bindings

These SA and ClusterRoleBinding are just example. 
However, you cannot deploy ClusterRoleBinding object since it is a cluster-scoped resource.
Please contact the Agora team for creating them for your service.
