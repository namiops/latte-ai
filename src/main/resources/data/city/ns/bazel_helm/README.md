# bazel_helm

## Intro

A zebra-patterned tool to render Helm templates from packaged charts. Also provides a repository_rule to download and provide Helm charts to the Bazel ecosystem.

## Usage

`WORKSPACE` additions

```
helm_repository_chart(
  name = "prometheus_helm_chart",
  archive_url = "https://github.com/prometheus-community/helm-charts/releases/download/prometheus-13.8.0/prometheus-13.8.0.tgz",
  sha256 = "979c465fb0bc925b8714dc878fd2faae86c311141bd92086e36dd733d0dff6a3",
)
```

Each chart needs an archive to the tgz containing the package, as well as the SHA hash to use as a checksum and ensure the upstream file has not been changed.

`BUILD` (in your desired output folder)

```
load("//ns/bazel_helm:helm_template.bzl", "helm_template")

helm_template(
    name = "zebra_template_test",
    copy_to_source = True,
    output_filename = "zebra-manifest.yaml",
    chart_file = "@prometheus_helm_chart//:chart",
    chart_version = "1.57.0",
    release_name = "zebra-template-test",
    metadata_namespace = "zebra_template_test_ns",
)
```

Note the package `@prometheus_helm_chart` and the output target `//:chart` - the name resolution is handled automatically, the `:chart` target always corresponds to the downloaded tgz file. The other attributes (version, release, namespace) are passed directly to Helm.
