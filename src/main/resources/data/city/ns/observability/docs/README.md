# Alerting in Preprod
## Creating Alerts in Grafana

### Create new alerts
#### Steps

- Visit [Grafana](https://athena.agora-dev.w3n.io/grafana) and navigate to [Alert rules](https://athena.agora-dev.w3n.io/grafana/alerting/list)
![alert rules](screenshots/alert-rules.png)

- Click [New Alert Rule](https://athena.agora-dev.w3n.io/grafana/alerting/new?returnTo=%2Falerting%2Flist)
![new alert rule](screenshots/new-alert.png)

- Select **Mimir or Loki alert** and choose **Prometheus** as the data source
![data source](screenshots/data-source.png)

- Input alert details
  -  Metric
  -  Alert evaluation behavior: specify how long the metric must be true for before triggers an alert
  -  Details: this is the information that will be accessible to you to display in slack 
![alert details](screenshots/alert-details.png)

- Input any required alert labels
!!! Danger "Required Labels"
    you MUST include the _namespace_ label that corresponds with the your K8s namespace
![alert labels](screenshots/alert-labels.png)

- Save and Exit
![save alert](screenshots/alert-save.png)

- Confirm alert has been created successfully on the [Alert rules](https://athena.agora-dev.w3n.io/grafana/alerting/list) page
![confirm alert](screenshots/alert-confirm.png)

### Migrate alerts from Dev environment
## Steps
 
- Find your existing PrometheusRule resource declaration file.

- In this repository, open to `ns/observability/alerts_values.yaml` and paste the contents of your prometheus rule file below the `---`. remove any comments from the pasted contents.
```yaml
#@data/values

#! paste your dev environment Prometheus Rules file below the ---
#! Including DevRel's alerts as an example
---
paste here!
```

- Run `bazel run //ns/observability:generate_alerts.copy` which will generate alerts in the correct format for grafana in `alerts_generated.yaml`. 
  !!! Note
      Follow the rest of the steps for each alert generated in the `alerts_generated.yaml` file.

- Navigate to [Alert rules](https://athena.agora-dev.w3n.io/grafana/alerting/list)

- Click [New Alert Rule](https://athena.agora-dev.w3n.io/grafana/alerting/new?returnTo=%2Falerting%2Flist)
![new alert rule](screenshots/new-alert.png)

- Select **Mimir or Loki alert** and choose **Prometheus** as the data source
![data source](screenshots/data-source.png)

- Select `Edit yaml`
![edit yaml](screenshots/alert-yaml.png)

- Paste the generated alert and Click Apply
![edit yaml](screenshots/alert-yaml-edit.png)

- Set `Namespace` and `Group` manually. These are specific to Grafana, but for consistency you should use the same values as specified in the `labels` section
![manual select](screenshots/alert-manual.png)

- Save and Exit
![save alert](screenshots/alert-save.png)

- Confirm alert has been created successfully on the [Alert rules](https://athena.agora-dev.w3n.io/grafana/alerting/list) page
![confirm alert](screenshots/alert-confirm.png)

## Sending Alerts to Slack

#### Generating Slack Webhook URL 
- From [Incoming WebHooks](https://woven-by-toyota.slack.com/apps/A0F7XDUAZ-incoming-webhooks) integration page, click “Add to Slack” button
![add to slack](screenshots/slack-add.png)

- Select channel and click add
![select channel](screenshots/slack-select.png)

- Copy the webhook URL on the following page
![copy URL](screenshots/slack-url.png)

- Base64 encode your webhook URL:
```bash
echo -n '{WEBHOOK_URL}' | base64
```

- Create a file `slack-secret.yaml` with the following contents
```yaml
apiVersion: v1
data:
  address: {BASE64_WEBHOOK_URL}
kind: Secret
metadata:
  name: slack-{YOUR_CHANNEL_NAME}
  namespace: {YOUR_NAMESPACE}
type: Opaque

```


- Apply the secret to the required namespace using
```bash
kubectl --context dev2-mgmt-east -n {YOUR_NAMESPACE} apply -f slack-secret.yaml
```
!!! Note Permissions
    Consumers of Agora do not currently have permission to perform this step. Please reach out to DevRel to perform this action. 

- Create an Alertmanager Configuration file named `alertmanager-config.yaml` in `infrastructure/k8s/environments/dev2/clusters/mgmt-east/{YOUR_NAMESPACE}` and reference it in the kustomization file in that folder. Raise a PR with this change and get it merged. The configuration should like below. Feel free to edit the text field:
```yaml
apiVersion: monitoring.coreos.com/v1alpha1
kind: AlertmanagerConfig
metadata:
  name: {YOUR_NAMESPACE}-alertmanager-config
  namespace: {YOUR_NAMESPACE}
spec:
  receivers:
    - name: slack-{YOUR_CHANNEL_NAME}
      slackConfigs:
        - apiURL:
            name: slack-{YOUR_CHANNEL_NAME}
            key: address
          iconEmoji: ":warning:"
          username: "Agora Alerts: Pre-prod"
          text: |-
            {{ range .Alerts }}
              Annotations:
                {{ range .Annotations.SortedPairs }} *{{ .Name }}*: {{ .Value }}
              {{ end }}
            {{- end }}
          title:
            '[{{ .Status | toUpper }}{{ if eq .Status "firing" }}:{{ .Alerts.Firing
            | len }}{{ end }}] {{ .CommonLabels.group }}'
  route:
    receiver: slack-{YOUR_CHANNEL_NAME}
    # How long to wait to buffer alerts of the same group before sending a notification initially.
    groupWait: 0s
    # How long to wait before sending an alert that has been added to a group for which there has already been a notification.
    groupInterval: 5m
    # How long to wait before re-sending a given alert that has already been sent in a notification.
    repeatInterval: 5m
    matchers:
      - matchType: "="
        name: group
        value: {YOUR_ALERT_GROUP_NAME}
```

## Known Issues


