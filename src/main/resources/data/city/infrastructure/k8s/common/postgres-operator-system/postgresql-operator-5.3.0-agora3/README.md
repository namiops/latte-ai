# Postgres Operator

This is a custom Crunchy Data Postgres Operator. There are some changes made to make this operator work better with Istio. You can read more about the operator in the official [operator documentation](https://access.crunchydata.com/documentation/postgres-operator/latest/quickstart/).

This version of the deployment adds the annotations to enable Prometheus scraping for the PGO operator.
There are no other changes, such as updates on the PGO images.

## Before Update

This version doesn't include any updates on the PGO version from [postgresql-operator-5.3.0-agora2](/infrastructure/k8s/common/postgres-operator-system/postgresql-operator-5.3.0-agora2).
So, when we update the deployment from the previous one, there is no special procedure other than the common things for updating Kubernetes resources.

## Images

The operator uses two custom images.

### Operator Images

The Agora team members modified the operator source code. You can find the modified source code [here](https://github.tri-ad.tech/cityos-platform/postgres-operator). The code used to make the actual image for this version is in the branch [operator-patch-5.3.0](https://github.tri-ad.tech/cityos-platform/postgres-operator/tree/operator-patch-5.3.0).

Note we always execute the commands in the modified operator [repository](https://github.tri-ad.tech/cityos-platform/postgres-operator).

To make the image,  I first checked the date when the official operator images was created and then cross-referenced it with the closest commit. This is because the team beind pgo forgot to tag the new chnages they added correctly.

I used this commit to make the image: [20c0a338e82630ebc1533f81f3f578f14eaffa5d](https://github.com/CrunchyData/postgres-operator/commit/20c0a338e82630ebc1533f81f3f578f14eaffa5d).

I made the image like so:

```shell
# Checkout the branch
$ git checkout operator-patch-5.3.0

# Change the source branch to the target branch
$ git rebase --onto 20c0a338e82630ebc1533f81f3f578f14eaffa5d main operator-patch-5.3.0

# Build the image
$ IMGBUILDER=docker PGO_BASEOS=ubi8 PGO_VERSION=5.3.0-agora2-$(git rev-parse --short HEAD) PGO_IMAGE_PREFIX=docker.artifactory-ha.tri-ad.tech/wcm-cityos/postgresql/custom-postgres-operator make postgres-operator-img-build

# Push the image
$ IMGBUILDER=docker PGO_BASEOS=ubi8 PGO_VERSION=5.3.0-agora2-$(git rev-parse --short HEAD) PGO_IMAGE_PREFIX=docker.artifactory-ha.tri-ad.tech/wcm-cityos/postgresql/custom-postgres-operator make push-postgres-operator
```

Generating the image also makes a new version of crd. You can find the new crd at `build/crd/generated/`. Save a copy of it because it is required when making the operator yaml files.

In the future, make a new branch from the branch [operator-patch-5.3.0-2](https://github.tri-ad.tech/cityos-platform/postgres-operator/tree/operator-patch-5.3.0-2) and make the operator image from there.

### PgBackRest Image (optional)

This image is built by the agora teams' build pipeline. To make this, I checked which version of pgBackRest is supported by the operator version. You can check it in the [official documentation here](https://access.crunchydata.com/documentation/postgres-operator/latest/references/components/).

I changed the image in the [WORKSPACE](../../../../../WORKSPACE) file in the Agora teams repository. I updated the `pgbackrest` container pull with the new official image for the pgBackRest.

The image ends up in [Artifactory](https://artifactory-ha.tri-ad.tech/ui/repos/tree/General/docker/wcm-cityos/postgresql/custom-pgbackrest).

## Files

Once you have the image(s), you can download the example from [https://github.com/CrunchyData/postgres-operator-examples](https://github.com/CrunchyData/postgres-operator-examples). Just make sure you download the correct version of the files. Mostly I had to change the related images, the operator image, and the crd. Your changes should be similar to chnages in this [Pull Request](https://github.tri-ad.tech/cityos-platform/cityos/pull/4306).

## Testing

I used manual tests on the local cluster to see what happens when I update the operator.

## During Update

During the update of the operator, in most cases, all the Postgres pods in the cluster will have to restart. This can be for multiple reasons, like related images changing or the new version of the operator managing the pods differently.

If Postgres is configured with additional replicas, the operator will do a rolling update. In this case, downtime might be just the time it takes to switch between replicas.

If Postgres is configured without replicas, then the operator will update the pods, which will cause restarts. This will take about 30 to 60 seconds.
