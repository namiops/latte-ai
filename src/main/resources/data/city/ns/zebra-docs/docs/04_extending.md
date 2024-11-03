# Extending Zebra 

Zebra is more a collection of philosophies and tools than an actual piece of software, making extending it to suit additional generation requirements easy.

## Writing your own generation 

Using the Pod+Service template generation as a starting point, the following components are required:

* toolchain for the software required to generate
* a rule for executing the generation
* (optional) macros to encapsulate logic and provide a simpler API for consumers to use the generation rules

## Toolchain

Bazel can only use software that has been provided to it, and toolchains are a useful abstraction to this rule. Using binaries is possible, but requires specifications for every platform that Bazel will execute on, a link to the binary for each platform, and the SHA hash to use as a checksum. Registering the repository where the binary can be retrieved from is also required.

All toolchains are registered with Bazel to allow other rules to consume it.

Please see //ns/bazel_ytt/toolchain for an example.

## Execution rule

This is the core of the logic for your generation routine.

The rule declares an implementation and attributes, which are then accessible to the execution context of the implementation. All required toolchains are also declared.

Writing the rule implementation itself depends on the software you are using, but it essentially constructs the arguments to the command and executes it. Note that all inputs and outputs must be specified, likely using `ctx.actions.declare_file` and attrs (which will be passed as arguments when the rule is instantiated); this is so that subsequent build steps in the chain know what to expect and that all builds are reproducible.

## Macros

Macros in Bazel are largely syntactic sugar and do not provide additional functionality than could be written into a rule. These are extremely useful when providing a subset of functionality as an API to a developer, as arguments can be hardcoded in a macro that calls a subsequent, more general rule. 

Macros also allow for rule chaining. 

When writing and debugging macros, running `bazel query <your target> --output=build` will expand the macro and show the generated code.
