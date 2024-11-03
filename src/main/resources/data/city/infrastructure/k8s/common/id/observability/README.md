# Identity Observability

In this directory, you will find grafana dashboards and prometheus alerts for
Identity services.

## Jsonnet

[Jsonnet](https://jsonnet.org) is used with bazel to generate Grafana dashboards
and Prometheus alerts.

A Jsonnet library is included in the [bazel directory](./bazel) that contains
code for creating different types of dashboards and alerts.

## Alerts

### Slack Integration

Alerts are currently handled under the slack channel named `#wcm-org-agora-iam-alerts-dev` on dev environment only.
Since it is a private channel, please ask one of the team members for invitation.

For alerts to work, a slack app has been set to administer all slack webhooks: <https://api.slack.com/apps/A068FHSMH0W>.
If you need access as a collaborator, please request it to any team member. 

In order to activate slack alarm notifications:

1.  Create a new slack channel (private) including all team members, with the purpose of sending your alerts to it.
2.  Under the above slack app link, go to `Incoming Webhooks` and click on `Add New Webhook to Workspace`.
3.  Once you have generated a webhook URL on the previous step, you can test whether this webhook URL works by sending a curl.
    ```shell
    curl -X POST --data-urlencode "payload={\"channel\": \"#<my-new-slack-channel-for-alerts>\", \"username\": \"<my-slackbot-username-of-choice>\", \"text\": \"This is a test.\", \"icon_emoji\": \":prometheus:\"}" <webhook-url>
    ```
4.  Store the webhook URL in a kubernetes secret as follows:
    1.  Set cluster context as necessary, e.g:
        ```shell
        kubectl config use-context <your-target-environment>
        ```
    2.  Run this command:
        ```shell
        read -s WEBHOOK_URL
        ```
        After that, paste the webhook URL (to prevent it from going into shell command history).
    3.  Run this command to store the secret:
        ```shell
        kubectl create secret generic -n id slack-webhook-url --from-literal=address="$WEBHOOK_URL"
        ```
    4.  Run this command to check if the secret is successfully stored:
        ```shell
        kubectl get secret -n id slack-webhook-url -o jsonpath='{.data.address}' | base64 --decode
        ```
5.  On your `AlertmanagerConfig` custom resource, specify the newly created secret as follows:
    ```yaml
    spec:
      receivers:
        - name: slack
          slackConfigs:
            - apiURL:
                name: slack-webhook-url
                key: address
              channel: "#<my-new-slack-channel-for-alerts>"
              iconEmoji: ":prometheus:"
              username: "<my-slackbot-username-of-choice>"
              text: |-
                {{ range .Alerts -}}
                  :{{ .Labels.emoji }}: *Alert Name:* {{ .Labels.alertname }}

                  *Summary:* {{ .Annotations.summary }}
                  *Description:* {{ .Annotations.description }}

                {{ end }}
              title: '[{{ .Status | toUpper }}{{ if eq .Status "firing" }}:{{ .Alerts.Firing
                | len }}{{ end }}] {{ .CommonLabels.group }}'
              titleLink: https://observability-dev.${cluster_domain}/prometheus/alerts?search=identity
    ```
6.  You have successfully setup the slack webhook.

### Creating alerts with Jsonnet

To create new alerts with Jsonnet, please do the following:

- Create and move to a new directory under this directory as
  `<service_name>/alerts/<alert_topic>`
- Create a `BUILD` file as follows while replacing `<service_name>` (e.g.
  `drako`):
  ```bazel
  load("@aspect_bazel_lib//lib:write_source_files.bzl", "write_source_file")
  load("@io_bazel_rules_jsonnet//jsonnet:jsonnet.bzl", "jsonnet_to_json")

  jsonnet_to_json(
      name = "alerts",
      src = "alerts.jsonnet",
      outs = ["alerts.yaml.gen"],
      imports = [
          "../../../bazel",
          "../../config",
      ],
      yaml_stream = True,
      deps = [
          "//infrastructure/k8s/common/id/observability/bazel:lib",
          "//infrastructure/k8s/common/id/observability/<service_name>/config:lib",
      ],
  )

  write_source_file(
      name = "alerts.copy",
      in_file = ":alerts.yaml.gen",
      out_file = "alerts.yaml",
      tags = [
          "jsonnet",
          "zebra",
      ],
  )

  filegroup(
      name = "files",
      srcs = glob([
          "**/*.json",
          "**/*.yaml",
      ]),
      visibility = ["//visibility:public"],
  )
  ```
- Create a `kustomization.yaml` file as follows:
  ```yaml
  apiVersion: kustomize.config.k8s.io/v1beta1
  kind: Kustomization
  namespace: id
  resources:
    - alerts.yaml
  ```
- Create a `alerts.jsonnet` file with the Jsonnet code you would like to
  generate your alerts. For example, the following creates alerts about resource
  usage for drako:
  ```jsonnet
  local config = import 'config_drako.libsonnet';
  local lib = import 'lib.libsonnet';

  [
    lib.PrometheusRuleCustomResource(
      namespace=config.Namespace,
      name='drako-resource-metrics',
      groups=[
        lib.Container.AlertGroup(config),
      ],
    ),
  ]
  ```
- Generate the `alerts.json` file from `alerts.jsonnet` by
  executing the following from the directory you created:
  ```console
  bazel run :alerts.copy
  ```
- Ensure that the new rules are exposed by a bazel `BUILD` file as a
  `filegroup` in every directory starting from [observability/BUILD](BUILD). This
  is important to ensure that validation of infrastructure file succeeds in CI/CD.
  To ensure that everything is valid, you can run the following while replacing
  `<env>` with the desired environment (e.g. `local`):
  ```console
  bazel run //infrastructure/k8s/<env>:validate
  ```

### Updating alerts

If you update a Jsonnet dashboard, you can simply regenerate it by doing the following:
```console
cd <city_repo>/infrastructure/k8s/common/id/observability/<service_name>/alerts/<alert_topic>
bazel run :alerts.copy
```

If you make changes to the [bazel](./bazel) code, it is a good idea to update
all the alerts.  You can do it one by one, or you can use something like the
following, that will update all alerts with one command:

```console
bazel query //infrastructure/k8s/common/id/observability/... | grep -E ':alerts.copy$' | xargs -L 1 bazel run
```

## Grafana Dashboards

### Creating new dashboard with Jsonnet

To create a new dashboard with Jsonnet, please do the following:

- Create and move to a new directory under this directory as
  `<service_name>/dashboards/<dashboard_name>`
- Create a `BUILD` file as follows while replacing `<service_name>` (e.g.
  `drako`):
  ```bazel
  load("@aspect_bazel_lib//lib:write_source_files.bzl", "write_source_files")
  load("@io_bazel_rules_jsonnet//jsonnet:jsonnet.bzl", "jsonnet_to_json")

  jsonnet_to_json(
      name = "dashboard",
      src = "dashboard.jsonnet",
      outs = [
          "dashboard.json.gen",
          "dashboard.yaml.gen",
          "kustomization.yaml.gen",
      ],
      imports = [
          "../../../bazel",
          "../../config",
      ],
      deps = [
          "//infrastructure/k8s/common/id/observability/bazel:lib",
          "//infrastructure/k8s/common/id/observability/<service_name>/config:lib",
      ],
  )

  write_source_files(
      name = "dashboard.copy",
      files = {
          "dashboard.json": ":dashboard.json.gen",
          "dashboard.yaml": ":dashboard.yaml.gen",
          "kustomization.yaml": ":kustomization.yaml.gen",
      },
      tags = [
          "jsonnet",
          "zebra",
      ],
  )

  filegroup(
      name = "files",
      srcs = glob([
          "**/*.json",
          "**/*.yaml",
      ]),
      visibility = ["//visibility:public"],
  )
  ```
- Create a `dashboard.jsonnet` file with the Jsonnet code you would like to
  generate your dashboard.
  For example, the following creates a dashboard with resource usage for drako:
  ```jsonnet
  local config = import 'config_drako.libsonnet';
  local lib = import 'lib.libsonnet';

  local dashboard = lib.Dashboard() + {
    title: 'Resource Metrics',
    panels: lib.RepositionPanels(lib.Container.ResourcePanels(config)),
    templating+: {
      list+: lib.Container.TemplatingList(config),
    },
  };

  lib.DashboardResourceGeneration(
    namespace='id',
    name='grafana-drako-resource-metrics',
    folderName='Identity Drako Service',
    dashboard=dashboard,
  )
  ```
- Generate the `dashboard.json`, `dashboard.yaml`, and `kustomization.yaml` files
  from `dashboard.jsonnet` by executing the following from the directory you created:
  ```console
  bazel run :dashboard.copy
  ```
- Ensure that the new dashboard is exposed by a bazel `BUILD` file as a
  `filegroup` in every directory starting from [observability/BUILD](BUILD).  This
  is important to ensure that validation of infrastructure file succeeds in CI/CD.
  To ensure that everything is valid, you can run the following while replacing
  `<env>` with the desired environment (e.g. `local`):
  ```console
  bazel run //infrastructure/k8s/<env>:validate
  ```

### Updating dashboards

If you update a Jsonnet dashboard, you can simply regenerate it by doing the following:
```console
cd <city_repo>/infrastructure/k8s/common/id/observability/<service_name>/dashboards/<dashboard_name>
bazel run :dashboard.copy
```

If you make changes to the [bazel](./bazel) code, it is a good idea to update
all the dashboards.  You can do it one by one, or you can use something like the
following, that will update all dashboards with one command:

```console
bazel query //infrastructure/k8s/common/id/observability/... | grep -E ':dashboard.copy$' | xargs -L 1 bazel run
```
