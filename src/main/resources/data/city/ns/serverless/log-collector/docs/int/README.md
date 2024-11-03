# Log Collector

The purpose of `log-collector` is to propagate user logs to Agora.

For details, see the [external documentation](https://developer.woven-city.toyota/docs/default/component/telemetry-collector).

## Debugging locally
Alternatively, to see the log output that will be pushed to Loki, you may start `log-collector` locally.

### Running with VSCode locally
1. Add this configuration to the `launch.json` file
```
    {
        "name": "Log collector",
        "type": "go",
        "request": "launch",
        "mode": "debug",
        "program": "serverless/log-collector/main.go",
        "env": {
            "LOGS_WRITE_URL":"http://localhost:3100/loki/api/v1/push",
            "KAFKA_TOPIC": "iot.telemetry",
            "KAFKA_SERVERS":"",
        }
    }
```
2. Forward the Loki pod to `localhost:3100`.
```bash
kubectx lab
kubectl port-forward loki-write-0 3100:3100 -n logging
```

3. [optional] To test `/otel/v1/metrics`, Kafka should be setup. Follow the below steps:
```bash
minikube start
kubectl context minikube
kubectl apply -k <repo>/infrastructure/k8s/local/notification/kafka
KAFKA_PORT=`kubectl get service cityos-kafka-kafka-external-bootstrap -o=jsonpath='{.spec.ports[0].nodePort}{"\n"}' -n kafka`
KAFKA_HOST=`minikube ip`
echo $KAFKA_HOST:$KAFKA_PORT
```
Set `KAFKA_SERVERS` environment variable to `echo $KAFKA_HOST:$KAFKA_PORT` in the configuration.
To tail Kafka messages published to the Kafka topic:
```bash
kubectl exec -it cityos-kafka-kafka-0 -n kafka -- ./bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic iot-telemetry --from-beginning
```


Then you can run the log collector from the `Run and Debug` panel.

### Running with bazel

#### 1. Forward the Loki pod to localhost:3100
```bash
kubectx lab
kubectl port-forward loki-write-0 3100:3100 -n logging
```

#### 2. Run with bazel while passing in the ENV
```bash
LOGS_WRITE_URL=http://localhost:3100/loki/api/v1/push \
bazel run //ns/serverless/log-collector:binary
```

Leaving `LOGS_WRITE_URL` empty will force `log-collector` to write the output to `stdout` instead.

### Switching backend

`log-collector` is capable of operating in both legacy and next-gen clusters.
To switch to next-gen mode, use `NEXT_GEN=True` ENV. For example:

```bash
NEXT_GEN=True \
bazel run //ns/serverless/log-collector:binary
```

(PoC) ADOT backend:

```bash
kubectl -n serverless port-forward svc/adot 4318:4318
```

```bash
LOGS_WRITE_URL=http://localhost:4318/v1/logs \
TRACES_WRITE_URL=http://localhost:4318/v1/traces \
AWS_ADOT=True \
bazel run //ns/serverless/log-collector:binary
```

### Testing

Get a timestamp (Linux):
```bash
LOG_TIMESTAMP=$(date +%s%N)
```

Get a timestamp (Mac):
```bash
LOG_TIMESTAMP=$(gdate +%s%N)
```

Make a request:
```bash
curl --location '<collector_url>' \
--header 'Content-Type: application/json' \
--data '[
    {
        "level": "debug",
        "log": "testing debug level",
        "log_attributes": {
            "caller": "my-robot/client.go:77"
        },
        "timestamp": "'$LOG_TIMESTAMP'"
    },
    {
        "level": "info",
        "log": "testing info level",
        "log_attributes": {
            "caller": "my-robot/client.go:78"
        },
        "timestamp": "'$LOG_TIMESTAMP'"
    }
]'
```
