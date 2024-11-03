# Consent 101

[go/agora-consent-101](http://go/agora-consent-101)

## Introduction

This code lab is intended to walk service developers through the different
consent-related workflows and use-cases they may encounter when using PII in
Agora. For more detailed information, please refer to the main [consent
documentation](../consent/README.md).

The code lab will start with two services, one that serves some PII and one that
reads it, and then incrementally walk you through how to add support for
consent to the flow.

## Prerequisites

You are more than welcome to just read through this code lab, but for maximum
benefit you should follow along with it. To do that, you will need the
following:

- [A Minikube cluster](https://minikube.sigs.k8s.io/docs/start/)
- A clone of this repo ([the city mono repo](https://github.com/wp-wcm/city))
- Bazel set up to work in the monorepo

Once you have all of that ready, continue to the [next section](01_no_consent.md)!
