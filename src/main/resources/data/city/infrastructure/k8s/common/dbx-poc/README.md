# DBX PoC Sample Workload

## How to connect to Databricks Kafka

1. Annotate your Kubernetes ServiceAccount to assume `arn:aws:iam::730335515733:role/agora_msk_client` ([ref](https://github.com/wp-wcm/city/blob/main/infrastructure/k8s/common/dbx-poc/base/service-account.yaml)).
2. Create ServiceEntry to allow accessing external resources ([ref](https://github.com/wp-wcm/city/blob/main/infrastructure/k8s/common/dbx-poc/base/service-entry.yaml)).
3. Make changes to your application to work with MSK IAM Authentication.
    1. For JVM lang, add aws-msk-iam-auth-1.1.9-all.jar ([Github Release](https://github.com/aws/aws-msk-iam-auth/releases/tag/v1.1.9), [Maven](https://repo1.maven.org/maven2/software/amazon/msk/aws-msk-iam-auth/1.1.9/)) to your dependency and [configure the client properties](https://github.com/wp-wcm/city/blob/main/infrastructure/k8s/common/dbx-poc/kafka-admin-cli/client.properties)
    2. For non-JVM lang, hook in [the AWS auth handler of your lang choice](https://github.com/search?q=org%3Aaws+aws-msk-iam-sasl-signer&type=repositories) and use it in your code ([nodejs client sample](https://github.com/wp-wcm/city/blob/9121025/ns/dbx-poc/nodejs-sample/main.js#L35)). Currently, there are 4 languages available (Python/Go/.NET/Node).
4. Use `b-1.dbxpockafkacluster.l8ccg2.c4.kafka.us-west-2.amazonaws.com:9098,b-2.dbxpockafkacluster.l8ccg2.c4.kafka.us-west-2.amazonaws.com:9098,b-3.dbxpockafkacluster.l8ccg2.c4.kafka.us-west-2.amazonaws.com:9098` for `KAFKA_BOOTSTRAP_SERVER`.

## References

- https://docs.aws.amazon.com/msk/latest/developerguide/iam-access-control.html#:~:text=Using%20the%20MSK%20custom%20AWS_MSK_IAM%20mechanism%20to%20configure%20IAM
