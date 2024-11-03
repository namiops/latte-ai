# Keycloak Legacy Configuration

The Keycloak Legacy's image construction
[script](https://github.com/keycloak/keycloak-containers/blob/main/server/tools/build-keycloak.sh#L85)
overrites the `standalone.xml` and `standalone-ha.xml` configuration files so they
use environment variables to setup keycloak.

This folder contains such files extracted from the container, so we can do the
same in bazel instead.

It also contains a group and passwd files. They combine the users created in the
distroless image with the expected user/group (jboss/jboss - 1000/1000) on upstream
keycloak. Doing this because there is a sidecar that mount things and move files
around under the assumption that this is the user running keycloak. I just keept
original distroless users for consistency.
