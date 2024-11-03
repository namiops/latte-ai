# Integration / end to end testing

If you need to write e2e test for your services, these are some guidelines that we recommend.

1. Add the `requires-network` tag: If your test requires network access, ensure that you add 
the `requires-network` tag to the test rule in the Bazel BUILD file. This tag indicates that 
the test needs network connectivity to run successfully. This apply if your test are accessing
container, because the test are running inside Bazel sandbox and container are running in the
host machine. Connectivity from sandbox to host is considered as network access.
1. If your test requires to run containers (postgres, redis, kafka, etc), run container on an 
unallocated port to avoid port conflicts and ensure that your tests run smoothly. This is important 
because the test will run in parallel with other test and will fail if the test are trying to run a 
container in the same port. Most of the test library supporting this features  
1. If running container(s) and must connect to them via localhost, you must add the constraint 
`//ns/bazel_docker/toolchain:dind_test` on `exec_compatible_with` field for your test target. 
This is a special constraint that allows us to provide the proper remote execution properties 
to enable a docker-in-docker environment for your test. By default this is disabled for all 
targets, as it is costly and ineffecient. However, sometimes there is massive benefit to being
able to run containers as a dind, so this enable that.

Note: We will may stop running tests that set `size = "large"` or  `size = "enormous"` 
unannounced. There is rarely a strong reason to have such slow and time consuming tests. If your
test is starting to fall under this category, you should start to consider how you can improve 
its speed. Usually the slowest part with tests is the setup of big tools such as container images.
Consider making the setup run one time, and use the same containers for all tests. If you must
spawn separate containers, then split your code, and create separate bazel test targets. This
will allow bazel to execute the exclusive tests in parallel, and can have drastic performance 
gains for your tests.

If your tests check all above point, your test target would look like this
```
*_test(
    name = "my_test",
    srcs = ["..."],
    tags = ["requires-network"],
    exec_compatible_with = ["//ns/bazel_docker/toolchain:dind_test"],
)
```
