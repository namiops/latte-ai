# kafka-operator-system for common

The directories here are generated with the following commands:

```shell
cd <REPO_ROOT>/ns/kafka-operator
make copy-built-yamls VER=<TARGET_VERSION>
```

## changes

### 0.3.5
- [[6170337505] Remove kafka-operator validation webhook from the local develpment env · Pull Request #20822 · wp-wcm/city](https://github.com/wp-wcm/city/pull/20822)

### 0.3.4
- [refactor(ns/kafka-operator): swap to the new OCI rules by spencer-cramm_stargate · Pull Request #14575 · wp-wcm/city](https://github.com/wp-wcm/city/pull/14575) is merged
  - removed `command` to solve `Error: failed to create containerd task: failed to create shim task: OCI runtime create failed: runc create failed: unable to start container process: exec: "/app/ns/kafka-operator/image.binary": stat /app/ns/kafka-operator/image.binary: not a directory: unknown` 

### 0.3.3
- [Move mTLS resource creation logic from kafka-operator to zebra](https://wovencity.monday.com/boards/3813113014/pulses/4540572940)
    - mTLS resources (AmazonRootCA Secret, Certificate, DestinationRule) creation logic are removed because [zebra will create them instead](https://developer.woven-city.toyota/docs/default/Component/kafka-service/01_quickstart/). 
  
### 0.3.2
- [[CITYPF-764] create mTLS related resources from kafka-operator - Jira](https://jira.tri-ad.tech/browse/CITYPF-764)
  - The new attribute `clientCertificateReady` is added to the status

### 0.3.1
- [[CITYPF-480] Support other topic formats for Kafka in the Operator, e.g. compaction, retention time - Jira](https://jira.tri-ad.tech/browse/CITYPF-480)
  - The new attributes `cleanupPolicy,maxMessageSize,retentionPeriod` are added to the spec

### 0.3.0
- CityOsKafka `v1alpha3` is released

### 0.1.0
- CityOsKafka `v1alpha1` is released
