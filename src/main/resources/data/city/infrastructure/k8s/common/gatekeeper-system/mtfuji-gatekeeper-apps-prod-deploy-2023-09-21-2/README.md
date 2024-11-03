## Generate Gatekeeper manifest
Download Gatekeeper manifest as per official [docs](https://open-policy-agent.github.io/gatekeeper/website/docs/install#deploying-a-release-using-prebuilt-image)

```shell
curl https://raw.githubusercontent.com/open-policy-agent/gatekeeper/release-3.10/deploy/gatekeeper.yaml -o manifests/gatekeeper/gatekeeper.yaml
```

## ConstraintTemplates

### Public Library
Aside from creating custom ConstraintTemplates, we leverage the [gatekeeper-library](https://github.com/open-policy-agent/gatekeeper-library/tree/master/library) to use Templates created by the open-source
community. This prevents us from re-inventing the wheel. To add these templates, do the following:

For example, we want to add the `general/allowedrepos` Template
1. Create a folder under [library](library) named `allowedrepos` and copy the files from upstream
1. Add the ConstraintTemplate path `library/allowedrepos/template.yaml` in the [kustomization.yaml](kustomization.yaml) file

### Custom Library
When creating ConstraintTemplates from scratch, please follow the directory structure below:

An example of creating `my-custom-template`
```
src
└── my-custom-template                          # Name of ConstraintTemplate
    ├── kustomization.yaml                      # Kustomization file that references template.yaml
    ├── samples                         
    │       └── test-case-1                     # Files for test-case-1 in suite.yaml
    │           ├── constraint.yaml             # Gatekeeper Constraint to test 
    │           └── (other files for testing)   
    ├── suite.yaml                              # Gator Test Suite                   
    └── template.yaml                           # Gatekeeper Contraint Template
```
You can use [OPA Playground](https://play.openpolicyagent.org/p/UlVJ0E8Vqd) for your quick testing during the development. Make sure to replace `input` with `input.review.object` when you are editing the policy on ./src/my-custom-template/template.yaml.

Remember to add the ConstraintTemplate path `src/my-custom-template/template.yaml` in the [kustomization.yaml](kustomization.yaml) file. You can also refer to [this PR](https://github.tri-ad.tech/TRI-AD/mtfuji-infra/pull/576/files) as an example.

## Test
[Gator CLI](https://open-policy-agent.github.io/gatekeeper/website/docs/gator/) is used to tests Gatekeeper ConstraintTemplates.
Since a misconfigured Gatekeeper policy is a blocking process, we have to create tests in order to be confident about its
deployment. 

### Gator
Follow this [guide](https://open-policy-agent.github.io/gatekeeper/website/docs/gator/#installation) to install Gator CLI

### Running tests
Running the command below will run the tests for all of the `suite.yaml` found under the `<project-root>/manifests/gatekeeper`
subdirectories. For more information on writing Gator CLI tests, you may refer to this [page](https://open-policy-agent.github.io/gatekeeper/website/docs/gator/#suites).

```shell
# Run all tests under subdirectories
$ cd <project-root>/manifests/gatekeeper
$ gator verify ./...

ok      <project-root>/manifests/gatekeeper/library/allowedrepos/suite.yaml 0.163s
ok      <project-root>/manifests/gatekeeper/library/automount-serviceaccount-token/suite.yaml       0.097s
ok      <project-root>/manifests/gatekeeper/library/containerlimits/suite.yaml      0.105s
ok      <project-root>/manifests/gatekeeper/src/istio-vs-same-host/suite.yaml       0.102s
PASS
```

## Apply the Constraint Template
Once your template is ready either on `./library` or `./src`, you can create the constraint yaml file on [./constraints](constraints), and then enable it on [./constraints/kustomization.yaml](constraints/kustomization.yaml). Similarly, if you want to disable a constraint template, you can remove it from [./constraints/kustomization.yaml](constraints/kustomization.yaml).

## Custom Code

There are some policies from the upstream library that we had to add custom code for various reasons. The following
are a list of the policies that have custom code and the reasons behind it. If the upstream eventually adds code that
does the same thing as our custom code, we can remove the policy from the list below.

#### allowedrepos

We added support for matching against wildcard prefixes to capture any subdomain up one level from the base registry.
For example, we whitelist `*.myregisty.com` to allow images pulled from `a.myregistry.com` and `b.myregistry.com`.

#### requiredprobes

When using expansion, the data in `input.review` doesn't contain the `.metadata.name` property of the kubernetes 
manifest being validated. `input.review` is being passed to the function `get_violation_message` where a child 
property `.name` is used. The policy fails and does not show up in the output due to this.

Since expansion is still in Alpha at the time of this writing, we adjust the violation message to not include
the name of the pod for now.
