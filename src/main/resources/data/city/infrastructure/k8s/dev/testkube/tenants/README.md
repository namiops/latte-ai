# Testkube tenants directory

This directory deploys tenants resources to the Testkube namespace. The goal is to avoid flux kustomization blocking failures for the tenant namespaces during upgrades and maintenance windows.

This directory is applied via the flux kustomize resource located [here](../../flux-system/kustomizations/services/testkube-tenants.yaml)
