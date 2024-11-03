# Welcome

This tutorial exists to help you understand the Personal Data Store (PDS).
Agora provides PDS as a central data store for any and all personal data
that are likely to be accessed by more than just one service. This
tutorial also will minimally explain the Consent service especially the
Data Protection Service (DPS) as it is essential to actually obtaining data
from PDS.

## What This Tutorial Covers

This tutorial provides a general overview of PDS and a small overview of
consent and DPS services as they relate to PDS. It also provides
instructions to run a minimal installation of PDS that can be run on
Kubernetes. Finally this tutorial contains a small sample application that
will fetch some data from PDS for us.

## Pre-requisites For The Tutorial

This tutorial requires the following installed locally

* **Minikube**
  * You can find instructions on how to do this on the [**minikube site**](https://minikube.sigs.k8s.io/docs/start/)
    for Windows, Mac, and Linux systems
  * Minikube requires a backing driver, and this tutorial is using **Docker**.
    You can find instructions [**here**](https://minikube.sigs.k8s.io/docs/drivers/docker/)
* **Kubectl**
  * You can find instructions on how to install
    [**here**](https://minikube.sigs.k8s.io/docs/start/) for Windows, Mac, and Linux

The tutorial also presumes you're working from the **source project root** which for our purposes is:

```shell
/ns/tutorial/pds-101/
```
