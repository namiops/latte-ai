# Sample sink binding connected to a cron job

This sample creates a cron job that will periodically send events to one of the following lambdas:

- LAB2: https://minimal-service-sample-serverless-lambda.agora-lab.w3n.io
- DEV2: https://minimal-service-sample-serverless-lambda.agora-dev.w3n.io
- LAB: https://minimal-service-sample-serverless-lambda.agora-lab.woven-planet.tech
- DEV: https://minimal-service-sample-serverless-lambda.cityos-dev.woven-planet.tech

## What does it do?

A sink binding adds a `K_SINK` environment variable to a source pod (cron job in this case).
This variable will contain an HTTP URL to a sink, which can be a serverless service (lambda) or a broker.

Then it's a source application responsibility to send HTTP requests or better yet [cloud events](https://cloudevents.io/) to a sink, `heartbeats` image used in this sample knows how to do that.

Note: a sink binding source can be any of the following:
- Deployment
- Job (notice that job is created by CronJob with randomized name, hence label selector is needed)
- DaemonSet
- StatefulSet
- Serverless service: in this case you can simply pass a bazel reference as a `source` parameter ([ref](https://github.com/wp-wcm/city/blob/main/ns/serverless/ytt/serverless_sink_binding_ytt.bzl)).