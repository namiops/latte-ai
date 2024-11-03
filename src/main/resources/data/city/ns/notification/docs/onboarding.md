# Notifier onboarding guide 

## Overview

This document instructs new and existing (i.e. DEV) clients on how to start using Notification service or migrate to a new environment.

## Prerequisites

### Authorization policy

In all environments, the Notifier API endpoints are protected by auth policies, so please let us know the following info to give you the access:

* K8s namespace of your service
* Target environment (i.e. dev2/speedway/smc) dev or prod
* Desired endpoints (i.e /{user_id}/settings/slackChannelWebhook)

### Spedway/SMC specific configuration

In order to enable the cross-service communication in Speedway or SMC environment, you will need to add an entry to the `Sidecar` resource (or create a new one) in your namespace.

```yaml
apiVersion: networking.istio.io/v1beta1
kind: Sidecar
metadata:
  name: default
  namespace: ${NAMESPACE}
spec:
  egress:
  - hosts:
    - "./*"
    - "istio-system/*"
    - "{NOTIFICATION_NAMESPACE}/*"
```

* Non-prod
   - Notification namespace - `agora-notification-dev`
   - Notification service url - `http://notifier.agora-notification-dev.svc.cluster.local:8081`
* Prod
   - Notification namespace - `agora-notification-prod`
   - Notification service url - `http://notifier.agora-notification-prod.svc.cluster.local:8081`

For more details about the Sidecar, please refer [Intra Mesh Traffic](https://portal.tmc-stargate.com/docs/default/Component/STARGATE-WELCOME-GUIDES/stargate-multicloud/documentation/features/service-mesh/intra-mesh-traffic/#examples) doc in Stargate dev portal.

### Data migration

If you are migrating to a new environment and have been storing notification user settings (i.e slackChannelWebhook or wovenAppToken), please make sure you create that data in the new environment as well by using the corresponding `PATCH {user_id}/settings/*` endpoints.

## Contact us

As always, feel free to tag **@agora-notifier** engineers in the [#wcm-org-agora-services](https://toyotaglobal.enterprise.slack.com/archives/C042AQ2TU4A) Slack channel with the above requests or other questions you might have regarding the Notification service.
