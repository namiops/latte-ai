# Cypress test executor image

This directory contains the Bazel rule to build an OCI Docker images for executing Cypress tests.
Downloading and Installing NPM packages during runtime in Agora clusters poses security risks. As such, built images should be configured to includes all dependencies required to run the tests in an `offline` mode.
The images may be used with Testkube in Agora clusters, Local instances of Testkube, or directly with `docker run`.

- [Cypress test executor image](#cypress-test-executor-image)
  - [Prerequisties](#prerequisties)
  - [General process](#general-process)
  - [Bundling, CommonJS and tree shaking](#bundling-commonjs-and-tree-shaking)
  - [Exporting Cypress Videos, Screenshots and Reports](#exporting-cypress-videos-screenshots-and-reports)

## Prerequisties

- Monorepo guide ([Link](../../../../docs/monorepo/README.md)).
- Typescript / Javascript development guide ([Link](../../../../docs/development/javascript/README.md)).
- Deployment workflow guide ([Link](../../../../docs/monorepo/using-the-deployment-workflow.md)).
- Flux Image Update Automation ([Link](../../../../docs/technical_notes/cd.md)).
- Testkube getting started ([Link](../../docs/01_getting-started.md)).

## General process

- Write Cypress test scripts. See the examples [project](./example/some-project/).

- Create and configure the Bazel BUILD file ([Example](./example/some-project/BUILD)). Build and load the executor image locally with Bazel:

```bash
$ bazel run //ns/testkube/executor-images/cypress/example/some-project:some_project_bundled_cypress_image.load
Another command (pid=2402357) is running. Waiting for it to complete on the server (server_pid=1584613)...
INFO: Analyzed target //ns/testkube/executor-images/cypress/example/some-project:some_project_bundled_cypress_image.load (1 packages loaded, 23 targets configured).
INFO: Found 1 target...
Target //ns/testkube/executor-images/cypress/example/some-project:some_project_bundled_cypress_image.load up-to-date:
  bazel-bin/ns/testkube/executor-images/cypress/example/some-project/some_project_bundled_cypress_image.load/tarball.tar
INFO: Elapsed time: 15.591s, Critical Path: 14.80s
INFO: 6 processes: 1 internal, 4 linux-sandbox, 1 local.
INFO: Build completed successfully, 6 total actions
INFO: Running command line: bazel-bin/ns/testkube/executor-images/cypress/example/some-project/some_project_bundled_cypress_image.load.sh
aff53c356226: Loading layer [==================================================>]  9.278kB/9.278kB
The image ns/testkube/executor-images/cypress/example/some-project:some_project_bundled_cypress_image already exists, renaming the old one with ID sha256:b03bd6598b0fcce6aa459a310cf75c0f1d40934d881a4289088087629e131ccc to empty string
Loaded image: ns/testkube/executor-images/cypress/example/some-project:some_project_bundled_cypress_image # <--- loaded image
```

- Run the Docker image locally to verify correctness:

```bash
docker run -it ns/testkube/executor-images/cypress/example/some-project:some_project_bundled_cypress_image

iqqq: The istio quitquitquit process is disabled
[...]

Cypress Version: 12.17.4 (stable)
System Platform: linux (Debian - 11.7)
System Memory: 33.2 GB free 25.7 GB
+ exec cypress run --headless --browser chrome


DevTools listening on ws://127.0.0.1:37529/devtools/browser/37796f06-8838-4758-8532-a46ea41b6840
libva error: vaGetDriverNameByIndex() failed with unknown libva error, driver_name = (null)
[758:0708/012903.942001:ERROR:gpu_memory_buffer_support_x11.cc(44)] dri3 extension not supported.
Couldn't determine Mocha version

====================================================================================================

  (Run Starting)

  ┌────────────────────────────────────────────────────────────────────────────────────────────────┐
  │ Cypress:        12.17.4                                                                        │
  │ Browser:        Chrome 114 (headless)                                                          │
  │ Node Version:   v20.5.0 (/usr/local/bin/node)                                                  │
  │ Specs:          3 found (browser-notification-spec.cy.js, es2015-commonjs-modules-spec.cy.js,  │
  │                 files.cy.js)                                                                   │
  │ Searched:       cypress/e2e/**/*.cy.{js,jsx,ts,tsx}                                            │
  └────────────────────────────────────────────────────────────────────────────────────────────────┘


────────────────────────────────────────────────────────────────────────────────────────────────────
                                                                                                    
  Running:  browser-notification-spec.cy.js                                                 (1 of 3)

  (Results)

  ┌────────────────────────────────────────────────────────────────────────────────────────────────┐
  │ Tests:        8                                                                                │
  │ Passing:      8                                                                                │
  │ Failing:      0                                                                                │
  │ Pending:      0                                                                                │
  │ Skipped:      0                                                                                │
  │ Screenshots:  0                                                                                │
  │ Video:        true                                                                             │
  │ Duration:     1 second                                                                         │
  │ Spec Ran:     browser-notification-spec.cy.js                                                  │
  └────────────────────────────────────────────────────────────────────────────────────────────────┘


  (Video)

  -  Started compressing: Compressing to 32 CRF                                                     
  -  Finished compressing: 0 seconds                                                 

  -  Video output: /test/data/artifacts/videos/browser-notification-spec.cy.js.mp4


────────────────────────────────────────────────────────────────────────────────────────────────────
                                                                                                    
  Running:  es2015-commonjs-modules-spec.cy.js                                              (2 of 3)

  (Results)

  ┌────────────────────────────────────────────────────────────────────────────────────────────────┐
  │ Tests:        5                                                                                │
  │ Passing:      5                                                                                │
  │ Failing:      0                                                                                │
  │ Pending:      0                                                                                │
  │ Skipped:      0                                                                                │
  │ Screenshots:  0                                                                                │
  │ Video:        true                                                                             │
  │ Duration:     0 seconds                                                                        │
  │ Spec Ran:     es2015-commonjs-modules-spec.cy.js                                               │
  └────────────────────────────────────────────────────────────────────────────────────────────────┘


  (Video)

  -  Started compressing: Compressing to 32 CRF                                                     
  -  Finished compressing: 0 seconds                                                 

  -  Video output: /test/data/artifacts/videos/es2015-commonjs-modules-spec.cy.js.mp4


────────────────────────────────────────────────────────────────────────────────────────────────────
                                                                                                    
  Running:  files.cy.js                                                                     (3 of 3)

  (Results)

  ┌────────────────────────────────────────────────────────────────────────────────────────────────┐
  │ Tests:        4                                                                                │
  │ Passing:      4                                                                                │
  │ Failing:      0                                                                                │
  │ Pending:      0                                                                                │
  │ Skipped:      0                                                                                │
  │ Screenshots:  0                                                                                │
  │ Video:        true                                                                             │
  │ Duration:     3 seconds                                                                        │
  │ Spec Ran:     files.cy.js                                                                      │
  └────────────────────────────────────────────────────────────────────────────────────────────────┘


  (Video)

  -  Started compressing: Compressing to 32 CRF                                                     
  -  Finished compressing: 0 seconds                                                 

  -  Video output: /test/data/artifacts/videos/files.cy.js.mp4


====================================================================================================

  (Run Finished)


       Spec                                              Tests  Passing  Failing  Pending  Skipped  
  ┌────────────────────────────────────────────────────────────────────────────────────────────────┐
  │ ✔  browser-notification-spec.cy.js          00:01        8        8        -        -        - │
  ├────────────────────────────────────────────────────────────────────────────────────────────────┤
  │ ✔  es2015-commonjs-modules-spec.cy.js       132ms        5        5        -        -        - │
  ├────────────────────────────────────────────────────────────────────────────────────────────────┤
  │ ✔  files.cy.js                              00:03        4        4        -        -        - │
  └────────────────────────────────────────────────────────────────────────────────────────────────┘
    ✔  All specs passed!                        00:05       17       17        -        -        -  
```

- Create a pull request and merge the Cypress scripts and BUILD files to your team directory. Bazel will build and push the executor image to artifactory if everything is configured correctly. Obtain the built image tag.

- Configure Testkube manifests with the image tag [example](./example/some-project/testkube-some-project-manifests.yaml). Use Flux image policies to keep the executor up to date with the latest image tag.

- Create a pull request to add Testkube manifests to the tenants [directory](../../../../infrastructure/k8s/environments/dev2/clusters/worker1-east/testkube/tenants/), send the review request to the team in #wcm-agora-testkube.

## Bundling, CommonJS and tree shaking

If the test code is bundled with `esbuild`, as seen in the [example](./example/some-project/BUILD), errors might occur if the code is not tree-shakable.

esbuild's tree shaking implementation relies on the use of ECMAScript module import and export statements. It does not work with CommonJS modules so we should use libraries exported as ES Modules. Many packages on npm include both formats. Please refer to the [documentation](https://esbuild.github.io/api/#tree-shaking) for more information.

As a concrete example, instead of:

```javascript
import {upperFirst} from 'lodash';
import {lowerFirst} from 'lodash';
```

use:

```javascript
import {upperFirst} from 'lodash-es';
import {lowerFirst} from 'lodash-es';
```

or:

```javascript
import upperFirst from 'lodash/upperFirst';
import lowerFirst from 'lodash/lowerFirst';
```

## Exporting Cypress Videos, Screenshots and Reports

The `cypress_test_image` macro exports the following environment variables in the built oci image: `TEST_ARTIFACTS_DIR` and `TEST_REPORTS_DIR` (see [Cypress_test_image.bzl](./image.bzl)). These variables make it easier to locate the artifacts because the directory is known.

As such, we MUST use these environment variables in the `Cypress.config.js` file in properties such as `reporterOptions`,`VideosFolder` and `screenshotsFolder`:

```javascript
module.exports = defineConfig({
  e2e: {},
  // https://docs.cypress.io/guides/tooling/reporters
  reporter: 'junit',
  
  // use TEST_REPORTS_DIR (/test/data/reports) as an output directory when executing in Testkube.
  // configure '.spec.executionRequest.artifactRequest.volumeMountPath' in k8s `Test` yaml accordingly.
  // This env var is defined in 'ns/testkube/executor-images/cypress/image.bzl'
  reporterOptions: {
    mochaFile: process.env.TEST_REPORTS_DIR ? process.env.TEST_REPORTS_DIR + '/my-test-output-[hash].xml' : "./reports/my-test-output-[hash].xml",
  },

  // use TEST_ARTIFACTS_DIR (/test/data/artifacts) as an output directory when executing in Testkube.
  // configure '.spec.executionRequest.artifactRequest.volumeMountPath' in k8s `Test` yaml accordingly.
  // This env var is defined in 'ns/testkube/executor-images/cypress/image.bzl'.
  screenshotsFolder: process.env.TEST_ARTIFACTS_DIR ? process.env.TEST_ARTIFACTS_DIR + "/screenshots" : "./screenshots",
  video: true,
  viewportHeight: 100,
  viewportWidth: 200,
  // use TEST_ARTIFACTS_DIR (/test/data/artifacts) as an output directory when executing in Testkube.
  // configure '.spec.executionRequest.artifactRequest.volumeMountPath' in k8s `Test` yaml accordingly.
  // This env var is defined in 'ns/testkube/executor-images/cypress/image.bzl'.
  videosFolder: process.env.TEST_ARTIFACTS_DIR ? process.env.TEST_ARTIFACTS_DIR + "/videos" : "./videos",
})
```

The values of `TEST_ARTIFACTS_DIR` and `TEST_REPORTS_DIR` are used in Testkube manifests to mount the artifacts volume and copy test results. Please define the Testkube manifests similar to the [example](./example/some-project/testkube-some-project-manifests.yaml):

```yaml
apiVersion: executor.testkube.io/v1
kind: Executor
metadata:
  name: some-project-executor
  namespace: testkube
spec:
  executor_type: container
  jobTemplateReference: injected-cypress-container-2048mib
  image: <YOUR_IMAGE_HERE> # {"$imagepolicy": "flux-tenants:example-imagepolicy"}
  features:
  - artifacts
  types:
  - some_project/cypress:12
  meta:
    iconURI: cypress
    docsURI: https://github.com/wp-wcm/city/main/ns/testkube/executor-images/cypress
---
apiVersion: tests.testkube.io/v3
kind: Test
metadata:
  labels:
    app: some-project
  name: test-some-project
  namespace: testkube
spec:
  executionRequest:
    artifactRequest:
      storageClassName: testkube-tests-ebs-sc # KEEP
  type: some_project/cypress:12
```

:warning: Testkube, by default, sets the environment variable `CI=1`. This should be considered when using `process.env.CI` in `cypress.config.js` file.
:bulb: Additional environment variables may also be passed to the `cypress_test_image` rule via the attribute `env`
