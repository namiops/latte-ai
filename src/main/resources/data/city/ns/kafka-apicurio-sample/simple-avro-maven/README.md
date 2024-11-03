# apicurio sample simple-avro-maven

This source is based on [apicurio simple-avro-maven example](https://github.com/Apicurio/apicurio-registry-examples/tree/main/simple-avro-maven)

The changes are followings:
- `pom.xml` is modified for building docker image.
- Split the sample file into producer/consumer file

## How to run

### Deploy kafka, apicurio, (kafka-monitor)

Please set up the following on local. (When you use Tilt, you can skip this step.)

```shell
# deploy cert-manager (repeat the command until no error happens)
kubectl apply -k ../../../infrastructure/k8s/local/cert-manager

# deploy the kafka cluster (repeat the command until no error happens)
kubectl apply -k ../../../infrastructure/k8s/local/kafka

kubectl apply -f k8s/_namespace.yaml
kubectl apply -f k8s/apicurio-local.yaml
```


### Upload artifact in Apicurio UI

First, please create port-forward session (When you use Tilt, you can skip this step.)
```shell
kubectl port-forward -n apicurio deployments/apicurio 8080
```

Then, open the Apicurio UI in your browser(http://localhost:8080/ui/artifacts), and click `Upload artifact` button then set the followings:
 
- Group: `kafka-apicurio-sample`
- ID: `kafka-apicurio-sample.simple-avro-maven-topic`
- Artifact: upload `src/main/resources/schemas/greeting.avsc`

After uploading the schema, you can edit the `Validity rule` and `Compatibility rule`.

### Run the apicurio sample application

The sample source is in
`./src/main/java/io/apicurio/registry/examples/simple/avro/maven/SimpleAvroMavenExample.java`

If you use [skaffold](https://skaffold.dev/), you can automatically deploy the new image just after editing the source code.

You can start `skaffold` with the following command:
```shell
skaffold dev
``` 

Unfortunately, Skaffold did not work when colima is used with the following error...
```shell
(⎈|minikube:default)☁ $ skaffold dev
invalid skaffold config: getting minikube env: running [/opt/homebrew/bin/minikube docker-env --shell none -p minikube --user=skaffold]
 - stdout: "false exit code 14\n"
 - stderr: "X MK_USAGE が原因で終了します: docker-env コマンドは「docker」ランタイムとだけ互換性がありますが、このクラスターは「containerd」ランタイムを使用するよう設定されています。\n"
 - cause: exit status 14
```

You can use Tilt instead:
```shell
tilt up
```


It would be good to test such as uploading new avro schema to apicurio, changing content rules and so on. 

ref: [Introduction to Schema Registry in Kafka | by Amarpreet Singh | Slalom Technology | Medium](https://medium.com/slalom-technology/introduction-to-schema-registry-in-kafka-915ccf06b902)
