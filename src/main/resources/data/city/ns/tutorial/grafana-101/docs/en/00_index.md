# Grafana 101

- [What This Tutorial Covers](#what-this-tutorial-covers)
- [Grafana Quickly](#grafana-quickly)
- [Grafana Hands-on](#grafana-hands-on)
  - [Pre-requisites For this Hands-on](#pre-requisites-for-this-hands-on)
  - [How to Set up A Dashboard for Your App in Grafana](#how-to-set-up-a-dashboard-for-your-app-in-grafana)
    - [Configure a Temporary Dashboard Manually to Get the Dashboard JSON](#configure-a-temporary-dashboard-manually-to-get-the-dashboard-json)
    - [Codify your Dashboard Configuration for Persistence and Portability](#codify-your-dashboard-configuration-for-persistence-and-portability)
      - [Create yaml resources using Bazel](#create-yaml-resources-using-bazel)
      - [Manully Create/Apply yaml resources](#manully-createapply-yaml-resources)
  - [How to Set up Alerts](#how-to-set-up-alerts)
- [Where to Go Next](#where-to-go-next)

## What This Tutorial Covers

This tutorial will give you an introduction to the primary toolings that Agora offers for visualizing your servies' metrics, [Grafana](https://grafana.com/grafana/).

You will also have a hands-on experience to set up a Grafana dashboard for monitoring an example service.

## Grafana Quickly

[Grafana](https://grafana.com/docs/grafana/latest/) is the primary visualization tool for Prometheus metrics in Agora. You can set up as cool dashboard as this to grasp what is going on in your services.

![Grafana Example Dashboard](./assets/graphana-ex-dashboard.png)

!!! Tip
    Precisely speaking, Grafana supports various [data sources](https://grafana.com/docs/grafana/latest/datasources/#data-sources) other than Prometheus, but at the time of this writing, Agora Grafana solely connects to Prometheus.

Please take a look at [Dashboard feature overview](https://grafana.com/docs/grafana/latest/dashboards/use-dashboards/#dashboard-feature-overview) section in the official docs to see what these icons / widgets are for.

## Grafana Hands-on

This page will walk you through how to create a sample Grafana dashboard in Agora platform.

### Pre-requisites For this Hands-on

* **Kubectl**
  * You can find instructions on how to install
    [**here**](https://kubernetes.io/docs/tasks/tools/) for Windows, Mac, and Linux
  * Set up access to [dev env](https://github.tri-ad.tech/cityos-platform/cityos/tree/main/infrastructure/k8s/dev).
* Access to [Agora Grafana](https://observability.cityos-dev.woven-planet.tech/grafana/)
  * If you don't have access, please reach out to us in [#wcm-org-agora-ama in Slack](https://woven-by-toyota.slack.com/archives/C02CVJLTMJ7) saying that you want to get access to Grafana. We will grant you access.

### How to Set up A Dashboard for Your App in Grafana

Go to [https://observability.cityos-dev.woven-planet.tech/grafana/](https://observability.cityos-dev.woven-planet.tech/grafana/), press `WovenPlanet` button under _Or sign in with_.

!!! Warning
    Note Agora team is planning a major update on how to set up dashboards in Grafana, and once this is updated Agora team might need to ask individual teams to migrate the dashboard settings on their own.

Here are the overall steps:

1. Configure a temporary dashboard manually to get the dashboard JSON
2. Codify your dashboard configuration for persistence and portability

Let's go over each of the steps in detail.

#### Configure a Temporary Dashboard Manually to Get the Dashboard JSON

You first create a dashboard with several panels as an example. You will configure the dashboard just temporarily (and discard it without saving it) to get the JSON that describes how your dashboard is configured, so that you can place the JSON into your repo for persistence and portability between different environments.

!!! Danger
    These temporarily created dashboards aren't persistent, meaning if the underlying Grafana Pod restarts, the dashboards are gone. So please make sure you follow all the steps to keep your dashboards persistent.

Adding a Pod status panel:

1. Hover over `+` button on the left-sidebar, and click `Dashboard`.
2. Click `Add a new panel`.
3. In the `Metrics browser` textbox, type in `kube_pod_status_phase{namespace="httpbin",pod=~"httpbin-.+",phase="Running"}`. You can see the graph by hitting `Shift+Enter` (`1` means it's in a good state).
4. Turn on `Instant` switch below `Metrics browser`.
5. In the right-sider (from top to bottom):
   1. Change `Time series` to `Stat`.
   2. Title: `Pod Status`
   3. Description: `Pod status for my app.`
   4. `Add value mappings`:
      - 1 => Healthy, Green
      - 0 => Unhealthy, Red
6. Hit `Apply` button on the top right of the screen.

!!! Tip
    In a real use case, change the namespace and pod query when you set this up for your own app.

!!! Tip
    If you want to edit the setting after applying it, click the panel title (`Pod Status` in this case) => `Edit`.

Adding a CPU usage panel:

1. Among the upper icons, find `Add Panel` button and hit it.
2. Click `Add a new panel`.
3. In the `Metrics browser` textbox, type in `rate(container_cpu_usage_seconds_total{namespace="httpbin",pod=~"httpbin-.*"}[5m])`.
4. In the right-sider (from top to bottom):
   1. Title: `CPU Usage`
   2. Description: `CPU usage for my app.`
5. Hit `Apply` button on the top right of the screen.

!!! Tip
    You see multiple lines for a single pod because multiple containers are running in the pod (as side cars) + the total usage.
    If you want to only see the data for your app, add `container="<your-app-name>"` in the label filter.
    If you want to only see the total usage, add `container=""` in the label filter.

Adding a memory usage panel:

1. Among the upper icons, find `Add Panel` button and hit it.
2. Click `Add a new panel`.
3. In the `Metrics browser` textbox, type in `container_memory_working_set_bytes{namespace="httpbin",pod=~"httpbin-.*"}`.
4. In the right-sider (from top to bottom):
   1. Title: `Memory Usage`
   2. Description: `Memory usage for my app.`
5. Hit `Apply` button on the top right of the screen.

Drag your panels however you like. The eventual dashboard might look something like this.

![Grafana Hands-on Dashboard](./assets/graphana-handson-dashboard.png)

Save the JSON Model:

1. Among the upper icons, find `Dashboard Settings` button and click on it.
2. Select `JSON Model` on the left navigator, and save the JSON contents as a file in your local computer (let's name it `httpbin-dashboard.json` for now).

!!! Warning
    This is a very important step. If you fail to do this, you will have to do the above steps all over again.

Close the browser tab for Grafana without saving your dashboard there.

#### Codify your Dashboard Configuration for Persistence and Portability

##### Create yaml resources using Bazel

You will require [Bazel](https://httpbin.cityos-dev.woven-planet.tech/docs/default/domain/agora-domain/development/bazel/#installing-bazel) before starting below steps

1. Create a new and empty folder named `grafana` (you can use any name but make sure it's empty)

!!! Warning
    If you are deploying your `GrafanaDashboard` in preprod cluster, make sure you create the folder mentioned above in `mgmt-east` cluster

2. Paste below `BUILD` file in the folder and change the name of the dashboard json accordingly

    ```bazel
    load("//ns/bazel_grafana:grafana_ytt.bzl", "grafana_ytt_build")

    files = [
        "httpbin-dashboard.json", # name of the json dashboard file
        "grafana_values.yaml",
    ]

    grafana_ytt_build(
        name = "grafana_ytt",
        files = files,
        output = "grafana.yaml",
    )
    ```

3. Paste below `grafana_values.yaml` file in the folder and change the values accordingly.

    ```yaml
    #@data/values
    ---
    namespace: httpbin     # your namespace
    grafanaFolders:
      - name: httpbin-grafana-folder
        title: "httpbin"     # This is the folder title, change it to your namespace name 
        dashboards:
          - name: httpbin-overview-dashboard
            jsonFile: httpbin-dashboard.json
    ```

4. Copy/Paste your dashboard json file in the folder and make sure the name matches the one you have provided in `BUILD` and `grafana_values.yaml`.

5. Create your resources by running below Bazel command inside the folder. This will create `grafana.yaml` file containing all your resources

    ```sh
    $ cd /change/to/new/directory
    $ bazel run :grafana_ytt.copy
    ```

6. Run `buildifier` and `gazelle`

    ```sh
    $ bazel run //:buildifier
    $ bazel run //:gazelle
    ```

7. Add the new `grafana` folder in the `kustomization.yaml` file 1-level above to deploy these resources through flux

You can take a look at an [example grafana dasboard deployment](https://github.com/wp-wcm/city/tree/main/infrastructure/k8s/environments/dev2/clusters/mgmt-east/developer-portal/grafana_v2) for developer portal on the monorepo.

##### Manully Create/Apply yaml resources

Now you use the dashboard JSON by creating a ConfigMap in Agora Kubernetes cluster.

!!! Note
    In real use cases, when creating Kubernetes resources in Agora, you will check in your changes to your repository so that Flux will pick them up automatically (highly recommended content if you're not familiar: [Deployment Application on Agora](https://developer.woven-city.toyota/docs/default/component/agora-deployment-tutorial/03_deployment/)). In this tutorial, for simplicity, we will walk you through temporarily creating the ad-hoc resource manually, but do check in your changes in your real use cases.

!!! Note
    The reason we design the workflow this way rather than just keeping the manually created dashboard is, just like other Infrastructure as Code efforts, we value reproducibility. You can port your configuration to different environments without relying on a manual workload which can often be error-prone.

1. Create a folder `my-httpbin-dashboard` anywhere on your PC.
2. Put `httpbin-dashboard.json` you created earlier under the folder.
3. Create `kustomization.yaml` under the folder with the following text.

    ```yaml
    apiVersion: kustomize.config.k8s.io/v1beta1
    kind: Kustomization
    namespace: tutorial # In real case, put <your-namespace>
    configMapGenerator:
    - name: httpbin-grafana-dashboard
        files:
        - httpbin-dashboard.json
    commonLabels:
    grafana_dashboard: "1"
    commonAnnotations:
    grafana_folder: tutorial # In real case, put <your-namespace>
    ```

4. Run the following commands.

    ```sh
    cd <path-to>/my-httpbin-dashboard

    # Check the manifest
    kubectl apply -k . --dry-run=client -o yaml

    # Apply the manifest
    kubectl apply -k .
    ```

5. Wait for a minute or two, and check out <https://observability.cityos-dev.woven-planet.tech/grafana/dashboards> to confirm your dashboard is there.

!!! Danger
    Please be 100% sure `grafana_folder` is set to your owned namespace in your real use case. Conflicting folder & dashboard names will lead to overriding the existing dashboard that another team might own. We plan to have prevention in this regard but is not currently placed.

Well done! This is the end of creating your dashboard. As said, you will check your changes in your repo in official use cases.
Now let's clean up your temporarily created resources.

1. `kubectl delete -k .`

### How to Set up Alerts

We are sorry, but setting up alerts is not officially supported at this moment (and is soon to be established). In case you disparately need ones right now, please reach out to us at #wcm-agora-team-ama.

## Where to Go Next

- Go back to [Observability 101 -> Where to Go Next](https://developer.woven-city.toyota/docs/default/Component/observability-tutorial/#where-to-go-next) section and continue your reading.
