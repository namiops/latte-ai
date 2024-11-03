# Links
- https://github.com/bazelbuild/rules_go/blob/master/docs/go/core/rules.md#go_binary
- https://github.com/prometheus-community/postgres_exporter
- https://github.com/opencontainers/image-spec/blob/main/annotations.md
- https://www.postgresql.fastware.com/blog/what-is-the-new-lz4-toast-compression-in-postgresql-14
- https://github.com/pgaudit/pgaudit
- https://github.com/CrunchyData/pgnodemx
- Bazel :
  - https://developer.woven-city.toyota/docs/default/domain/agora-domain/development/docker/#b-building-a-generic-oci-image
  - https://github.com/bazel-contrib/rules_oci/blob/main/docs/compare_dockerfile.md
  - https://github.com/chainguard-dev/rules_apko/tree/main/examples
- https://docs.gitlab.com/ee/user/project/codeowners/reference.html

# Notes :
```
export PGO_VERSION=v.7.6
export IMAGE_TAG=latest
make release-crunchy-postgres-exporter-image

buildah bud \
        --tag localhost/postgres-operator:latest \
        --label name='postgres-operator' \
        --label build-date='2023-12-19T03:58:14Z' \
        --label description='Crunchy PostgreSQL Operator' \
        --label maintainer='Crunchy Data' \
        --label summary='Crunchy PostgreSQL Operator' \
        --label url='https://www.crunchydata.com/products/crunchy-postgresql-for-kubernetes' \
        --label vcs-ref='2bad41458f86e2e6563c26723f66a82faee420c6' \
        --label vendor='Crunchy Data' \
        --label io.k8s.display-name='postgres-operator' \
        --label io.k8s.description='Crunchy PostgreSQL Operator' \
        --label io.openshift.tags="postgresql,postgres,sql,nosql,crunchy" \
        --annotation org.opencontainers.image.authors='Crunchy Data' \
        --annotation org.opencontainers.image.vendor='Crunchy Data' \
        --annotation org.opencontainers.image.created='2023-12-19T03:58:14Z' \
        --annotation org.opencontainers.image.description='Crunchy PostgreSQL Operator' \
        --annotation org.opencontainers.image.revision='2bad41458f86e2e6563c26723f66a82faee420c6' \
        --annotation org.opencontainers.image.title='Crunchy PostgreSQL Operator' \
        --annotation org.opencontainers.image.url='https://www.crunchydata.com/products/crunchy-postgresql-for-kubernetes' \
        --label release='v4.7.6' --label version='v4.7.6' --annotation org.opencontainers.image.version='v4.7.6' \
        --file build/postgres-operator/Dockerfile --format docker --layers .
```
- Prometheus all labels :
```
http://localhost:9090/api/v1/label/__name__/values
```

kubectl cp !

```
jq '.processes[] | select(.type | match("bgwriter|walwriter")) | del(.pid) | del(.uss)' plop
```
