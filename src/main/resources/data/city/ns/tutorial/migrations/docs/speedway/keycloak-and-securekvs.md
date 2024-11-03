# Keycloak secrets and SecureKVS setup for Speedway

## Keycloak secrets

Deploying Keycloak secrets has the same manual process as in pre-prod:

```sh
kubectl apply -f - <<EOF
apiVersion: v1
data:
  CLIENT_ID: <>
  CLIENT_SECRET: <>
kind: Secret
metadata:
  name: keycloak-client-secret-ac-user-registration
  namespace: agora-ac-user-registration-prod
type: Opaque
EOF
```

Once your secrets have been set up for pre-prod (the current stable URL points to pre-prod), you can configure them in your Speedway manifest file.

```yaml title="infra/k8s/agora-name/speedway/common/service/deployment.yaml" hl_lines="33-44"
apiVersion: apps/v1
kind: Deployment
metadata:
  labels:
    app: <service>
  name: <service>
spec:
  replicas: 1
  selector:
    matchLabels:
      app: <service>
  template:
    metadata:
      labels:
        app: <service>
    spec:
      containers:
        - name: <service>
          image: agora-<name>-placeholder
          env:
            - name: APP_NAME
              valueFrom:
                fieldRef:
                  fieldPath: metadata.labels['app']
            - name: APP_VERSION
              valueFrom:
                fieldRef:
                  fieldPath: metadata.labels['version']
            - name: POD_UID
              valueFrom:
                fieldRef:
                  fieldPath: metadata.uid
            - name: KEYCLOAK_CLIENT_ID
              valueFrom:
                secretKeyRef:
                  name: keycloak-client-secret-agora-<name>
                  key: CLIENT_ID
            - name: KEYCLOAK_CLIENT_SECRET
              valueFrom:
                secretKeyRef:
                  name: keycloak-client-secret-agora-<name>
                  key: CLIENT_SECRET
            - name: KEYCLOAK_ENDPOINT_DOMAIN
              value: https://id.woven-city.toyota # Uses the ID stable URL
          ports:
            - containerPort: 8080
              name: http
              protocol: TCP
          securityContext:
            allowPrivilegeEscalation: false
      securityContext:
        runAsNonRoot: true
        runAsUser: 1000
```

## SecureKVS

SecureKVS requires some setup on the Speedway side:

- Create a `couchdbdatabase` object in your namespace and Create an authorization policy in the SKVS namespace

  You can set up SecureKVS using [agoractl](https://developer.woven-city.toyota/docs/default/component/agoractl-tutorial/plugins/08_agoractl_securekvs/).

  ```bazel
  bazel run //ns/agoractl -- securekvs -env ENV -n YOUR_DATABASE_NAME -ns YOUR_NAMESPACE -o OUTPUT_DIR -s YOUR_SERVICE_ACCOUNT
  ```

  Please modify the above values accordingly:

    - `-env`: Set to `dev3` for Speedway DEV, or `prod` for Speedway PROD.

    - `-n`: Set the name of your database.

    - `-ns`: Set your namespace.

    - `-o`: Set the output directory for the resources. For example: `infra/k8s/{YOUR_NAMESPACE}/speedway/{ENV}`.

    - `-s`: Set your service account.

- Update a sidecar in your namespaces

  According to the SMC Guide [here](https://portal.tmc-stargate.com/docs/default/Component/STARGATE-WELCOME-GUIDES/stargate-multicloud/documentation/features/service-mesh/intra-mesh-traffic/#one-way-a-b), to allow requests from SecureKVS to start in your namespace, you'll need to update the sidecar in your namespace as follows:

  ```yaml
  apiVersion: networking.istio.io/v1beta1
  kind: Sidecar
  metadata:
    name: default
    namespace: YOUR_NAMESPACE
  spec:
    egress:
    - hosts:
      - "./*"
      - "istio-system/*"
      # For Speedway Dev, use this:
      - "agora-secure-kvs-dev/*"
      # For Speedway Prod, use this:
      - "agora-secure-kvs-prod/*"
  ```
