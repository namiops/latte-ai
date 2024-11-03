# Agora Infra

| Last Update | 2024-07-19              |
|-------------|-------------------------|
| Tags        | Infrastructure, Grafana |

## Contents

* [Agora Infra](#agora-infra)
  * [Monitoring](#monitoring)
  * [Setting up Alerts for Pre-production](#setting-up-alerts-for-pre-production)
      * [Pre-requisites](#pre-requisites)
      * [Steps](#steps)
* [Istio](istio.md)
* [Speedway](speedway.md)

## Monitoring

* **Gatekeeper**
  * [Development](https://observability.cityos-dev.woven-planet.tech/grafana/d/c6bed1c56f60c2a405f79794cd5042c9a8e1bda4/gatekeeper-overview?orgId=1)
  * [Preproduction](https://athena.agora-dev.w3n.io/grafana/d/c6bed1c56f60c2a405f79794cd5042c9a8e1bda4/gatekeeper-overview?orgId=1)

## Setting up Alerts for Pre-production

If you need or want to monitor Flux and the events on the preproduction you can
set up a Slack notification doing the following:

### Pre-requisites

* On the Preproduction environment
  under `infrastructure/k8s/environments/dev2/clusters/worker1-east`
* Have a tenant
  under `infrastructure/k8s/environments/dev2/clusters/worker1-east/flux-tenants/namespaces`
* Have a Slack Channel to work with or you wish to send notifications to

### Steps

1. Generate a YAML manifest in
   the `infrastructure/k8s/environments/dev2/clusters/worker1-east/flux-tenants/notifications`
   folder. Fill in the values in `<>` with the appropriate values:

* `<MY_KIND>` can refer to the following:
    * Kustomization
    * ImageAutomationUpdate
    * ImageRepository
* Make sure that <MY_RESOURCE_NAME> matches the appropriate `metadata.name`

```yaml
apiVersion: notification.toolkit.fluxcd.io/v1beta2
kind: Alert
metadata:
  name: <MY_NAMESPACE>-slack
  namespace: flux-tenants
spec:
  eventSeverity: error
  eventSources:
    - kind: <MY_KIND>
      name: <MY_RESOURCE_NAME>
  providerRef:
    name: <MY_NAMESPACE>-slack-provider
---
apiVersion: notification.toolkit.fluxcd.io/v1beta2
kind: Provider
metadata:
  name: <MY_NAMESPACE>-slack-provider
  namespace: flux-tenants
spec:
  type: slack
  channel: <MY_SLACK_CHANNEL>
  address: https://slack.com/api/chat.postMessage
  secretRef:
    name: flux-bot-slack-token
```

2. Add your new YAML manifest to
   the `infrastructure/k8s/environments/dev2/clusters/worker1-east/flux-tenants/notifications/kustomization.yaml`
   file. **Add your file in alphabetical order**

3. Run the following:

```shell
bazel run //:gazelle
```

