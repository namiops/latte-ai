# Guidelines for maintaining Agora KNative Eventing

This document describes guidelines for maintaining KNative Eventing (namespace `knative-eventing` and `knative-sources`) in Agora.
The target audience is the Infra team.

## Deploy KNative Eventing

### knative-eventing namespace

There are 5 main components:

- [eventing](https://github.com/knative/eventing)
- [in-memory-channel](https://github.com/knative/eventing)
- [mt-channel-broker](https://github.com/knative/eventing)
- [kafka-controller](https://github.com/knative-extensions/eventing-kafka-broker)
- [kafka-broker](https://github.com/knative-extensions/eventing-kafka-broker)

**NOTE:** Note that KNative Eventing CRDs are put together inside a subfolder at `eventing-<version>-<agora-revision>/crds`.
When you deploy KNative Eventing to a new environment, do not forget to include CRDs from the subfolder.
For example, the resource list in `kustomization.yaml` should be like:

```yaml
  - ../../common/knative-serving/eventing-v1.10.1-agora1/crds
  - ../../common/knative-serving/eventing-v1.10.1-agora1
  - ../../common/knative-serving/in-memory-channel-v1.10.1-agora1/crds
  - ../../common/knative-serving/in-memory-channel-v1.10.1-agora1
  - ../../common/knative-serving/kafka-controller-v1.10.1-agora1/crds
  - ../../common/knative-serving/kafka-controller-v1.10.1-agora1
```

### knative-sources namespace

There is one main component:

- [rabbitmq-source](https://github.com/knative-extensions/eventing-rabbitmq)

**NOTE:** CRD folders should be put above like one in `knative-eventing`` namespace

## Upgrade KNative Eventing

The patch version number of each component is independent. 
This means we can install the same minor versions of core and extension components.
For example, we can install these components together:

- eventing: `v1.10.6`
- kafka-controller: `v1.10.1`
- rabbitmq-source: `v1.10.2`

### knative-eventing namespace

For installing the components, the script is available at [import script](../../../../k8s/common/knative-eventing/bin/import).
You can run the script by the below example to generate each component.

```bash
./import -n eventing -v v1.10.1
./import -n in-memory-channel -v v1.10.1
./import -n mt-channel-broker -v v1.10.1
./import -n kafka-controller -v v1.10.1
./import -n kafka-broker -v v1.10.1
```

### knative-sources namespace

For installing the components, the script is available at [import script](../../../../k8s/common/knative-sources/bin/import).
You can run the script by the below example to generate each component.

```bash
$ ./import -n rabbitmq-source -v v1.10.1
```


## Configuration

### knative-eventing namespace

#### Configmap config-br-default-channel

Specify a default channel for Broker if there is no channel config explicitly set.

#### Configmap config-br-defaults

Specify a default config for Broker such as a broker class and channel's configmap.
We can update this config for enforcing everyone to use Broker with a robust channel like KafkaChannel.
