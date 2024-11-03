# What is purpose of this config 

Configurations for KNative features

# Changelog

## [0.0.3]

- `kubernetes.podspec-securitycontext`: enabled for explicitly specifying runAsNonRoot and not allow priviledgeEscalation recommended by semgrep CI job.

## [0.0.2]

- `kubernetes.podspec-affinity`: enabled for scheduling KServe's InferenceService on GPU nodes
- `kubernetes.podspec-nodeselector`: enabled for scheduling KServe's InferenceService on GPU nodes
- `kubernetes.podspec-tolerations`: enabled for scheduling KServe's InferenceService on GPU nodes

## [0.0.1]

- A default config for a local cluster
