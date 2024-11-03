# Tests

As your rules grow in complexity, tests will become important.

Aspect's `write_source_file` rule automatically generates a `.copy_test` test target, which will generate the file in Bazel, then diff it against the one already committed to source.

Agora CI automatically runs all Bazel test targets. If the run fails for a `zebra` target due to the `.copy_test` target, the rule will be executed with `bazel run` and the output will be committed to source. 

This behavior can be used to easily create tests for rules.

Add a flag for your test targets to make sure that the `write_source_files` macro is instantiated but `zebra` tag doesn't get added to the output:

```
def ytt_build(name, output, copy_to_source = False, fail_on_diff = False, **kwargs):
    ...
    if copy_to_source:
        if fail_on_diff:
            tags = kwargs.get("tags", default = [])
        else:
            tags = ["zebra"] + kwargs.get("tags", default = [])
        _write_source_files(
            # this (name + .copy) is the run target to execute for the copy-to-source rule
            name = name + ".copy",
            files = {
                output: name,
            },
            diff_test = True,
            tags = tags
        )
```

Create a test folder near your rule and instantiate a target of your rule, plus the expected output:

```BUILD
load("//ns/hoodiestreamer_job:hoodiestreamer_job.bzl", "hoodiestreamer_job")

values_files = [
    "schema.avsc",
    "values.yaml",
]

hoodiestreamer_job(
    name = "hs_s3_ingest",
    copy_to_source = True,
    output = "output.yaml",
    values_files = values_files,
    fail_on_diff = True,
)

```

```values.yaml
apiVersion: sparkoperator.k8s.io/v1beta2
kind: SparkApplication
metadata:
  name: s3
  namespace: hoodiestreamer-test
spec:
  type: Scala
  mode: cluster
  image: docker.artifactory-ha.tri-ad.tech/wcm-cityos/data-platform/spark-hudi-image:main-5637c36a-1699598593
  imagePullPolicy: Always
  mainClass: org.apache.hudi.utilities.deltastreamer.HoodieDeltaStreamer
  mainApplicationFile: local:///opt/spark/jars/processed_hudi-utilities-slim-bundle_2.12-0.14.0.jar
  sparkVersion: 3.4.1
 ...
```

and if the upstream rule changes, the CI run will fail but not execute the `.copy` target that writes the file to the source.
