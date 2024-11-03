# How to expose a `pre-prod` service on public internet

## Overview

This document provides a comprehensive step-by-step guide on how to expose your service deployed on the Agora `pre-prod` environment to the public, enabling you to access and test your service over the internet.

!!! Note
    Before starting this process, please consult with your service manager, compliance personnel, infra, QA, and security teams to ensure that the content your service will be publishing online follows all the necessary guidelines.

## What you'll learn

* [Step 1: Prepare the manifest files for your VirtualService to join the VirtualGateway](#step-1-prepare-the-manifest-files-for-your-virtualservice-to-join-the-virtualgateway)
* [Step 2: Add your DNS record to Route53 in `common.yaml`](#step-2-add-your-dns-record-to-route53-in-commonyaml)
* [Step 3: Finalize your changes and request approval](#step-3-finalize-the-above-changes)
* [Recommended step: Whitelist your service](#whitelisting-your-service)

## Prerequisites

Before you start, please ensure that your service:

* Is already deployed and running successfully on `pre-prod`.
* Meets authentication and security feature requirements.
* Has been properly tested before being exposed to the internet.
* Has logging, monitoring, and backup functionalities.

## Steps

### Step 1: Prepare the manifest files for your VirtualService to join the VirtualGateway

Create the file `virtualservices-routetable-${your-service}-public-ingress-city-gateway.yaml` under `infrastructure/k8s/environments/dev2/clusters/worker1-east/city-public-ingress/ungloo/` as follows:

```yaml
apiVersion: networking.istio.io/v1beta1
kind: VirtualService
metadata:
  name: virtualservice-public-ingress-${your-service}
  namespace: city-public-ingress
spec:
  exportTo:
  - .
  gateways:
  - virtualgateway-default-city-pub-03deeadef8506e5fd76accb27604197
  hosts:
  - ${your-service}.agora-dev.w3n.io
  http:
  - match:
    - sourceLabels:
        app: city-public-ingress
      uri:
        prefix: /
    name: ${your-service}.${your-namespace}
    route:
    - destination:
        host: ${your-service}.${namespace}.svc.cluster.local
        port:
          number: ${your-service-port}
```

Add the DNS endpoint of your service to the city public [`gateway` file](https://github.com/wp-wcm/city/blob/main/infrastructure/k8s/environments/dev2/clusters/worker1-east/city-public-ingress/ungloo/gateways-virtualgateway-default-city-pub-03deeadef8506e5fd76accb27604197.yaml):

```yaml
  servers:
  - hosts:
    - cvm.agora-dev.w3n.io
    - id.agora-dev.w3n.io
    - chimaki-survey.agora-dev.w3n.io
    - foodagri-dev.agora-dev.w3n.io
    - ${your-service}.agora-dev.w3n.io
    port:
      name: https-8443-cvm-agora-dev-w3n-io
      number: 8443
      protocol: HTTPS
```

Next, create `authorizationpolicy-${your-service}-public-internet-policy.yaml` under `infrastructure/k8s/environments/dev2/clusters/worker1-east/city-public-ingress/`. This will be your new `AuthorizationPolicy` file:

```yaml
apiVersion: security.istio.io/v1
kind: AuthorizationPolicy
metadata:
  name: ${your-service}-public-internet-policy
  namespace: city-public-ingress
spec:
  action: ALLOW
  rules:
    - to:
        - operation:
            hosts:
              - "your-service.${cluster_domain}"
  selector:
    matchLabels:
      app: city-public-ingress
```

### Step 2: Add your DNS record to Route53 in [`common.yaml`](https://github.com/wp-wcm/city/blob/main/infrastructure/terraform/environments/dev2/common.yaml)

```yaml
    city-public-ingress:
    alias:
      - cvm
      - id
      - mini-app-backend
      - woven-app-api
      - woven-chat
      - woven-chat-status-service
      - chimaki-survey
      - ${your-service}
```

### Step 3: Finalize the above changes

Add the public internet policy for your service in the correct alphabetical position in [`kustomization.yaml`](https://github.com/wp-wcm/city/blob/main/infrastructure/k8s/environments/dev2/clusters/worker1-east/city-public-ingress/kustomization.yaml), as shown below:

```yaml
apiVersion: kustomize.config.k8s.io/v1beta1
kind: Kustomization
resources:
  ...
  - authorizationpolicy-${your-service}-public-internet-policy.yaml
```

Then, run `bazel run //:gazelle`. Make a pull request and ask the Agora [DevRel](https://toyotaglobal.enterprise.slack.com/archives/C0415J5P1FD) or [Infra](https://toyotaglobal.enterprise.slack.com/archives/C02USLDU1U3) team for review.

## Whitelisting your service

Since the services deployed in the `pre-prod` environment are mainly used for the POC and demonstration purposes, we strongly suggest whitelisting your service with the specified public IPs to differentiate it as a public service.

To do this, update your `AuthorizationPolicy` file as follows:

```yaml
## Example allow 192.168.1.1 and range 172.1.1.0 - 172.1.1.255
apiVersion: security.istio.io/v1
kind: AuthorizationPolicy
metadata:
  name: ${your-service}-public-internet-policy
  namespace: city-public-ingress
spec:
  action: ALLOW
  rules:
    - to:
      - operation:
        hosts:
          - "your-service.${cluster_domain}"
    - from:
      - source:
        remoteIpBlocks:
          - 192.168.1.1/32
          - 172.1.1.0/24
  selector:
    matchLabels:
      app: city-public-ingress
```

## More information

For examples and details on exposing public services on the `pre-prod` environment, see:

* [Live example (Chimaki survey endpoint)](https://github.com/wp-wcm/city/pull/28728)
* [POC-GoLive Process on Pre-production](https://docs.google.com/document/d/115lsD5FN1eIWF2dGwzbVVIIYdS7WYWxHYdmfvHCmm1w)
