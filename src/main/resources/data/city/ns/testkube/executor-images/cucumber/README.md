# (PoC) BDD test execution with cucumber (PoC)

- Build an executor image with the macro `cucumber_jvm_image`. See the [example directory](./example/BUILD).

- Load and run the Docker image:

```sh
$ bazel run //ns/testkube/executor-images/cucumber/example/helloworld:example_test.load
INFO: Analyzed target //ns/testkube/executor-images/cucumber/example/helloworld:example_test.load (198 packages loaded, 10522 targets configured).
INFO: Found 1 target...
Target //ns/testkube/executor-images/cucumber/example/helloworld:example_test.load up-to-date:
  bazel-bin/ns/testkube/executor-images/cucumber/example/helloworld/example_test.load/tarball.tar
INFO: Elapsed time: 1.246s, Critical Path: 0.01s
INFO: 1 process: 1 internal.
INFO: Build completed successfully, 1 total action
INFO: Running command line: bazel-bin/ns/testkube/executor-images/cucumber/example/helloworld/example_test.load.sh
Loaded image: ns/testkube/executor-images/cucumber/example/helloworld:example_test
```

```sh
$ docker run ns/testkube/executor-images/cucumber/example/helloworld:example_test

Scenario: A Test That Always Passes          # ns/testkube/executor-images/cucumber/example/helloworld/hello_world.feature:2
  Given I have a working hello world example # global.woven_city.cucumber.helloworld.HelloWorldSteps.i_have_a_working_hello_world_example()
  When I run the hello world example         # global.woven_city.cucumber.helloworld.HelloWorldSteps.i_run_the_hello_world_example()
  Then I should see "Hello" on the console   # global.woven_city.cucumber.helloworld.HelloWorldSteps.i_should_see_on_the_console(java.lang.String)

1 Scenarios (1 passed)
3 Steps (3 passed)
0m0.172s
```

- Alternatively, use the suffix `*_bin` to run the generated java binary:

```sh
$ bazel run //ns/testkube/executor-images/cucumber/example/helloworld:example_test_bin 
INFO: Analyzed target //ns/testkube/executor-images/cucumber/example/helloworld:example_test_bin (0 packages loaded, 0 targets configured).
INFO: Found 1 target...
Target //ns/testkube/executor-images/cucumber/example/helloworld:example_test_bin up-to-date:
  bazel-bin/ns/testkube/executor-images/cucumber/example/helloworld/example_test_bin
  bazel-bin/ns/testkube/executor-images/cucumber/example/helloworld/example_test_bin.jar
INFO: Elapsed time: 0.574s, Critical Path: 0.37s
INFO: 2 processes: 1 internal, 1 worker.
INFO: Build completed successfully, 2 total actions
INFO: Running command line: bazel-bin/ns/testkube/executor-images/cucumber/example/helloworld/example_test_bin --glue global.woven_city.cucumber.helloworld --plugin pretty --object-factory org.citrusframework.cucumber.backend.CitrusObjectFactory --glue global.woven_city.cucumber.common ns/testkube/executor-images/cucumber/example/helloworld

Scenario: A Test That Always Passes          # ns/testkube/executor-images/cucumber/example/helloworld/hello_world.feature:2
  Given I have a working hello world example # global.woven_city.cucumber.helloworld.HelloWorldSteps.i_have_a_working_hello_world_example()
  When I run the hello world example         # global.woven_city.cucumber.helloworld.HelloWorldSteps.i_run_the_hello_world_example()
  Then I should see "Hello" on the console   # global.woven_city.cucumber.helloworld.HelloWorldSteps.i_should_see_on_the_console(java.lang.String)

1 Scenarios (1 passed)
3 Steps (3 passed)
0m0.172s
```
