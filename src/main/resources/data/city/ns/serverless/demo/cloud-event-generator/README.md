## Generating cloud events
You can quickly generate cloud events with cloud-event-generator

Usage:
```bash
bazel run //ns/serverless/demo/cloud-event-generator:binary -- --target=<TARGET_ENDPOINT> --data=<JSON_DATA>
```

Example:
```bash
bazel run //ns/serverless/demo/cloud-event-generator:binary -- --target="https://minimal-service-sample-serverless-lambda.agora-lab.woven-planet.tech" --data='{"id":1,"message": "test"}'
```