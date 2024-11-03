# Kafka Configuration for Speedway environment (IPv4)

## Kafka bootstrap servers

Please set to `${KAFKA_BOOTSTRAP_SERVERS}`. CityCD will substitute it.

To view the list of variables substituted by CityCD, navigate to the 'DETAILS' section of your ArgoCD application's page and then switch to the 'MANIFEST' tab.
![citycd_substition_example.jpg](citycd_substition_example.jpg)

## Kafka template values

When using the Speedway environment, set `cityoskafkas[*].env` to `speedway` as shown below:

- `kafka-values.yaml` in `common`

```yaml
cityoskafkas:
  - env: speedway # <========= Add this
#!  namespace: ${NAMESPACE}  <==== This will be set by default
    topics:
        name: <your_topic_name>
        description: <your_topic_description>
        readAccessPrincipals:
          - User:CN=${NAMESPACE}.${CLUSTER_DOMAIN},OU=CityOS  # <=== Please use UPPERCASE for variables in ${} so that CityCD can substitute them
        writeAccessPrincipals:
          - User:CN=${NAMESPACE}.${CLUSTER_DOMAIN},OU=CityOS  # <=== Please use UPPERCASE for variables in ${} so that CityCD can substitute them
    consumerGroups:
      - datacg
```

This will generate manifests tailored for the speedway environment.


## Add istio annotation temporarily

Currently(Sep. 12, 2024), it seems that the webhook is not stable in the speedway environment.
- [Agora Infra Tasks - Issue with webhooks certificates](https://wovencity.monday.com/boards/5710223440/pulses/6969415996/posts/3449789070)
- [Agora Infra Tasks - Stabilizing the Certificate Issuance Process Using AWSClusterIssuer on Speedway (Only Dev?)](https://wovencity.monday.com/boards/5710223440/views/125027254/pulses/7213364220?term=kafka-operator)

 Therefore, although the following annotation is normally added automatically by the Kafka operator's mutating webhook when `CityOsKafka` is in the same namespace and doesn't need to be written, it is recommended to include it until the webhook stabilizes


```yaml
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: NAME
spec:
  template:
    metadata:
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
```


## If you want to access from the SMC cluster, not from Speedway...


!!!NOTE
    The method is not stable yet, and there is possibility of change in the future. The ticket: [Sprint [Orc] - Design IAM configuration for Agora Kafka access](https://wovencity.monday.com/boards/3813113014/views/90945203/pulses/7340702217)

We use operators to register [Kafka topics](https://github.com/wp-wcm/city/tree/main/ns/kafka-operator) and [Apicurio schema registry schemas](https://github.com/Apicurio/apicurio-registry-content-sync-operator).

These operators can only be used in the Speedway environment, so you need to follow the steps below to set it up. Only the topic configuration and schemas are required to be deployed on Speedway, your application workloads can be deployed directly on SMC.

### 1. Create a Speedway namespace and set up the CityCD project:
- Create your Speedway namespace following https://developer-portal.woven-city.toyota/docs/default/component/agora-migrations-tutorial/speedway/#first-step-namespace-creation
- Then, set up your CityCD project following https://developer-portal.woven-city.toyota/docs/default/component/agora-migrations-tutorial/speedway/citycd/#citycd

### 2. Create `CityOsKafka` (and `Artifact`) resources:
Create the YAML files for the `CityOsKafka` (and `Artifact`) resources in the namespace created in `#1`. For reference, check out [the Mini App team's configuration](https://github.com/wp-wcm/city/blob/main/infra/k8s/agora-wcm-mini-app/speedway/common/kafka-0.0.1/kafka-config-values.yaml))

You can verify the created resources in [our ArgoCD](https://argocd.agora-dev.w3n.io/applications).


### 3. Create IAM resources and annotate the ServiceAccount:

- For Speedway Dev: https://github.com/wp-wcm/city/tree/main/infrastructure/terraform/environments/dev2/base/bucket1_east-kafka_iam_k8s_configs
- For Speedway Prod: https://github.com/wp-wcm/city/tree/main/infrastructure/terraform/environments/prod/accounts/storage-valet/kafka_iam_k8s_configs

Please create a file as shown below and ask `@agora-data-orchestration` for the review. 

- `<YOUR_NAMESPACE>.yaml`

```yaml
topic_prefix: "`<YOUR_SPEEDWAY_NAMESPACE>`."
read_namespace_service_accounts:
  - "`<SMC_NAMESPACE_A>`:`<K8S_SERVICE_ACCOUNT_A>`"
  - "`<SMC_NAMESPACE_B>`:`<K8S_SERVICE_ACCOUNT_B>`"
write_namespace_service_accounts: [] # When nothing is configured, please set it to an empty list. 
```

To enable IRSA to function with the IAM role created by this, please add the following annotation to the k8s `ServiceAccount` resource:

```yaml
apiVersion: v1
kind: ServiceAccount
metadata:
  annotations:
    eks.amazonaws.com/role-arn: arn:aws:iam::471112803776:role/`<YOUR_SMC_NAMESPACE>`-`<YOUR_SERVICE_ACCOUNT>`-msk-role # Speedway Dev
#    eks.amazonaws.com/role-arn: arn:aws:iam::058264466652:role/`<YOUR_SMC_NAMESPACE>`-`<YOUR_SERVICE_ACCOUNT>`-msk-role # Speedway Prod
  name: `<YOUR_SERVICE_ACCOUNT>`
  namespace: `<YOUR_SMC_NAMESPACE>`
```


### 4. Update the CODEOWNERS file

Let's update [CODEOWNERS file](https://github.com/wp-wcm/city/blob/main/CODEOWNERS) as shown below so that your team can edit the files created so far by yourselves.
The `@wp-wcm/agora-data` team will be responsible for reviewing IAM changes until the [Sprint [Orc] - Set the kafka IAM yamls CODEOWNERS properly](https://wovencity.monday.com/boards/3813113014/views/90945203/pulses/7313170686) is finished.

```
# Speedway namespace
/infra/k8s/agora-kafka-monitor @wp-wcm/agora-devrel @wp-wcm/agora-data @wp-wcm/<YOUR_GROUP>
```

### 5. Configure a Kafka client to use AWS IAM with AWS_MSK_IAM mechanism

In the Agora and Speedway environments, we were able to set up Kafka clients using the PLAIN text mechanism through configurations like Istio's `DestinationRule`s. However, in other environments, you will need to set up the Kafka client using AWS IAM. Please follow [the official documentation to configure the Kafka client to use the AWS_MSK_IAM mechanism](https://github.com/aws/aws-msk-iam-auth?tab=readme-ov-file#configuring-a-kafka-client-to-use-aws-iam-with-aws_msk_iam-mechanism).

!!!NOTE
    Make sure to set the consumer_group_id to start with `smc.<SMC_NAMESPACE>.` ( The consumer group ID/role name may change in the future, and the consumer group might need to read messages from the beginning.)
