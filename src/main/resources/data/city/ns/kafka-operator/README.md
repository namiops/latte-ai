# Kafka Operator

In order to help service teams get their applications connected to the Kafka message bus, many tasks have been automated
to remove the CityOS team from the process. 

As the owner of a namespace, service teams have the right to control Kafka topics and access control lists (ACLs)
related to that namespace. They do so by creating a Kubernetes custom resource object in the namespace. The operator
then automatically detects creation or modification of these objects and updates the Kafka resources accordingly.

This is an operator to create a Custom Resource Definition (CRD) for Kafka cluster state and a controller to monitor
those created CRs across all namespaces, and then apply the changes via a Kafka client.

Built on operator-sdk.

## Usage

### changes in API version

Samples are in `./config/samples`

#### v1alpha2
- The ACL ResourceName also automatically have the namespace prepended when deployed

#### v1 alpha3
- **BREAKING CHANGE** The topic delimiter is modified from "-" to ".", which makes kafka topics and acls(`v1alpha1`, `v1alpha2`) recreate.
  - e.g. namespace(`e-palette`) prepended topic name is modified from `e-palette-adk-data` to  `e-palette.adk-data`
- The yaml attributes case changed from lower case to camel case
  - e.g. `replicationfactor` to `replicationFactor`
- Other topic formats are supported for Kafka in the operator, e.g. compaction, retention time
- [create mTLS related resources from kafka-operator](https://wovencity.monday.com/boards/3813113014/pulses/3813275325)

### CRD and metadata

#### v1alpha3
```yaml
apiVersion: kafkagroup.woven-city.global/v1alpha3
kind: CityOsKafka
metadata:
 name: service-namespace
 namespace: service-namespace
```
 
### Topics
Topics are relatively straightforward. Specifying a topic will create a new topic with the desired attributes. 
Currently, specifying the partition and replication factor is supported. Note that topics only need to be specified
_once_ by the namespace that "owns" them, and not by any namespace publishing to or consuming it. 

The topics will automatically have the namespace prepended when deployed. This is done to prevent accidental collisions
in topic names. In the example given below, the namespace is `service-namespace`.

For now, replicationFactor should be 2, unless you are very certain of what you are doing.


#### v1alpha3

The following optional topic configs are supported. Details are in [TN-0160 CityOsKafka Topic Config - Google Docs](https://docs.google.com/document/d/1CgwAo0KtWZ7uMk-A65HIxOY5pgTzk5gECIj2rByWI1w/edit#)

```yaml
cleanupPolicy:
  default: delete
  description: 'A string that is either "delete" or "compact" or both ("compact,delete"). This string designates the retention policy to use on old log segments. The default policy ("delete") will discard old segments when their retention time or size limit has been reached. The "compact" setting will enable log compaction on the topic. Default: delete'
  enum:
    - delete
    - compact
    - compact,delete
  type: string
  
maxMessageSize:
  default: 1MB
  description: 'The largest record batch size allowed by Kafka (after compression if compression is enabled). 
    This value should be less than 10MB. If you need more, please consider sending just metadata to Kafka.
    Valid time units are KB, KiB, MB, MiB. (Here, KB/MB is assumed to mean the same as KiB/MiB)
    ref: [How to send Large Messages in Apache Kafka?](https://www.conduktor.io/kafka/how-to-send-large-messages-in-apache-kafka)
    Default: 1MB'
  type: string
  
retentionPeriod:
  default: 1w
  description: 'This configuration controls the maximum time we will retain a log before we will discard old log segments
    if we are using the "delete" retention policy. (**Note** cleanupPolicy is set to "delete" by default.) If set to -1, no time limit is applied.
    Valid time units are "ns", "us" (or "µs"), "ms", "s", "m", "h", "d", "w".  (and "-1")
    Default: 1w'
  type: string
```

The sample yaml is as follows:
```yaml
spec:
  topics:
    - name: topic-1
      partitions: 9
      replicationFactor: 3
      ## optional
      cleanupPolicy: compact,delete
      maxMessageSize: 1MB
      retentionPeriod: 4w
```

will result in the deployment of a topic with the name `service-namespace-topic-1` in `v1alpha1`&`v2alpha2` and  `service-namespace.topic-1` in `v1alpha3`

### ACLs
ACLs are designed to restrict access to Kafka resources. Each ACL has a Resource (only topics and consumer groups are
currently supported) and policies.

#### v1alpha3
```yaml
spec:
  acls:
    # Topic ACLs should be specified in the same namespace as the topic.
    - resource:
        resourceName: topic-1  # Just topic name. This will result in aws-pca-cert.topic-1 (<namespace>.<topic>)
        resourcePattern: literal
        resourceType: topic
      policies:
        - principal: User:CN=aws-pca-cert-test,OU=CityOS
          operation: write
          permission: allow
        - principal: User:principal # Grant access to a SASL/SCRAM user. This is deprecated and should be avoided.
          operation: write
          permission: allow

    # Consumer group ACLs should be specified in every namespace that consumes messages from Kafka, regardless of which
    # topics it is consuming and which namespace owns them.
    - resource:
        resourceName: consumer-group  # This will result in aws-pca-cert.consumer-group(<namespace>.<topic>)
        resourcePattern: literal
        resourceType: group
      policies:
        - principal: User:CN=aws-pca-cert-test,OU=CityOS
          operation: read
          permission: allow
```

### Resources:
`resourceName` is the name of the topic to apply the ACL to
`resourcePattern` may be ‘literal’, specifying a full topic name, or ‘prefixed’ which will allow, for example, matching
all topics in a namespace
`resourceType` must be ‘topic’ or ‘group’

### Policies:
`host` can restrict access to specific hostnames. Most applications should use ‘*’
`principal` is the user name, which is the subject on the SSL certificate (see Certificate, below). It MUST start with `User:`
`operation` may be ‘read’, ‘write’, or ‘describe’
`permission` may be ‘allow’ or ‘deny’

Note that consumers must have both ‘read’ and ‘describe’ access on a topic, as well as ‘read’ on the consumer group they
will join in order to consume data from topics.

### mTLS settings

mTLS settings are required to communicate with the Kafka cluster (Amazon MSK),
The mTLS settings includes the following:
- (zebra) Certificates
- (zebra) Amazon RootCA Secret
- (zebra) Destination Rule
- (kafka-operator) Istio annotation (userVolume, userVolumeMount) to Pods

These mTLS resources are created by [the code abstraction tool, zebra](https://developer.woven-city.toyota/docs/default/Component/kafka-service/01_quickstart/), and the annotation will be added by kafka-operator, so you don't have to create by yourself.

Note that this setup allows your application code to use PLAINTEXT to communicate with Kafka. 
Communication is still secure, however, as the TLS encryption is handled by Envoy. 
Application-level TLS setup is not required.

#### Certificates
SSL certificates are required to communicate with the Kafka cluster, they are used to both authenticate and authorize
the service. These certificates can be automatically requested by deploying a Certificate resource into the service
namespace. Currently, namespaces should only have one certificate per namespace.

Please note that these certificates will expire, based on the duration requested in the custom resource. The certificate
secrets (and volume mounted files) will be updated and injected to the sidecar automatically. This _may_ cause
disconnections for long-lived connections, requiring the application to reconnect to Kafka.

Recommended config for this resource is below, replacing the name of the namespace as necessary. 

```yaml
apiVersion: cert-manager.io/v1
kind: Certificate
metadata:
 name: kafka-client-certs
 namespace: service-namespace-kafka
spec:
 duration: 2160h # 90d
 renewBefore: 360h # 15d
 commonName: service-namespace.${cluster_domain}
 subject:
   organizationalUnits:
   - "CityOS"
 dnsNames:
   - service-namespace.${cluster_domain}
 usages:
   - client auth
 privateKey:
   rotationPolicy: Always
   algorithm: "RSA"
   size: 2048
 secretName: kafka-client-certs
 issuerRef:
   group: awspca.cert-manager.io
   kind: AWSPCAClusterIssuer
   name: aws-pca-cluster-issuer
```

The certificate will be automatically issued (and rotated) and injected into the namespace as a Kubernetes Secret
containing a data object with tls.crt (the client certificate) and tls.key (the client private key). 

The subject name on this certificate MUST match the principal property in the ACL policy.

#### Amazon RootCA Secret

```yaml
# Kafka server certificate CA.
apiVersion: v1
data:
  # This certificate is the lab/dev cluster.
  kafka-ca-cert.pem: LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tCk1JSUQ3ekNDQXRlZ0F3SUJBZ0lCQURBTkJna3Foa2lHOXcwQkFRc0ZBRENCbURFTE1Ba0dBMVVFQmhNQ1ZWTXgKRURBT0JnTlZCQWdUQjBGeWFYcHZibUV4RXpBUkJnTlZCQWNUQ2xOamIzUjBjMlJoYkdVeEpUQWpCZ05WQkFvVApIRk4wWVhKbWFXVnNaQ0JVWldOb2JtOXNiMmRwWlhNc0lFbHVZeTR4T3pBNUJnTlZCQU1UTWxOMFlYSm1hV1ZzClpDQlRaWEoyYVdObGN5QlNiMjkwSUVObGNuUnBabWxqWVhSbElFRjFkR2h2Y21sMGVTQXRJRWN5TUI0WERUQTUKTURrd01UQXdNREF3TUZvWERUTTNNVEl6TVRJek5UazFPVm93Z1pneEN6QUpCZ05WQkFZVEFsVlRNUkF3RGdZRApWUVFJRXdkQmNtbDZiMjVoTVJNd0VRWURWUVFIRXdwVFkyOTBkSE5rWVd4bE1TVXdJd1lEVlFRS0V4eFRkR0Z5ClptbGxiR1FnVkdWamFHNXZiRzluYVdWekxDQkpibU11TVRzd09RWURWUVFERXpKVGRHRnlabWxsYkdRZ1UyVnkKZG1salpYTWdVbTl2ZENCRFpYSjBhV1pwWTJGMFpTQkJkWFJvYjNKcGRIa2dMU0JITWpDQ0FTSXdEUVlKS29aSQpodmNOQVFFQkJRQURnZ0VQQURDQ0FRb0NnZ0VCQU5VTU9zUXErVTdpOWI0WmwxK09pRk94SHovTHo1OGdFMjBwCk9zZ1BmVHozYTNZNFk5azJZS2liWGx3QWdMSXZXWC8yaC9rbFE0Ym5hUnRTbXBEaGNlUFlMUTFPYi9iSVNkbTIKOHhwV3JpdTJkQlRyei9zbTR4cTZIWll1YWp0WWxJbEhWdjhsb0pOd1U0UGFoSFFVdzJlZUJHZzYzNDVBV2gxSwpUczlEa1R2blZ0WUFjTXRTN250OXJqcm52REg1UmZiQ1lNOFRXUUlyZ013MFI5KzUzcEJsYlFMUExKR21wdWZlCmhSaEpmR1pPb3pwdHFiWHVOQzY2RFFPNE05OUg2N0ZyalNYWm04NkIwVVZHTXBad2g5NENEa2xEaGJac2M3dGsKNm1GQnJNblVWTitITDhjaXNpYk1uMWxVYUovOHZpb3Z4RlVjZFVCZ0Y0VUNWVG1MZndVQ0F3RUFBYU5DTUVBdwpEd1lEVlIwVEFRSC9CQVV3QXdFQi96QU9CZ05WSFE4QkFmOEVCQU1DQVFZd0hRWURWUjBPQkJZRUZKeGZBTitxCkFkY3dLemlJb3JodFNwenlFWkdETUEwR0NTcUdTSWIzRFFFQkN3VUFBNElCQVFCTE5xYUVkMm5kT3htZlp5TUkKYnc1aHlmMkUzRi9ZTm9ITjJCdEJMWjlnM2NjYWFOblJib2JoaUNQUEU5NUR6K0kwc3dTZEh5blZ2L2hleU5YQgp2ZTZTYnpKMDhwR0NMNzJDUW5xdEtyY2dmVTI4ZWxVU3doWHF2ZmRxbFM1c2RKL1BITFR5eFFHamhkQnlQcTF6CnF3dWJkUXh0UmJlT2xLeVdON1dnMEk4VlJ3N2o2SVBkai8zdlFRRjN6Q2VwWW9VejhqY0k3M0hQZHdiZXlCa2QKaUVEUGZVWWQveDdINGM3L0k5dkcrbzFWVHFrQzUwY1JSajcwL2IxN0tTYTdxV0ZpTnlpMkxTcjJFSVpreVhDbgowcTIzS1hCNTZqemFZeVdmL1dpM01PeHcrM1dLdDIxZ1o3SWV5TG5wMktodkFvdG5EVTBtVjNIYUlQekJTbENOCnNTaTYKLS0tLS1FTkQgQ0VSVElGSUNBVEUtLS0tLQo=
kind: Secret
metadata:
  name: kafka-ca-cert
  namespace: service-namespace
type: Opaque
```

#### Destination Rule

Based on the certificate and secret we created above, the DestinationRule should be set as follows:

```yaml
# DestinationRule telling Envoy to originate an mTLS connection to Kafka, using custom certificates.
apiVersion: networking.istio.io/v1alpha3
kind: DestinationRule
metadata:
  name: kafka-mtls
  namespace: service-namespace
spec:
  host: kafka.generated
  exportTo:
    - "."
  trafficPolicy:
    tls:
      mode: MUTUAL
      clientCertificate: /etc/kafka-certs/tls.crt
      privateKey: /etc/kafka-certs/tls.key
      caCertificates: /etc/kafka-cacert/kafka-ca-cert.pem
---
```

#### Istio annotation (userVolume, userVolumeMount) to Pods

To enable injection of the secrets to your deployment and configure the Istio sidecar to use the certificates when
talking to Kafka, add the following annotations to your deployment:

```yaml
spec:
  template:
    metadata:
      annotations:
        # `secretName` must match `secretName` on the namespace's Certificate.
        sidecar.istio.io/userVolume: |-
          [
            {"name": "certs", "secret": {"secretName": "kafka-client-certs"}},
            {"name": "cacert", "secret": {"secretName": "kafka-ca-cert"}}
          ]

        # `mountPath` must match paths declared in the DestinationRule.
        # `name` must match `name` in the `userVolume` annotation above.
        sidecar.istio.io/userVolumeMount: |-
          [
            {"name": "certs", "mountPath":"/etc/kafka-certs", "readonly":true}, 
            {"name": "cacert", "mountPath":"/etc/kafka-cacert", "readonly":true}
          ]
```

!!!Warning
    Please note that the annotations _have_ to be included in all deployments in the namespace, even if they do not use Kafka.

Note that when using this configuration, applications should communicate with Kafka in plaintext - the sidecar will take
care of the mTLS origination. Applications should use `kafka.default` as the host name for the Kafka bootstrap URL.

## Relevant files

* `main.go` - the primary entrypoint for the project, also instantiates the Kafka client for actual deployments
* `api/<version>/cityoskafka_types.go` - definition of the CRD
* `config/samples/*` - sample YAML demonstrating how to create the CR, also useful as a test
* `controllers/cityos_kafka_controller.go` - the business logic for the Kafka reconciliation loop

### Reference files

Bazel is used and the following files are not necessary for this project but kept for reference.
* Makefile
* Dockerfile (`go.mod` is necessary to be generated as follows:)

```generate_go_mod.sh
echo "module github.com/wp-wcm/city/ns/kafka-operator" > go.mod

go mod tidy
```

`go.mod` is currently gitignored because bazel support just one `go.mod` file. (`ns/go.mod`)

When you need to update `ns/go.mod`, you need to delete `ns/kafka-opeartor/go.mod` temporarily.

ref: [Support for multiple Go projects in a monorepo · Issue #634 · bazelbuild/bazel-gazelle](https://github.com/bazelbuild/bazel-gazelle/issues/634)


## Regenerating

Whenever the API changes (modifications to anything in the `api` subtree) run `make generate` with the operator-sdk installed to regenerate several Golang methods: DeepCopy, DeepCopyInto, and DeepCopyObject that
depend on the types.

`make manifests` may also be required if the CRD has changed.

The files have been significantly changed from the generated ones. Be aware of what changes when running `make generate`.

## Building

Build the Dockerfile as normal, e.g. `docker build -t <repo>/kafka-reconcile-operator:<ver>` - this may be pushed to a remote registry or to Minikube as needed.

Alternatively, to build the operator with Bazel use `bazel build //ns/kafka-operator/...`, or 
`bazel build //ns/kafka-operator:image` to build the Docker image. Push to Artifactory with `bazel run //ns/kafka-operator:push`.

## Deploying

kubebuilder generates a Kustomize project.

Any overrides needed for the controller deploy (or other manifests) should be done via patches to avoid
losing changes to accidental overwrites upon regeneration or rerunning of kubebuilder.

A Makefile target has been added to build and move the YAMLs to the infrastructure folders for Flux to deploy.

- For the Speedway environment (The output will be generated in `infra/k8s/agora-kafka-system/common/`):
    - `make copy-built-yamls VER=<desired version of YAMLs>`
- For the previous environment (The output will be generated in `infrastructure/k8s/common/kafka-operator-system/`):
    - `make copy-built-yamls-legacy VER=<desired version of YAMLs>`


e.g. `make copy-built-yamls VER=0.2` will compile the CRDs, deploy, webhook, and other manifests and place them
in `infrastructure/k8s/common/kafka-operator/kafka-operator-0.2/kafka-operator.yaml`. These manifests in `common` 
may then be referenced on a per-environment basis from the respective folders.

## Debugging

The operator puts fairly verbose logs into the generated pod in `kafka-operator-system` - `kubectl logs -n kafka-operator-system kafka-operator-controller-manager-XXXXX-XXXX manager` will go a long way. For local development, the deployment also ships with the Bitnami Kafka container. While this does contain a full Kafka setup, it also contains all the scripts like `kafka-acls.sh` and other tools needed to connect to the main running Kafka instance in-cluster.

## Testing
For OSX users: be aware that the homebrew install of kubebuilder does not include the appropriate binaries. Install kubebuilder manually instead.

## Development

Please see the following documents:
- [development on local](./development_on_local.md)
- [development on ec2](./development_on_ec2.md)

## Admin tools
Please see the following documents:
- [Kafka Admin Tools](./kafka_admin_tools.md)
