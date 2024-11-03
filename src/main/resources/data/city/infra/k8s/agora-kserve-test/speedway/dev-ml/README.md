# Sample KServe Application: Test Cases

## General helpful commands

- List sample Inference Services

```bash
$ kubectl get isvc -n agora-kserve-test-dev
NAME            URL   READY     PREV   LATEST   PREVROLLEDOUTREVISION   LATESTREADYREVISION             AGE
sklearn-iris          Unknown          100                              sklearn-iris-predictor-00004    24h
pytorch-mnist         Unknown          100                              pytorch-mnist-predictor-00001   62m
```

- List Revisions to check if KServe app pods are ready.

```bash
$ kubectl get revision -n agora-kserve-test-dev
```

- Get routes

```bash
$ kubectl get httproutes -n agora-kserve-test-dev
NAME                                                                    HOSTNAMES                                                                                                                                                                 AGE
sklearn-iris-predictor.agora-kserve-test-dev.svc.cluster.local          ["sklearn-iris-predictor.agora-kserve-test-dev","sklearn-iris-predictor.agora-kserve-test-dev.svc","sklearn-iris-predictor.agora-kserve-test-dev.svc.cluster.local"]      24h
sklearn-iris-predictor-agora-kserve-test-dev-lambda.agora-dev.w3n.io    ["sklearn-iris-predictor-agora-kserve-test-dev-lambda.agora-dev.w3n.io"]                                                                                                  24h
pytorch-mnist-predictor-agora-kserve-test-dev-lambda.agora-dev.w3n.io   ["pytorch-mnist-predictor-agora-kserve-test-dev-lambda.agora-dev.w3n.io"]                                                                                                 62m
pytorch-mnist-predictor.agora-kserve-test-dev.svc.cluster.local         ["pytorch-mnist-predictor.agora-kserve-test-dev","pytorch-mnist-predictor.agora-kserve-test-dev.svc","pytorch-mnist-predictor.agora-kserve-test-dev.svc.cluster.local"]   62m
```

**NOTE:** We should verify the status of these HTTPRoutes if they are accepted by the local Gateway.

## sklearn-iris

Sample base manifest: "common/kserve-sample/inference-service/sklearn-iris-0.1.0/inferenceservice-sklearn-iris.yaml"

### 1. Healthcheck

```bash
export LOCAL_GW_HOST="knative-local-gateway.agora-knative-serving-dev.svc.cluster.local"
export KSERVE_APP_HOST="sklearn-iris-predictor.agora-kserve-test-dev"
curl ${LOCAL_GW_HOST} -H "Host: ${KSERVE_APP_HOST}"
```

Expect result: `{"status":"alive"}`

### 2. Inference

```bash
export LOCAL_GW_HOST="knative-local-gateway.agora-knative-serving-dev.svc.cluster.local"
export KSERVE_APP_HOST="sklearn-iris-predictor.agora-kserve-test-dev"

cat > /tmp/iris.json <<EOF
{
  "instances": [
    [6.8,  2.8,  4.8,  1.4],
    [6.0,  3.4,  4.5,  1.6]
  ]
}
EOF
curl -X POST -H "Host: ${KSERVE_APP_HOST}" \
  -d @/tmp/iris.json -H "Content-Type: application/json" \
  http://$LOCAL_GW_HOST/v1/models/sklearn-iris:predict 
```

Expected result: `{"predictions":[1,1]}`

## pytorch-mnist

Sample base manifest: "common/kserve-sample/inference-service/pytorch-mnist-0.1.0/inferenceservice-pytorch-mnist.yaml"

### 1. Healthcheck

```bash
export LOCAL_GW_HOST="knative-local-gateway.agora-knative-serving-dev.svc.cluster.local"
export KSERVE_APP_HOST="pytorch-mnist-predictor.agora-kserve-test-dev"
curl ${LOCAL_GW_HOST} -H "Host: ${KSERVE_APP_HOST}"
```

Expect result: `{"status":"alive"}`

### 2. Inference

```bash
export LOCAL_GW_HOST="knative-local-gateway.agora-knative-serving-dev.svc.cluster.local"
export KSERVE_APP_HOST="pytorch-mnist-predictor.agora-kserve-test-dev"

cat > /tmp/mnist.json <<EOF
{
  "instances": [
    {
      "data": "iVBORw0KGgoAAAANSUhEUgAAABwAAAAcCAAAAABXZoBIAAAAw0lEQVR4nGNgGFggVVj4/y8Q2GOR83n+58/fP0DwcSqmpNN7oOTJw6f+/H2pjUU2JCSEk0EWqN0cl828e/FIxvz9/9cCh1zS5z9/G9mwyzl/+PNnKQ45nyNAr9ThMHQ/UG4tDofuB4bQIhz6fIBenMWJQ+7Vn7+zeLCbKXv6z59NOPQVgsIcW4QA9YFi6wNQLrKwsBebW/68DJ388Nun5XFocrqvIFH59+XhBAxThTfeB0r+vP/QHbuDCgr2JmOXoSsAAKK7bU3vISS4AAAAAElFTkSuQmCC",
      "target": 0
    }
  ]
}
EOF
curl -X POST -H "Host: ${KSERVE_APP_HOST}" \
  -d @/tmp/mnist.json -H "Content-Type: application/json" \
  http://$LOCAL_GW_HOST/v1/models/mnist:predict
```

Expected result: `{"predictions":[[2]]}`
