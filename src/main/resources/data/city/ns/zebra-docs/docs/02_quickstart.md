# Quickstart

All generation systems built on Zebra should provide their own docs, detailing how to configure and instantiate rules in your own project. For this example, we will use the simple Pod+Service config generation.
This sample is copied from [the official playground of YTT](https://carvel.dev/ytt/)

## Declaring your Pod+Service settings

In a folder in `/ns/example-service`, create the following `values.yaml` that contains the values to pass to the Pod+Service configuration generation.

```yaml
#@data/values
---
echos:
- name: first
- name: second
  port: 8081
  text: "Hello #ytt World on 8081!"
```

And then a `BUILD` file that instantiates the rule, specifies the values file, and declares the desired output.

!!! tip
    You can recognize zebra-generated files easily by prepending `z-` to the filename.

```
load("//ns/zebra-docs/ytt-sample:ytt_sample.bzl", "ytt_sample_build")

values_files = [
    "values.yaml",
]

ytt_sample_build(
    name = "ytt_sample",
    copy_to_source = True,
    output = "z-ytt-sample-output.yaml",
    values_files = values_files,
)

# This filegroup is required, as Bazel's glob operator cannot cross package boundaries.
# Since this BUILD file is needed to instantiate the target under the /infrastructure hierarchy,
# this subfolder becomes a separate package and all files inside are hidden from
# the top-level file glob, and subsequently all validations will fail.
filegroup(
    name = "files",
    srcs = glob(["*.yaml"]),
    visibility = ["//visibility:public"],
)
```

Then, execute the run target (please see the [Outputs](03_outputs.md) section)

```shell
bazel run //ns/example-service:ytt_sample.copy
```

and the resulting `z-ytt-sample-output.yaml` will be generated and copied to the folder containing the BUILD file.

```yaml
% cat ns/example-service/z-ytt-sample-output.yaml
kind: Pod
apiVersion: v1
metadata:
  name: echo-app
  labels:
    app: echo
    org: test
spec:
  containers:
    - name: echo-first
      image: hashicorp/http-echo
      args:
        - -listen=:8080
        - '-text=Hello #ytt World on 8080!'
    - name: echo-second
      image: hashicorp/http-echo
      args:
        - -listen=:8081
        - '-text=Hello #ytt World on 8081!'
---
kind: Service
apiVersion: v1
metadata:
  name: echo-service
spec:
  selector:
    app: echo
    org: test
  ports:
    - name: echo-first
      port: 8080
    - name: echo-second
      port: 8081
```


!!!Note
    You have run the command `bazel run //ns/example-service:ytt_sample.copy` in this tutorial but this command will be executed by GitHub Actions after the pull request is created so it is not necessary to be executed by yourself. 
    

That's it!
You can learn more how to write YTT codes [in the playground](https://carvel.dev/ytt/) or [your local machine](https://carvel.dev/ytt/docs/v0.40.0/install/).  
