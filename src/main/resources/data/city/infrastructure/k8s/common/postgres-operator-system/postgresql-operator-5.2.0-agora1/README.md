# Postgres Operator

This is a custom Crunchy Data Postgres Operator. There are some changes made to make this operator work better with Istio. You can read more about the operator in the official [operator documentation](https://access.crunchydata.com/documentation/postgres-operator/latest/quickstart/).

## Before Update

Before I updated, I checked the official documentation to see if there were any particular steps I needed to take before I updated the operator.

You can check it in the [Upgrading PGO v5 Using Kustomize](https://access.crunchydata.com/documentation/postgres-operator/latest/upgrade/kustomize/) section of official documentation.

In this case, I was updating from `5.0.4` to `5.2.0`. This required some extra configurations.

## Images

The operator uses two custom images.

### Operator Images

The Agora team members modified the operator source code. You can find the modified source code [here](https://github.tri-ad.tech/cityos-platform/postgres-operator). The code used to make the actual image for this version is in the branch [operator-patch-5.2.0](https://github.tri-ad.tech/cityos-platform/postgres-operator/tree/operator-patch-5.2.0).

Note we always execute the commands in the modified operator [repository](https://github.tri-ad.tech/cityos-platform/postgres-operator).

To make the image, I picked the latest commit to have version `5.2.0` at the time. Like so

```shell
$ git grep "5\.2\.0" $(git rev-list origin/main) docs/config.toml | head -1
80dfa39fbe33e6dc9d384d6161578986b00adecf:docs/config.toml:operatorVersion = "5.2.0"
```

I used this commit to make the image: [80dfa39fbe33e6dc9d384d6161578986b00adecf](https://github.com/CrunchyData/postgres-operator/commit/80dfa39fbe33e6dc9d384d6161578986b00adecf).

I made the image like so:

```shell
# Checkout the branch
$ git checkout operator-patch-5.2.0

# Change the source branch to the target branch
$ git rebase --onto 80dfa39fbe33e6dc9d384d6161578986b00adecf main operator-patch-5.2.0

# Build the image
$ IMGBUILDER=docker PGO_BASEOS=ubi8 PGO_VERSION=5.2.0-$(git rev-parse --short HEAD) PGO_IMAGE_PREFIX=docker.artifactory-ha.tri-ad.tech/wcm-cityos/postgresql/custom-postgres-operator make postgres-operator-img-build

# Push the image
$ IMGBUILDER=docker PGO_BASEOS=ubi8 PGO_VERSION=5.2.0-$(git rev-parse --short HEAD) PGO_IMAGE_PREFIX=docker.artifactory-ha.tri-ad.tech/wcm-cityos/postgresql/custom-postgres-operator make push-postgres-operator
```

Once finished, push the branch as making the image also makes the crd.

In the future, make a new branch from the branch [operator-patch-5.2.0](https://github.tri-ad.tech/cityos-platform/postgres-operator/tree/operator-patch-5.2.0) and make the operator image from there.

### PgBackRest Image (optional)

This image is built by the agora teams' build pipeline. To make this, I checked which version of pgBackRest is supported by the operator version. You can check it in the [official documentation here](https://access.crunchydata.com/documentation/postgres-operator/latest/references/components/).

I changed the image in the [WORKSPACE](../../../../../WORKSPACE) file in the Agora teams repository. I updated the `pgbackrest` container pull with the new official image for the pgBackRest.

The image ends up in [Artifactory](https://artifactory-ha.tri-ad.tech/ui/repos/tree/General/docker/wcm-cityos/postgresql/custom-pgbackrest).

## Files

Once you have the image(s), you can download the example from [https://github.com/CrunchyData/postgres-operator-examples](https://github.com/CrunchyData/postgres-operator-examples). Just make sure you download the correct version of the files. Mostly I had to change the related images, the operator image, and the crd. You can have a look at the difference in this [Pull Request](https://github.tri-ad.tech/cityos-platform/cityos/pull/4306).

## Testing

I used manual tests on the local cluster to see what happens when I update the operator.

## During Update

During the update of the operator, in most cases, all the Postgres pods in the cluster will have to restart. This can be for multiple reasons, like related images changing or the new version of the operator managing the pods differently.

If Postgres is configured with additional replicas, the operator will do a rolling update. In this case, downtime might be just the time it takes to switch between replicas.

If Postgres is configured without replicas, then the operator will update the pods, which will cause restarts. This will take about 30 to 60 seconds.
