# Observability 101

!!! warning
    Currently this document intended for the deprecated environments: Pre-Prod and Legacy Dev.

## What This Tutorial Covers

This tutorial is meant to provide you with basic concepts about observability, and some of the practical steps to get you started with observability in Agora, to help you understand your system's state, diagnose issues and build more reliable and performant services.

## What is Observability and Why is it Important?

> Observability is a measure of how well internal states of a system can be inferred from knowledge of its external outputs. ([Wikipedia](https://en.wikipedia.org/wiki/Observability))

The term `observability` arose in control theory and was ported to the IT industry, and it practically means how engineers can understand the current state, especially the healthiness, of a system from the data it generates (e.g. logs, metrics, traces).

!!! Tip
    The word `observability` is commonly abbreviated as `o11y`.

Observability is important because it gives you greater control over complex systems.
Modern software systems are getting more complex than ever, with faster delivery cycles developed by distributed teams. Woven City services are no exception.
Without proper tooling, it became nearly impossible to shed light on what is happening inside such complex systems. Issues may not be noticed until it gets major, and once noticed it's still difficult to spot what exactly causes the issue.
Armed with high observability, we'll have an effective way to answer such questions as "Why is X broken?" or "What is causing latency right now?", helping engineers catch issues early, diagnose them, and build more reliable and performant services.

## Why use Agora for Observability?

If you are to build your system from the ground up, you have to build your own solution to:

- Collect your infrastructure & application metrics like CPU usage, memory consumption, disk pressure, and so on.
- Aggregate logs from various services you run, and make it searchable.
- Fire alerts upon certain conditions over metrics and logs.
- Provide a way to inspect which services a certain request went through and how much time was taken in each of them
- Make these essential instruments highly reliable.
- ...a lot more

With Agora, you will gain those benefits built-in. You deploy your services, and the platform is already collecting the metrics.
You have a way to customize the dashboard to visualize the metrics to monitor the healthiness of your services.
You can configure your alert rules as per your needs.
You can also visualize the topology of your services' internal or external dependencies, viewing the traffic going through at runtime, digging deeper to diagnose the latency by looking into the traces.

We will cover how exactly you will be utilizing those features later in this tutorial.

## Observability Three Pillars

The primary data classes used in observability are logs, metrics, and traces. Together they are often called "the three pillars of observability."

> - Logs: Logs are granular, timestamped, complete and immutable records of application events that come in three formats: plain text, structured or binary.
> - Metrics: Metrics (sometimes called time-series metrics) are fundamental measures of application and system health over a given period of time, such as how much memory or CPU capacity an application uses over a five-minute span, or how much latency an application experiences during a spike in usage.
> - Traces: Traces record the end-to-end “journey” of every user request, from the UI or mobile app through the entire distributed architecture and back to the user.
>
> [IBM: What is observability?](https://www.ibm.com/cloud/learn/observability).

Agora provides a set of toolings to address each of these pillars; [Loki](https://grafana.com/logs/) for logging, [Prometheus](https://prometheus.io/) and [Grafana](https://grafana.com/grafana/) for metrics, and [Jaeger](https://www.jaegertracing.io/) for tracing.

## Where to Go Next

- Go to [Prometheus 101](https://developer.woven-city.toyota/docs/default/Component/prometheus-tutorial) to learn about our metrics-gathering platform, [Prometheus](https://prometheus.io/).
- Go to [Grafana 101](https://developer.woven-city.toyota/docs/default/Component/grafana-tutorial) to learn about our metrics-visualizing tool, [Grafana](https://grafana.com/grafana/).
- Go to Loki 101 (TBA) to learn about our log aggregation platform, [Loki](https://grafana.com/logs/).
- Go to Jaeger 101 (TBA) to learn about our distributed tracing system, [Jaeger](https://www.jaegertracing.io/).
- Go to Kiali 101 (TBA) to learn about our service mesh management console, [Kiali](https://kiali.io/).

## Further Reference

- [New Relic: What is observability?](https://newrelic.com/topics/what-is-observability): An excellent article to summarize what o11y is, its difference from monitoring, who gains benefits out of it on what, etc.
- [Splunk: What is observability? A Beginners Guide](https://www.splunk.com/en_us/data-insider/what-is-observability.html): Another brilliant observability article with slightly longer contents.
