# Deploying Redis Clusters

We have tested out a cluster and run a sample app against it, finally let's
discuss how to create your own clusters in Agora.

Agora has adopted a system of configuration abstraction tooling that has been
named Zebra. Zebra is as much a philosophy around code generation vs. operator
usage as it is a practical system used by the Agora team. For details on the
ideas of Zebra you can refer to the [Technical Note](https://docs.google.com/document/d/1WwjguyivZs3-fp0lZbG8pKeTSCnIBZvRCt8AEMg7Vwc/edit#heading=h.5yrph2uavrbf).

You can either follow along with this tutorial from the
**tutorial deployment folder**

```shell
/ns/tutorial/redis-101/deploy/03_zebra_usage
```

or create your own BUILD file anywhere you'd like to create a new redis
cluster.

## Create a BUILD file

In practical terms agora-redis consists of a [bazel](https://bazel.build/) rule
and a [helm](https://helm.sh/) chart. Service teams that wish to add redis can
create or add to their current bazel BUILD file the rule, a filegroup to
instantiate the target, and then use the rule.

```starlark
load("//ns/redis-operator/bazel:redis_build.bzl", "DEFAULT_CHART", "redis_build")

# This filegroup is required, as Bazel's glob operator cannot cross package boundaries.
# Since this BUILD file is needed to instantiate the target under the /infrastructure hierarchy,
# this subfolder becomes a separate package and all files inside are hidden from
# the top-level file glob, and subsequently all validations will fail.
filegroup(
    name = "files",
    srcs = glob(["**/*.yaml"]),
    visibility = ["//visibility:public"],
)

redis_build(
    name = "newapp",
    chart = DEFAULT_CHART,
    namespace = "redis-test",
    values_file = "redis_values.yaml",
)
```

Under this BUILD file definition we would create a file called
redis_values.yaml. Resources under the local cluster are fairly constrained so
let's also limit the requested space to a much lower amount.

```yaml
name: newapp
redis:
  resources:
    requests:
      cpu: 10m
      memory: 30Mi
sentinel:
  resources:
    requests:
      cpu: 10m
      memory: 30Mi
```

When using the Speedway environment, don't forget to set `redisFailoverLabels` as follows to make this work on the Istio.

```yaml
name: test-redis
redisFailoverLabels:
  security.istio.io/tlsMode: istio
```

## Build and Run Bazel targets

With just this BUILD file we can now generate a working cluster. But if you
need more the chart supports affinity, custom annotations, tolerations, custom
configuration and more. Let's now do a little bit of exploration with bazel to
see what that BUILD file changes.

```shell
$ bazel query //ns/tutorial/redis-101/deploy/03_zebra_usage:all --output=label_kind
filegroup rule //ns/tutorial/redis-101/deploy/03_zebra_usage:files
_helm_template rule //ns/tutorial/redis-101/deploy/03_zebra_usage:newapp
_write_source_file rule //ns/tutorial/redis-101/deploy/03_zebra_usage:newapp.copy
fail_with_message_test rule //ns/tutorial/redis-101/deploy/03_zebra_usage:newapp.copy_test
test_suite rule //ns/tutorial/redis-101/deploy/03_zebra_usage:newapp.copy_tests
_kubectl_slice rule //ns/tutorial/redis-101/deploy/03_zebra_usage:newapp.kubectl_slice
_kustomize_create rule //ns/tutorial/redis-101/deploy/03_zebra_usage:newapp.kustomize_create
_yq_rule rule //ns/tutorial/redis-101/deploy/03_zebra_usage:newapp.yq
Loading: 0 packages loaded
```

Now that we have seen our targets let's actually try building them.

```shell
$ bazel build //ns/tutorial/redis-101/deploy/03_zebra_usage:all
INFO: Analyzed 7 targets (1 packages loaded, 8 targets configured).
INFO: Found 7 targets...
INFO: From Creating the kustomization file...:
Wrote bazel-out/k8-fastbuild/bin/ns/tutorial/redis-101/deploy/03_zebra_usage/newapp.kubectl_slice.output/service-newapp-redis-headless.yaml -- 343 bytes.
Wrote bazel-out/k8-fastbuild/bin/ns/tutorial/redis-101/deploy/03_zebra_usage/newapp.kubectl_slice.output/service-newapp-sentinel-headless.yaml -- 330 bytes.
Wrote bazel-out/k8-fastbuild/bin/ns/tutorial/redis-101/deploy/03_zebra_usage/newapp.kubectl_slice.output/redisfailover-newapp-redis.yaml -- 583 bytes.
3 files generated.
INFO: Elapsed time: 0.448s, Critical Path: 0.15s
INFO: 5 processes: 1 internal, 4 linux-sandbox.
INFO: Build completed successfully, 5 total actions
```

If you are actually following along with this tutorial rather than running it
out of the deployment folder you will notice something strange here. We have no
files! This is because the build process will not generate the new files,
running the .copy target is what will actually copy the files back into the
source tree from bazel's build cache. Let's run it next:

```shell
$ bazel run //ns/tutorial/redis-101/deploy/03_zebra_usage:newapp.copy
INFO: Analyzed target //ns/tutorial/redis-101/deploy/03_zebra_usage:newapp.copy (133 packages loaded, 450 targets configured).
INFO: Found 1 target...
Target //ns/tutorial/redis-101/deploy/03_zebra_usage:newapp.copy up-to-date:
  bazel-bin/ns/tutorial/redis-101/deploy/03_zebra_usage/newapp.copy_update.sh
INFO: Elapsed time: 0.625s, Critical Path: 0.02s
INFO: 1 process: 1 internal.
INFO: Build completed successfully, 1 total action
INFO: Running command line: bazel-bin/ns/tutorial/redis-101/deploy/03_zebra_usage/newapp.copy_update.sh
Copying directory /home/user/.cache/bazel/_bazel_user/104a733010cf04a1f7d133abe6aca840/execroot/wp_wcm_city/bazel-out/k8-fastbuild/bin/ns/tutorial/redis-101/deploy/03_zebra_usage/newapp.copy_update.sh.runfiles/wp_wcm_city/ns/tutorial/redis-101/deploy/03_zebra_usage/newapp.kustomize_create.output to ns/tutorial/redis-101/deploy/03_zebra_usage/out/newapp in /home/user/workspace/cityos
```

Now after running the copy command we can see the out/newapp folder in the
original folder with the BUILD file in it. You should see 4 files

```shell
$ ls out/newapp/
kustomization.yaml 
redisfailover-newapp-redis.yaml 
service-newapp-redis-headless.yaml 
service-newapp-sentinel-headless.yaml
```

## Running the generated cluster

Let's verify that this cluster actually works the way we want. First we will
need a namespace for these files (although they have already been written with
the namespace included). First lets make _namespace.yaml

```yaml
---
apiVersion: v1
kind: Namespace
metadata:
  name: redis-test
  labels:
    name: redis-test
    istio.io/rev: default
```

And then let's refer to our namespace and cluster with a kustomization

```yaml
apiVersion: kustomize.config.k8s.io/v1beta1
kind: Kustomization
resources:
- _namespace.yaml
- out/newapp
```

And finally deploy this cluster

```shell
$ kubectl apply -k .
namespace/redis-test created
service/newapp-redis-headless created
service/newapp-sentinel-headless created
redisfailover.databases.spotahome.com/newapp-redis created

$ kubectl get pods -n redis-test
NAME                                READY   STATUS    RESTARTS   AGE
rfr-newapp-redis-0                  2/2     Running   0          55s
rfr-newapp-redis-1                  2/2     Running   0          54s
rfr-newapp-redis-2                  2/2     Running   0          54s
rfs-newapp-redis-7f49b58564-b9vjg   2/2     Running   0          55s
rfs-newapp-redis-7f49b58564-rs6mq   2/2     Running   0          54s
rfs-newapp-redis-7f49b58564-wc2mz   2/2     Running   0          54s
```

And that's all you need! We can reference this generated folder in a
kustomization and it will be deployed along with the rest of your service
giving you an easily set up Redis cluster.

## Teardown

```shell
$ kubectl delete -k .
namespace "redis-test" deleted
service "newapp-redis-headless" deleted
service "newapp-sentinel-headless" deleted
redisfailover.databases.spotahome.com "newapp-redis" deleted
```

For more details on the agora-redis helm chart see the [Redis Helm Chart README](https://github.com/wp-wcm/city/blob/main/infrastructure/helm/agora-redis/README.md)
