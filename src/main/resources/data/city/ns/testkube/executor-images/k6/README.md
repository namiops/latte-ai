# Testkube K6 Executor image

The executor image can be used for running K6 JavaScript files.
The executor entrypoint requires passing the contents of the JavaScript file via the 1st command line argument.

:warning: ensure the test targets (SUT) are configured appropriately with resource utilization limits. Monitor closely during the test runs.

Usage:

```sh
docker run agora-k6-image '<k6_js_script>' [k6_flags]
```

The following is a complete example of how to build the executor image locally and run it with docker:

- Build and load the image locally

```sh
$ bazel run //ns/testkube/executor-images/k6:agora_k6_image.load
Another command (pid=3920610) is running. Waiting for it to complete on the server (server_pid=3637874)...
INFO: Analyzed target //ns/testkube/executor-images/k6:agora_k6_image.load (0 packages loaded, 4 targets configured).
INFO: Found 1 target...
Target //ns/testkube/executor-images/k6:agora_k6_image.load up-to-date:
  bazel-bin/ns/testkube/executor-images/k6/agora_k6_image.load/tarball.tar
INFO: Elapsed time: 2.055s, Critical Path: 1.56s
INFO: 3 processes: 2 internal, 1 linux-sandbox.
INFO: Build completed successfully, 3 total actions
INFO: Running command line: bazel-bin/ns/testkube/executor-images/k6/agora_k6_image.load.sh
The image ns/testkube/executor-images/k6:agora_k6_image already exists, renaming the old one with ID sha256:e80967e531064db98e7dad0a89edccb1cf1cfbd086fba4b2a15f184cab0d778c to empty string
Loaded image: ns/testkube/executor-images/k6:agora_k6_image
```

- Run the docker image locally

```sh
$ docker run ns/testkube/executor-images/k6:agora_k6_image "export default function () {}" --verbose
iqqq: The istio quitquitquit process is disabled
k6 v0.52.0 (commit/20f8febb5b, go1.22.4, linux/amd64)
Reading script from argument
+ exec k6 run script.js --verbose
time="2024-07-23T04:43:03Z" level=debug msg="Logger format: TEXT"
time="2024-07-23T04:43:03Z" level=debug msg="k6 version: v0.52.0 (commit/20f8febb5b, go1.22.4, linux/amd64)"

          /\      |‾‾| /‾‾/   /‾‾/   
     /\  /  \     |  |/  /   /  /    
    /  \/    \    |     (   /   ‾‾\  
   /          \   |  |\  \ |  (‾)  | 
  / __________ \  |__| \__\ \_____/ .io

time="2024-07-23T04:43:03Z" level=debug msg="Resolving and reading test 'script.js'..."
time="2024-07-23T04:43:03Z" level=debug msg=Loading... moduleSpecifier="file:///home/nonroot/script.js" originalModuleSpecifier=script.js
time="2024-07-23T04:43:03Z" level=debug msg="'script.js' resolved to 'file:///home/nonroot/script.js' and successfully loaded 30 bytes!"
time="2024-07-23T04:43:03Z" level=debug msg="Gathering k6 runtime options..."
time="2024-07-23T04:43:03Z" level=debug msg="Initializing k6 runner for 'script.js' (file:///home/nonroot/script.js)..."
time="2024-07-23T04:43:03Z" level=debug msg="Detecting test type for..." test_path="file:///home/nonroot/script.js"
time="2024-07-23T04:43:03Z" level=debug msg="Trying to load as a JS test..." test_path="file:///home/nonroot/script.js"
time="2024-07-23T04:43:03Z" level=debug msg="Babel: Transformed" t=18.307191ms
time="2024-07-23T04:43:03Z" level=debug msg="Runner successfully initialized!"
time="2024-07-23T04:43:03Z" level=debug msg="Parsing CLI flags..."
time="2024-07-23T04:43:03Z" level=debug msg="Consolidating config layers..."
time="2024-07-23T04:43:03Z" level=debug msg="Parsing thresholds and validating config..."
time="2024-07-23T04:43:03Z" level=debug msg="Initializing the execution scheduler..."
time="2024-07-23T04:43:03Z" level=debug msg="Starting 2 outputs..." component=output-manager
time="2024-07-23T04:43:03Z" level=debug msg=Starting... component=metrics-engine-ingester
time="2024-07-23T04:43:03Z" level=debug msg="Started!" component=metrics-engine-ingester
     execution: local
        script: script.js
        output: -

     scenarios: (100.00%) 1 scenario, 1 max VUs, 10m30s max duration (incl. graceful stop):
              * default: 1 iterations for each of 1 VUs (maxDuration: 10m0s, gracefulStop: 30s)

time="2024-07-23T04:43:03Z" level=debug msg="Starting the REST API server on localhost:6565"
time="2024-07-23T04:43:03Z" level=debug msg="Trapping interrupt signals so k6 can handle them gracefully..."
time="2024-07-23T04:43:03Z" level=debug msg="Starting emission of VUs and VUsMax metrics..."
time="2024-07-23T04:43:03Z" level=debug msg="Start of initialization" executorsCount=1 neededVUs=1 phase=execution-scheduler-init
time="2024-07-23T04:43:03Z" level=debug msg="Initialized VU #1" phase=execution-scheduler-init
time="2024-07-23T04:43:03Z" level=debug msg="Finished initializing needed VUs, start initializing executors..." phase=execution-scheduler-init
time="2024-07-23T04:43:03Z" level=debug msg="Initialized executor default" phase=execution-scheduler-init
time="2024-07-23T04:43:03Z" level=debug msg="Initialization completed" phase=execution-scheduler-init
time="2024-07-23T04:43:03Z" level=debug msg="Start of test run" executorsCount=1 phase=execution-scheduler-run
time="2024-07-23T04:43:03Z" level=debug msg="setup() is not defined or not exported, skipping!"
time="2024-07-23T04:43:03Z" level=debug msg="Start all executors..." phase=execution-scheduler-run
time="2024-07-23T04:43:03Z" level=debug msg="Starting executor" executor=default startTime=0s type=per-vu-iterations
time="2024-07-23T04:43:03Z" level=debug msg="Starting executor run..." executor=per-vu-iterations iterations=1 maxDuration=10m0s scenario=default type=per-vu-iterations vus=1
time="2024-07-23T04:43:03Z" level=debug msg="Regular duration is done, waiting for iterations to gracefully finish" executor=per-vu-iterations gracefulStop=30s scenario=default
time="2024-07-23T04:43:03Z" level=debug msg="Executor finished successfully" executor=default startTime=0s type=per-vu-iterations
time="2024-07-23T04:43:03Z" level=debug msg="teardown() is not defined or not exported, skipping!"
time="2024-07-23T04:43:03Z" level=debug msg="Test finished cleanly"
time="2024-07-23T04:43:03Z" level=debug msg="Stopping vus and vux_max metrics emission..." phase=execution-scheduler-init
time="2024-07-23T04:43:03Z" level=debug msg="Metrics emission of VUs and VUsMax metrics stopped"
time="2024-07-23T04:43:03Z" level=debug msg="Releasing signal trap..."
time="2024-07-23T04:43:03Z" level=debug msg="Waiting for metrics and traces processing to finish..."
time="2024-07-23T04:43:03Z" level=debug msg="Sending usage report..."
time="2024-07-23T04:43:03Z" level=debug msg="Metrics and traces processing finished!"
time="2024-07-23T04:43:03Z" level=debug msg="Stopping outputs..."
time="2024-07-23T04:43:03Z" level=debug msg="Stopping 2 outputs..." component=output-manager
time="2024-07-23T04:43:03Z" level=debug msg=Stopping... component=metrics-engine-ingester
time="2024-07-23T04:43:03Z" level=debug msg="Stopped!" component=metrics-engine-ingester
time="2024-07-23T04:43:03Z" level=debug msg="Generating the end-of-test summary..."

     data_received........: 0 B 0 B/s
     data_sent............: 0 B 0 B/s
     iteration_duration...: avg=3.08µs min=3.08µs med=3.08µs max=3.08µs p(90)=3.08µs p(95)=3.08µs
     iterations...........: 1   3928.794528/s


running (00m00.0s), 0/1 VUs, 1 complete and 0 interrupted iterations
default ✓ [ 100% ] 1 VUs  00m00.0s/10m0s  1/1 iters, 1 per VU
time="2024-07-23T04:43:04Z" level=debug msg="Usage report sent successfully"
time="2024-07-23T04:43:04Z" level=debug msg="Everything has finished, exiting k6 normally!"
```

To run the docker image with Testkube, the k6 `Executor` [manifest](../../../../infrastructure/k8s/common/testkube/executors/executor-agora-k6.yaml) is already deployed to the clusters. Refer to the [example](./example.yaml) manifest file to configure Testkube `Test` resources.
