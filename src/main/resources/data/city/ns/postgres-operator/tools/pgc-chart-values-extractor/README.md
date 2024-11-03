# pgc-chart-values-extractor.py

This small script aims to help the migration to the standardized PostgresCluster manifest.
We've been defining the manifest standardization as a Helm chart [agora-postgres-cluster](/infrastructure/helm/agora-postgres-cluster).
The script generates a `values.yaml` file for the chart from the existing PostgresCluster manifest.
We can use the generated `values.yaml` by adding some necessary configurations, such as the storage class.

Regarding the usage of the chart, you can see [/infrastructure/k8s/common/brr/postgres-cluster-0.2.1](/infrastructure/k8s/common/brr/postgres-cluster-0.2.1) as an example.

## Usage

```bash
$ python ns/postgres-operator/docs/tools/pgc-chart-values-extractor/pgc-chart-values-extractor.py --help
usage: Agora PGC chart values extractor [-h] [--debug] target

This small script generates "values.yaml" for the Agora PostgresCluster chart from the existing PostgresCluster manifest to help the migration to the standardized manifest. We can use the generated "values.yaml" by adding some necessary configurations, such as the storage class. This script requires a Python module PyYaml (https://pyyaml.org/wiki/PyYAMLDocumentation). And it requires the CLI tool yq (https://github.com/mikefarah/yq).

positional arguments:
  target      Path of PostgresCluster manifest

  options:
    -h, --help  show this help message and exit
      --debug
```
