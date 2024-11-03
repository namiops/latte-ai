# IoTA serverless source sample

This is a sample deployment of the IoTA serverless source. It forwards logs from `test` tenant to [minimal-service-sample](https://github.com/wp-wcm/city/tree/main/infrastructure/k8s/common/serverless/demo/minimal-service-sample):

You can send logs using [device logging sample](https://github.com/wp-wcm/city/tree/main/ns/iot/demo/devicelog).

Endpoints to see incoming log events:

- LAB2: https://minimal-service-sample-serverless-lambda.agora-lab.w3n.io
- DEV2: https://minimal-service-sample-serverless-lambda.agora-dev.w3n.io
- LAB: https://minimal-service-sample-serverless-lambda.agora-lab.woven-planet.tech
- DEV: https://minimal-service-sample-serverless-lambda.cityos-dev.woven-planet.tech
