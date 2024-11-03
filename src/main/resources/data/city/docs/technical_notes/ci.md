# CI

### Github Actions

We use Github Actions as a CI tool. This is because the organization has provisioned Github Actions
runners and it integrates natively with Github where we store our code. 

All of the actions are run in containers with our own custom images. The reason for running in containers
can be described [here](https://confluence.tri-ad.tech/pages/viewpage.action?spaceKey=IEKB&title=Restrictions).
The Dockerfiles for the custom images run on these containers can be found in [here](/infra/images). If you are
interested in using the images, feel free to try them out! If you want to contribute to them, you can also make
a PR or raise an issue!

### Building Images

Images are built using Bazel to the company's Artifactory. Using the company's Artifactory is a short-term measure
while we set up a better image repository. For reasoning on this, see [Artifactory HA](#artifactory-ha). More info 
on Bazel can be found [here](./bazel/bazel.md).

<!-- 
TODO: Add build constraints here when implemented, or pull it out into a separate file/section
https://jira.tri-ad.tech/browse/WCMDO-60
-->

#### Artifactory HA

While the organization does provide Artifactory HA as an artifacts repository, we have chosen to not use
it in the long term, as the control over the images is minimal at best. The repository settings are all global, 
meaning that anyone with access can access, change, and delete any other artifact. In addition, any retention 
policy set would be applied globally.