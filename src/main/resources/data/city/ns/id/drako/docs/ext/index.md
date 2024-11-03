# About Drako

Drako, named after
the [Athens' legislator](https://en.wikipedia.org/wiki/Draco_(lawgiver)), is
Agora's Policy Decision Point ([PDP](https://csrc.nist.gov/glossary/term/policy_decision_point))
system.

It is implemented as an external authorizer (Istio configuration, leveraging
envoy filters) and proxy the requests to other authorizers and is configured
directly via Kubernetes manifests.

It supports hot-reloading of configurations. You should expect to have your new configuration take effect
right after pushing changes to the manifest files.

## Quick Start

For a quick start, please refer to the [authorization scenarios documentation](authorization_scenarios/). This resource will guide you in adopting Drako to address common authorization questions, complete with fully working examples for each scenario.

For the time being, you need to set a label for your service to use [Drako Buddy](drako_buddy/) as described in its documentation.

## Reference Documentation

To learn more about Drako in depth, please check the following references:

- [DrakoPolicyBinding](crd/DrakoPolicyBinding/)
- [DrakoPolicy](crd/DrakoPolicy/)
- [DrakoGroup](crd/DrakoGroup/)
