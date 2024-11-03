# Overview
Integration tests for `log-collector`.

## Development

### Setup

Install Ginkgo: https://onsi.github.io/ginkgo/#installing-ginkgo (optional if not running from bazel)

### Running a test locally

Run via Ginkgo:

```sh
COLLECTOR_ENDPOINT=<ENDPOINT> ginkgo -v
```

Or via Bazel:

```bazel
COLLECTOR_ENDPOINT=<ENDPOINT> bazel run //ns/serverless/log-collector/integration:integration_test
```

Endpoints (for local use):
- LAB:  https://log-collector-serverless-lambda.agora-lab.woven-planet.tech
- LAB2: https://log-collector-serverless-lambda.agora-lab.w3n.io
- DEV:  https://log-collector-serverless-lambda.cityos-dev.woven-planet.tech
- DEV2: https://log-collector-serverless-lambda.agora-dev.w3n.io

### TestKube

These tests are also running on schedule:

- LAB: https://testkube.agora-lab.woven-planet.tech/tests/log-collector-e2e
- LAB2: https://testkube.agora-lab.w3n.io/tests/log-collector-e2e
- DEV: https://testkube.cityos-dev.woven-planet.tech/tests/log-collector-e2e
- DEV2: https://testkube.agora-dev.w3n.io/tests/log-collector-e2e
