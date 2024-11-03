# :warning: This is Kubernetes System Namespace :warning: 

In this namespace please to be aware changes must be deleted manually, as flux has set-up flag `prune` to `false`. 
Also this namespace is using another structure than other namespaces, because there are only low-level and critical
components for the Agora Kubernetes Platform, so every component has defined different kustomization inside Flux.
