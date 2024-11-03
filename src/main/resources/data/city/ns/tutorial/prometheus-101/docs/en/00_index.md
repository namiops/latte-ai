# Prometheus 101

Welcome to Prometheus 101!

## What This Tutorial Covers

This tutorial will give you an introduction to the primary toolings that Agora offers for collecting your servies' metrics, [Prometheus](https://prometheus.io/).

## Prometheus Quickly

[Prometheus](https://prometheus.io/) is a free software application used for event monitoring and alerting. It records real-time metrics in a time series database (allowing for high dimensionality) built using an HTTP pull model, with flexible queries and real-time alerting. The below architecture illustrates its overall architecture (taken from [the official docs](https://prometheus.io/docs/introduction/overview/)).

![Prometheus Architecture](./assets/prom-arch.png)

Agora team sets up Prometheus and all other related backends so that they collect metrics from your services and make them queriable all behind the scene.

### PromQL

[PromQL](https://prometheus.io/docs/prometheus/latest/querying/basics/) is the query language specific to Prometheus. You will use it to define your monitoring dashboards in Grafana and to set alerting criteria. Prometheus also provides a barebones WebUI to try out and visualize your queries quickly, which you will be seeing in a moment.

While we recommend you to go through the above official PromQL docs as the power of Prometheus stack best shines when you truly understand the query language, we will introduce to you some example queries and walk you through some core concepts along the way.

For these examples, you can use [this external service: Promlab's demo env](https://demo.promlabs.com/graph).

#### PromQL Example: Gauge

Let's start simple -- try typing in

`node_filesystem_avail_bytes`

in the _Expression_ textbox within the Prometheus WebUI. This is already a valid PromQL expression; this is one of the queries to get free disk spaces. You should have seen there is an autocomplete suggestion with fuzzy searches, which is extremely useful (if you haven't hit `Ctrl+Space` to force it to appear). Press `Enter` to execute the query, and switch to `Graph` tab. You should see something like this.

![Filesystem query result](./assets/prom-filesystem-result.png)

You should see several lines in the graph. All of these lines are about a single [**metric**](https://prometheus.io/docs/concepts/data_model/#metric-names-and-labels) (`node_filesystem_avail_bytes` in this case), and each line corresponds to a time series of [**samples**](https://prometheus.io/docs/concepts/data_model/#samples) with the same set of [**labels**](https://prometheus.io/docs/concepts/data_model/#metric-names-and-labels). If you draw a vertical line at any specific timestamp you should get multiple samples (because you have multiple lines in the graph) -- this sample set is called an [**instant vector**](https://prometheus.io/docs/prometheus/latest/querying/basics/#expression-language-data-types).

Take a look at the legends. `device`, `fstype`, `instance`, `job`, and `mountpoint` are all labels attached to the metrics. You can filter the query result by the label values by

- `node_filesystem_avail_bytes{device="/dev/vda1"}`
- `node_filesystem_avail_bytes{device="/dev/vda1",mountpoint="/"}` (filter by multiple labels)
- `node_filesystem_avail_bytes{device=~"/dev/.+"}` (use regex)
- `node_filesystem_avail_bytes{device!="/dev/vda1"}` (negate)

Try it in the demo to notice that the resulting graph shows fewer lines and that the explorer again gives you the suggestion for the possible label values as you type.

You can do some arithmetic over the time series using [functions](https://prometheus.io/docs/prometheus/latest/querying/functions/).

`sum(node_filesystem_avail_bytes)`

gives you the summation of `node_filesystem_avail_bytes` metric value across all labels (don't worry too much about what it practically means, we are just giving a syntactical example that you can apply some [functions](https://prometheus.io/docs/prometheus/latest/querying/functions/) over metric).

You can also do arithmetic over multiple metrics using [operators](https://prometheus.io/docs/prometheus/latest/querying/operators/). Suppose you want to get the percentage of the available filesystem space. You can type

`node_filesystem_avail_bytes / node_filesystem_size_bytes * 100`

Notice the number of lines is the same as the original query result for `node_filesystem_avail_bytes`. As you can imagine by this, the `/` (divided by) operator is applied to a pair of vectors with exactly the same labels and produces resulting vector for each pair (e.g. `node_filesystem_avail_bytes{device="/dev/vda1", fstype="ext4", instance="node-exporter:9100", job="node", mountpoint="/"}` matches `node_filesystem_size_bytes{device="/dev/vda1", fstype="ext4", instance="node-exporter:9100", job="node", mountpoint="/"}` then produce `{device="/dev/vda1", fstype="ext4", instance="node-exporter:9100", job="node", mountpoint="/"}` in the graph).
How operators attempt to find the matching vectors is called [**vector matching**](https://prometheus.io/docs/prometheus/latest/querying/operators/#vector-matching).

Like `node_filesystem_avail_bytes`, a metric type that represents a single numerical value that can arbitrarily go up and down is called [**Gauge**](https://prometheus.io/docs/concepts/metric_types/#gauge).

#### PromQL Example: Counter

Another metric type Prometheus offers is [**Counter**](https://prometheus.io/docs/concepts/metric_types/#counter), is a cumulative metric that represents a single monotonically increasing counter whose value can only increase or be reset to zero on restart.

Counters are idiomatically used with [Range Vector Selectors](https://prometheus.io/docs/prometheus/latest/querying/basics/#range-vector-selectors) and some related functions. Let's take a look at an example.

Type in `node_cpu_seconds_total`; this shows the total CPU time. You can see each line increases monotonically.

![CPU query result](./assets/prom-cpu-result.png)

!!! Tip
    In case they all look flat, try clicking one of the legends to show just one time series

The monotonically-increasing graph may be hard to interpret -- we may want to see the spikes in CPU load instead of the total CPU seconds. The spike means a large difference between the values at two timestamps, so we have to 1) specify the time length between those two timestamps and 2) take the difference between those values. The below query will do what we want.

`increase(node_cpu_seconds_total[5m])`

`[5m]` (m=minutes) part addresses 1), and `increase` function addresses 2). Feel free to try with different time ranges to see what happens.

#### PromQL Example: Histogram

[**Histogram**](https://prometheus.io/docs/concepts/metric_types/#histogram) is another powerful metrics type. You use this to answer such questions as "What's the 90th percentile request latency over the last 5 minutes?" Type in the below query in the demo env.

`histogram_quantile(0.9, rate(demo_api_request_duration_seconds_bucket{path="/api/foo"}[5m]))`

![Histogram query result](./assets/prom-histo-result.png)

#### How to Know the List of Metrics?

How would you know the list of metric names? It's somewhat difficult because it depends on which [exporters](https://prometheus.io/docs/instrumenting/exporters/) are installed in the cluster. At the time of this writing, [Node exporter](https://github.com/prometheus/node_exporter) and [kube-state-metrics](https://github.com/kubernetes/kube-state-metrics) are installed, so it's good to check their documentation.

Another way to answer this question is, just type in the expression textbox in the Prometheus WebUI or Grafana; as they provide fairly good fuzzy suggestions for auto-completion.

For your reference, here are some useful example queries for monitoring your Kubernetes Pod.

- **Detecting Down Pods**: `kube_pod_status_phase{namespace="<your-namespace>",pod~="<app-name>-.+",phase="Running"}`
- **Kubernetes ReplicasSet mismatch**: `kube_replicaset_spec_replicas != kube_replicaset_status_ready_replicas`
- **CPU Usage**: `rate(container_cpu_usage_seconds_total{namespace="<your-namespace">,pod~="<app-name>-.+"}[5m])`
- **Memory Usage**: `container_memory_working_set_bytes{namespace="<your-namespace">,pod~="<app-name>-.+"}`

## Where to Go Next

- Go back to [Observability 101 -> Where to Go Next](https://developer.woven-city.toyota/docs/default/Component/observability-tutorial/#where-to-go-next) section and continue your reading.

## Further Reference

- [PromQL Cheat Sheet](https://promlabs.com/promql-cheat-sheet/)
- [Awesome Prometheus alerts](https://awesome-prometheus-alerts.grep.to/): Demonstrates good & practical PromQL examples.
