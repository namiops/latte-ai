# Chat App Demo for Drako

## About

This is a chat application for introducting how to use and setup authz configuration using drako on the k8s cluster.
This application consists of both frontend app and backend app.

### Frontend

- Authenticate the user.
- Render chat UI and communicate with the backend.

### Backend

- Receive the request from the frontend and authorize the request.
- Store and response back data to frontend.

## How to run in local cluster

### Deploy the local cluster

If this is the first time deploying to local cluster, you might want to check the tutorial
in [local_cluster_deployment.md](/ns/id/docs/int/onboarding/local_cluster_deployment.md).

### Deploy the chat app and its dependencies

Uncomment `consent.yaml` and `id-chat.yaml` in [kustomization.yaml](/infrastructure/k8s/local/flux-system/kustomizations/services/kustomization.yaml).
Push the commit to origin branch.
Wait for flux to finish reconciliation and check if `id-chat` namespace exists.

### Create a new group in consent service

Create a new group in consent service with the name specified in the `VITE_APP_SERVICE_NAME` environment variable (i.e., `agora-id-chat`).

```shell
curl -X 'POST' \
  'http://consent.consent.svc.cluster.local/v3alpha/admin/groups/agora-id-chat' \
  -H 'accept: */*'
```

### Add client ID of the chat frontend app to the group

Add client ID specified in the `VITE_APP_KEYCLOAK_CLIENT_ID` environment variable (i.e., `id-chat-public`) to the group specified in the `VITE_APP_SERVICE_NAME` environment variable (i.e., `agora-id-chat`).

```shell
curl -X 'POST' \
  'http://consent.consent.svc.cluster.local/v3alpha/admin/groups/agora-id-chat/clients?client_ids=id-chat-public' \
  -H 'accept: */*'
```

### Try the chat app

If you run the local cluster in EC2 instance please follow
additional steps in [local_cluster_access.md](/ns/id/docs/int/onboarding/local_cluster_access.md) to be able to access the chat app from your PC.
Make sure to declare `id-chat.woven-city.local` in `/etc/hosts`.
Finally, open <https://id-chat.woven-city.local/> on your browser (Chrome or Firefox).
