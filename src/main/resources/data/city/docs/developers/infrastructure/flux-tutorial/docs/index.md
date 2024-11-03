# Welcome

This tutorial is intended to help developers learn how [Flux](https://fluxcd.io)
is used in Agora. As knowledge of flux is essential to understanding how Agora
uses flux, there will be significant overlap with the official
[flux documentation](https://fluxcd.io/flux/), so it is advised that developers
looking for canonical information refer to the previous link. Beyond the
paraphrasing of flux documentation here, this document will point out anything
that is notably different from standard flux usage compared to how we are using
flux in Agora.

This tutorial was adapted from material from the following [tutorial session](http://go/learnflux)

## Tools used in this tutorial

The sections in this tutorial that cover commands require the following tools
to be installed

* **A working kubernetes cluster**
  * If you are working from the Agora repository the standard
  [bin/bootstrap](https://github.tri-ad.tech/cityos-platform/cityos/tree/main/infrastructure/k8s/local/bin/bootstrap)
  works perfectly well.
  * For those not using the agora bootstrap you can try setting up minikube
  from the
  [minikube site](https://minikube.sigs.k8s.io/docs/start/)
* **Kubectl**
  * Users working from an Agora requested dev box will already have this tool
  installed
  * For those not using an Agora dev machine you can find instructions on how
  to install
  [**here**](https://minikube.sigs.k8s.io/docs/start/) for Windows, Mac, and Linux
* **The flux command line**
  * Users working from an Agora requested dev box will already have this tool
  installed
  * For users not using an Agora dev machine flux's [get started guide](https://fluxcd.io/flux/get-started/)
  includes directions for this.
* **Kustomize**
  * Users working from an Agora requested dev box will already have this tool
  installed
  * For users not using an Agora dev machine Kustomize's [installation guide](https://kubectl.docs.kubernetes.io/installation/)
  has directions
* **K9s**
  * This tool is optional, but allows us to better inspect what the cluster is
  doing during reconcilliation loops.
  * You can install k9s from the following [guide](https://k9scli.io/topics/install/)
