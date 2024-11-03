# Hands-on: Establishing mTLS between External and Internal Workloads

## What we do

In this hands-on tutorial, you will establish mTLS sessions between workloads outside and inside of Agora. This requires several steps.

1. (Already done by Agora) Server-side setup
    1. Run the Spire server for the trust domain
    2. Run the sample backend in Agora
2. Agent setup
    1. Run the Spire agent and get it attested by the Spire server
3. External workload setup
    1. Create a registration entry for your workload
    2. Run the external workload
4. Do mTLS!

For 2~3, we will do it in 2 different ways on the following pages.

Here’s the high-level diagram of what the final status will look like.

![hands-on-overview](./assets/hands-on-overview.png)

(Source: https://docs.google.com/drawings/d/1NJ9jxBv91O6VNvEHtpRk6bIkCNJ5NQoWh2K14dC14oQ/edit)

## Prerequisite

This tutorial assumes the following.

- You have an introductory-level understanding of Kubernetes.
- You have read/write access to the [city repo](https://github.com/wp-wcm/city), and checked out to your PC.
- You have set up [kubectl to interact with dev cluster](https://github.com/wp-wcm/city/blob/main/infrastructure/k8s/dev/README.md).
- You have these software installed on your PC.
    - [Minikube](https://minikube.sigs.k8s.io/docs/start/)
    - [kubectx](https://github.com/ahmetb/kubectx) for context switching

## First Step: Running Minikube

Let's start a Kubernetes cluster in your PC as the foundation of _External Workload_.

```sh
minikube start --container-runtime=docker
```

!!! note

    While the other Kubernetes distribution may work, the author failed to run spire-agent in other tools like Rancher Desktop, colima or microk8s on M1 Mac Laptop. It is mostly due to how it examines the host PID and network as part of its functionality.
