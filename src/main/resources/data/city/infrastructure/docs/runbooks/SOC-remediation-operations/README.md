Runbooks for SOC remediation in case of security issue
======================================================

# Setup

## Software
  - kubectl >=1.23
  - flux >= 0.38.2
  - kubelogin >= v0.0.11 <= v0.0.28

You can obtain the required software in our bundle on the [Artifactory](https://artifactory-ha.tri-ad.tech/ui/native/wcm-cityos/tools/k8s-tools/)

## Connectivity
Refer [here](../../../k8s/dev/README.md) for a guide on how to connect to the Agora DEV cluster.

# Runbooks

  - [Block Network in and out](1-block-network-in-and-out.md)
  - [Shut down an application](2-shut-down-application.md)
  - [Drain a node](5-cordon-and-drain-node.md)
