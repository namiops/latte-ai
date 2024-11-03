# Remote Pulumi

## What?

This is a Pulumi stack example that should be deployed via `pulumi-kubernetes-operator`.

## Why?

It is a part of Pulumi PoC: https://wovencity.monday.com/docs/6076222940

## How?

Operator should deploy `stack.yaml` via integration with Flux (yaml should be put to infrastructure folder and bound to a kustomization).

Unfortunately it won't work in Agora's monorepo, because the operator, as of now (2024-02-16), does not support repos of this size, see:

https://github.com/pulumi/pulumi-kubernetes-operator/blob/v1.14.0/pkg/controller/stack/flux.go#L19

```shell
$ du -sh city
1.9G    city
```

### References
- https://fluxcd.io/blog/2023/02/flux-pulumi-superpowers/
- https://github.com/pulumi/pulumi-kubernetes-operator/blob/master/docs/stacks.md
- https://www.pulumi.com/docs/using-pulumi/continuous-delivery/pulumi-kubernetes-operator/
