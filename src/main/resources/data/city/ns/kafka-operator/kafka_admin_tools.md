# Kafka Admin Tools

## Kafka UI

You can access Kafka UI (AKHQ) for the admins if you are in `/ns/kafka-admin` group set in Keycloak.
- lab: https://kafka-admin.agora-lab.woven-planet.tech/ui/kafka-cluster/topic
- dev: https://kafka-admin.cityos-dev.woven-planet.tech/ui/kafka-cluster/topic

For service teams, we provide the service-team-focused AKHQ. 
- lab: https://kafka-monitor.agora-lab.woven-planet.tech/ui/login
- dev: https://kafka-monitor.cityos-dev.woven-planet.tech/ui/login

The admins must assign the user under `/ns/<SERVICE_TEAM_NAMESPACE>` group in Keycloak so that the service team can view only their resource. 

## Kafka CLI

We can delete topics on the AKHQ for the admins but cannot delete ACLs on that.
We can use the kafka cli container for that.
This can be risky so not deployed permanently.

If you need, you can deploy by adding the following yaml.

- `infrastructure/k8s/common/kafka-admin/kafka-admin-0.1.0/cli-deployment.yaml`
 
```cil-deployment.yaml
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: kafka-admin-cli
  namespace: kafka-admin
  labels:
    app: kafka-admin-cli
spec:
  replicas: 1
  selector:
    matchLabels:
      app: kafka-admin-cli
  template:
    metadata:
      labels:
        app: kafka-admin-cli
      annotations:
        # Add these annotations to mount the certificates from the secrets in the sidecar.
        # `secretName` must match `secretName` on the namespace's Certificate.
        sidecar.istio.io/userVolume: |-
          [
            {"name": "certs", "secret": {"secretName": "kafka-client-certs"}},
            {"name": "cacert", "secret": {"secretName": "kafka-ca-cert"}}
          ]
        # Paths in volume mounts must match those in the DestinationRule.
        sidecar.istio.io/userVolumeMount: |-
          [
            {"name": "certs", "mountPath":"/etc/kafka-certs", "readonly":true},
            {"name": "cacert", "mountPath":"/etc/kafka-cacert", "readonly":true}
          ]
    spec:
      containers:
      - name: kafka-admin-cli
        image: cli-placeholder
        # overwrite the command to avoid the following error:
        #   KAFKA_ZOOKEEPER_CONNECT is require.
        #   Command [/usr/local/bin/dub ensure KAFKA_ZOOKEEPER_CONNECT] FAILED
        command:
        - "/bin/bash"
        - "-c"
        - "trap : TERM INT; sleep infinity & wait"
```

The sample deletion scripts are in https://github.tri-ad.tech/kohei-watanabe/delete-kafka-topics-acls
