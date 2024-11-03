# pgAgora Operator

## Changelog
- 20240131 [v1.0.0] Integration improvement in our monorepo and upgrade to Zalando Operator 1.10.1
  See release notes : https://github.com/zalando/postgres-operator/releases/tag/v1.10.1
- 202309xx [v0.0.0] Initial import for PoC based on Zalando Operator 1.10.0
  See release notes : https://github.com/zalando/postgres-operator/releases/tag/v1.10.0

## Update

To reflect the update of values here in the kustomize places, you have two options :
- Let CI/CD do it for you with GHA and steps related to Zebra. The "how is it implemented ?" is out of the scope  
of this README but basically if you push your updated values files, GHA will use Bazel to update kustomization and  
then commit those changes on you behalf.
- Copycat what GHA is doing. Basically you have to run this command to get the bazel target :
```
bazel query 'attr(tags, "\bzebra\b", filter(".*zalando.*.copy$", //...))' --color=no --noshow_progress
```
It really bounds to Zalando PostgreSQL Operator as we filter to match all of them but it's a pattern you can adapt obviously to your needs if any.
You should get :
```
[dbaron@agora-dev-vm-dbaron zalando-postgres-operator-system-1.10.1 (david.baron/pgo-zalando)]$ bazel query 'attr(tags, "\bzebra\b", filter(".*zalando.*.copy$", //...))' --color=no --noshow_progress
//infrastructure/helm/agora-zalando-postgresql/e2e:simple.copy
//infrastructure/k8s/common/zalando-postgres-operator-system/pgagora-operator-1.0.0-agora1:zal_pgo.copy
//infrastructure/k8s/common/zalando-postgres-operator-system/pgagora-operator-1.0.0-agora1:zal_pgo_crds.copy
//infrastructure/k8s/environments/lab2/clusters/worker1-east/postgresql-sample:zalando-postgresql-sample.copy
//infrastructure/k8s/environments/local/clusters/worker1-east/postgresql-sample:zalando-postgresql-sample.copy
```
For the sake of this topic, we should focus on the two last lines.
Then run :
```
bazel run //infrastructure/k8s/environments/local/clusters/worker1-east/postgresql-sample:zalando-postgresql-sample.copy
```
This will generate kustomize for you and you can review them by yourself and start from there.

## Upgrade

Process to upgrade the Zalando PostgreSQL Operator depend on the changelog of it.
For example while working on upgrade from version `1.10.0` to `1.10.1`, differences were easy to catch and port.
Files are located in `infrastructure/helm/agora-zalando-postgresql`. From what I've seen we do not keep version there,
just replace/delete existing files there. We do not bother to keep the `Helm Chart` versioning as well. `0.0.0` is
defacto standard.
As we do not (yet) patched it, it was a simpler replace to existing files and a vimdiff for `values.yaml` just
in case if we have some default values we'd like to enforce compared to upstream.

When you are fine with your *import* and do not forget to create/update your `BUILD` file, you can query the bazel
WORKSPACE and as previous get the proper targets.
Then run `bazel run` like that one for current layout ( 2023-11-21 ) :
```
bazel run //infrastructure/k8s/common/zalando-postgres-operator-system/pgagora-operator-1.0.0-agora1:zal_pgo.copy
bazel run //infrastructure/k8s/common/zalando-postgres-operator-system/pgagora-operator-1.0.0-agora1:zal_pgo_crds.copy
```

Obviously when upgrading, you have to loop to updating chapter from this document.

## Tips
You can generate docs from CRD that way ( please pay attention to the path and you have to refer
to your own clone of Zalando PostgreSQL Operator ) :
```
git clone git@github.com:elastic/crd-ref-docs.git
cd crd-ref-docs
go build
./crd-ref-docs --source-path=/home/dbaron/git/postgres-operator/pkg/apis --renderer=asciidoctor --max-depth=20
```
You'll get a file name `out.asciidoc` ( Markdown if available via `--render=markdown` ).
If you prefer HTML you can use `asciidoctor out.asciidoc` to convert it to a single HTML file with embedded css.
Some details available [here](https://alvinalexander.com/source-code/how-to-convert-asciidoc-to-html/).

`values.yaml` defined here are used/merged with the Helm Chart template visible here :
https://github.com/zalando/postgres-operator/tree/v1.10.1/charts/postgres-operator  
and  
https://github.com/zalando/postgres-operator/tree/v1.10.1/charts/postgres-operator/templates

## Links

- https://opensource.zalando.com/postgres-operator/docs/reference/cluster_manifest.html
- https://github.com/zalando/postgres-operator/tree/v1.10.1/charts/postgres-operator  