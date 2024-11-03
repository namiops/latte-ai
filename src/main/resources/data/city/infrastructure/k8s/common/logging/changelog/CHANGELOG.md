# 08.06.2023 10:00

## Fluent-Bit

* Delete not-needed POD
* Change DNS configuration for Fluent-Bit as a part of Agora Revision 4 without and with SOC

```diff
diff -rupd fluent-bit-0.21.0-agora2/configmap-fluent-bit.yaml fluent-bit-0.21.0-agora4/configmap-fluent-bit.yaml
--- fluent-bit-0.21.0-agora2/configmap-fluent-bit.yaml  2023-04-11 08:47:15.000000000 +0900
+++ fluent-bit-0.21.0-agora4/configmap-fluent-bit.yaml  2023-06-08 09:59:25.000000000 +0900
@@ -66,3 +66,5 @@ data:
         Tls Off
         Net.connect_timeout 60
         Net.keepalive_max_recycle 4000
+        Net.dns.prefer_ipv4 true
+        Net.dns.resolver LEGACY
```
# 13.02.2023 15:30

## Loki

* Added Zone Topology Constraints for `Loki Read` and `Loki Write` StateFulsets as a part of Agora Revision 4

```diff
diff -rup ../loki-3.8.0-agora2/kustomization.yaml ../loki-3.8.0-agora4/kustomization.yaml
--- ../loki-3.8.0-agora2/kustomization.yaml     2023-02-09 14:30:15.000000000 +0900
+++ ./kustomization.yaml        2023-02-13 15:25:50.000000000 +0900
@@ -21,3 +21,6 @@ resources:
   - virtualservice-loki.yaml
 patchesStrategicMerge:
   - loki-patch.yaml
+patches: 
+  - patches/statefulset-loki-read.yaml 
+  - patches/statefulset-loki-write.yaml
Only in ../loki-3.8.0-agora4/: patches
```

# 20.12.2022 11:45

## Fluent-bit 
* Increased Buffer size from 32k to 128k on Filtering of Kubernetes, as this generates bunch of spam

# 20.12.2022 10:00

# Major Changes

## Loki

### General

* Upgraded version from 2.6.1 to 2.7.1
  * https://github.com/grafana/loki/blob/main/CHANGELOG.md
  * Main reason: Fixed caching mechanism and parallizer in querier and spanlog spam in logs
* GRPC Configuration
  * Increased max and send grpc msg size from `16777216`B to `204857600`B
  * Enabled compression `snappy`, as default was no compression
* Added ServiceMonitor and PrometheusRules for agora-injected-prom, federated to main Prometheus
* Set up limit for 46Gi for Loki writes, and request for 24Gi
* Set up limit for 12Gi for Loki-reads and request for 8Gi
* Increased PVC side for Loki-readfs from 10Gi to 100Gi
* Increased PVC size for Loki-writes from 100Gi to 300Gi
* Added In istio virtual-services and authorization policy new prefixes for loki components identified during tests, and not-documented at all on loki page
* Added storageClass dedicated for loki using gp3
* Disabled logging in local as its too greedy for resources
 
### General Limits Config
* Disabled enforcing metric name, as we are using fluent-bit, so we will have this check at this stage, and this is pretty greedy from a CPU perspective
* Increased `ingestion_burst_size_mb` from 6MB to 254MB as `fluent-bit` sometimes needs 250MB as a max
* Increased `ingestion_rate_mb` to 128MB as by default, `fluent-bit` is using 64MB 
* Decreased `max_chunks_per_query` from 2000000 to 100000 as Loki tried to fetch too much data for the current possibilities of connection between node and S3
* Increased `max_concurrent_tail_requests` from 10 to 96, as we have 8 CPUs; one `concurrent_request` need `1/12` of CPU. And this is a child of the primary process, so when we have `parallelism` set to 8, we need to adjust the value this, ten as an effect gives the situation when threads are doing nothing. 
* Increased `max_entries_limit_per_query` from 5000 to 50000, as this is needed for a better User Experience
* Decreased `max_global_streams_per_user` from 5000 to 1200 because of a weird mechanism doing streams, every time, I observed reaching 75% with even one user.
* Set `max_line_size` to 0 as we do not want to cut logs at all
* Set `max_query_length` to 721h as 30 days of logs in one query should be enough for a good user experience. Now, I would like to admit - 30 days, for example, one month in one query. Accessibility still is for 180 days still available
* Set `max_query_parallelism` to 8 because we have 8vCPU; the default was 32vCPU, so many threads waited constantly doing delay, and increased usage of memory of local queue cache of data
* Decreased `max_query_series` from 500 to 250, as we are not using that much. and with `edge` syntax, this could blow up memory
* Increased `per_stream_rate_limit` from 3MB to 64MB, as `fluent-bit` needs it
* Increased `per_stream_rate_limit_burst` from 15MB to 128MB, as `fluent-bit` needs it
* Increased `query_timeout` from 1 minute to 15 minutes, as some specific queries need more time to do, and this limit is not real 15 minutes in the code
* Decreased `reject_old_samples_max_age` from 168h to 48h as we do not want to ingest that old logs
* Increased `split_queries_by_interval` from 15m to 1h as we are using sharding
* Enabled sharding, divide query by 8MB 
```
  # The formula used is n = ceil(stream size + ingested rate / desired rate), where n is the number of shards.
  # For instance, if a stream ingestion is at 10MB, desired rate is 3MB (default), and a stream of size 1MB is
  # received, the given stream will be split into n = ceil((1 + 10)/3) = 4 shards.
  ```
 
### Frontend Component
* Added compression for responses (200MB vs. 16MB)
* Added logging slow queries, more than 300s, so 1/3 of timeout

### Frontend Worker
* Set up parallelism to 8, as underneath we have 8vCPU, the default was 10, so two threads waited constantly doing delay, and increased usage of memory of local queue cache of data
*  Enabling max concurrent by default is not enabled, even if in documentation is stated is enabled

### Ingester
* Enable LZ4 compression
* Usage of network drop from 1Gbps to 0.5Gbps

### Ingester Client
* Increase the cleanup period from 15s to 600s, as one session takes minimum 450s
* Enabled health check of ingesters because, by default, this is disabled, so sometimes Loki tried to write to non-existing TCP connections; as an effect, we got a memory leak
* Increase remote_timeout to 300s because sometimes POD needs a few minutes to clean up and be ready

### Memberlist
* Increased `dead_node_reclaim_time` from 0s to 30s as sometimes readiness failed because of it
* Increased `gossip_to_dead_nodes_time` from 0s to 30s as sometimes dead node is not dead
* Changed `max_join_retries ` from 10 to 30 as sometimes the dead node is not dead

### Querier
* Increased `max_concurrent` from 10 to 64, as this parameter gives the best value during tests
* Decreased `query_ingesters_within` from 3h to 2h, as because of the amount of data, 2 hours is usually cached data in-mem of ingesters
* Decreased `tail_max_duration` from 1 hour to 1 min as this is for live logs, and 1 hour is dangerous for the local CPU of mac - crashed for my browser

#### Engine
* Increased timeout from 3 minutes to 900s for massive slow queries, like two days logs from the whole cluster
* Increased `max_look_back_period` from 30s to 1min for specific case from IoT team

### Query Range
* Enabled embedded 4GB cache for results of queries
* Set up validity of this cache for 1 hour

### Query Scheduler
* Increased `max_outstanding_requests_per_tenant` from 100 to 4096 as this value limited 10 people to use Loki in same time

### Server 
* Increased `http_server_idle_timeout` to 600s from 120s
* Increased `http_server_read_timeout` to 900s from 30s
* Increased `http_server_write_timeout` to 60s from 30s

### Storage Config
* Increased TTL for 48hours for BoltDB Shipper from 24 hours for situations when Loki will be down, as 6 hours downtime = 24 hours delay
* Increased `max_parallel_get_chunk` from 150 to 1000, this value is not even documented, and the name is confusing, but this is parameter telling about how many TCP go-routine connection could be in-use for fetching data from S3 - with 150 many queries, especially IoT use-case could fail, as well as 2 days from whole cluster

## Fluent-bit
* Added alerts for agora-injected-prom federated to main prom
* Added service monitor for agora-injected-prom to get metrics from all fluent-bit instances federated to main prom
* Changed level of metrics from `debug` to `info

# Diffs

## Loki

```diff
diff -du ../loki-3.2.1-agora4/README.md ../loki-3.8.0-agora2/README.md
--- ../loki-3.2.1-agora4/README.md	2022-11-16 16:41:22.000000000 +0900
+++ ../loki-3.8.0-agora2/README.md	2022-12-20 08:39:22.000000000 +0900
@@ -1,4 +1,4 @@
-# loki-3.2.1-agora4
+# loki-3.8.0-agora2
 
-Generated by "https://github.tri-ad.tech/cityos-platform/infrastructure/k8s/common/logging/bin/import -t loki -f loki-values-agora4.yaml -v 3.2.1 -r agora4"
-using Values from "https://github.tri-ad.tech/cityos-platform/infrastructure/k8s/common/logging/bin/loki-values-agora4.yaml"
+Generated by "https://github.tri-ad.tech/cityos-platform/infrastructure/k8s/common/logging/bin/import -t loki -f loki-values-3.8.0-agora2.yaml -r agora2"
+using Values from "https://github.tri-ad.tech/cityos-platform/infrastructure/k8s/common/logging/bin/loki-values-3.8.0-agora2.yaml"
diff -du ../loki-3.2.1-agora4/configmap-loki.yaml ../loki-3.8.0-agora2/configmap-loki.yaml
--- ../loki-3.2.1-agora4/configmap-loki.yaml	2022-11-16 16:41:22.000000000 +0900
+++ ../loki-3.8.0-agora2/configmap-loki.yaml	2022-12-20 08:39:22.000000000 +0900
@@ -5,10 +5,10 @@
 metadata:
   name: loki
   labels:
-    helm.sh/chart: loki-3.2.1
+    helm.sh/chart: loki-3.8.0
     app.kubernetes.io/name: loki
     app.kubernetes.io/instance: loki
-    app.kubernetes.io/version: "2.6.1"
+    app.kubernetes.io/version: "2.7.0"
     app.kubernetes.io/managed-by: Helm
 data:
   config.yaml: |
@@ -24,17 +24,75 @@
           insecure: false
           region: ${agora_environment_region}
           s3forcepathstyle: false
+    frontend:
+      compress_responses: true
+      log_queries_longer_than: 300s
+    frontend_worker:
+      grpc_client_config:
+        grpc_compression: snappy
+        max_recv_msg_size: 204857600
+        max_send_msg_size: 204857600
+      match_max_concurrent: true
+      parallelism: 8
+    ingester:
+      chunk_encoding: lz4
+    ingester_client:
+      pool_config:
+        client_cleanup_period: 600s
+        health_check_ingesters: true
+        remote_timeout: 300s
     limits_config:
       enforce_metric_name: false
+      ingestion_burst_size_mb: 254
+      ingestion_rate_mb: 128
+      ingestion_rate_strategy: local
       max_cache_freshness_per_query: 10m
+      max_chunks_per_query: 100000
+      max_concurrent_tail_requests: 96
+      max_entries_limit_per_query: 50000
+      max_global_streams_per_user: 12000
+      max_line_size: 0
+      max_query_length: 721h
+      max_query_parallelism: 8
+      max_query_series: 250
+      per_stream_rate_limit: 64MB
+      per_stream_rate_limit_burst: 128MB
+      query_timeout: 15m
       reject_old_samples: true
-      reject_old_samples_max_age: 168h
-      split_queries_by_interval: 15m
+      reject_old_samples_max_age: 48h
+      shard_streams:
+        desired_rate: 8MB
+        enabled: true
+        logging_enabled: false
+      split_queries_by_interval: 1h
     memberlist:
+      dead_node_reclaim_time: 30s
+      gossip_to_dead_nodes_time: 30s
       join_members:
       - loki-memberlist
+      left_ingesters_timeout: 300s
+      max_join_backoff: 1m
+      max_join_retries: 30
+    querier:
+      engine:
+        max_look_back_period: 1m
+        timeout: 900s
+      max_concurrent: 64
+      query_ingesters_within: 2h
+      tail_max_duration: 15m
     query_range:
-      align_queries_with_step: true
+      cache_results: true
+      results_cache:
+        cache:
+          embedded_cache:
+            enabled: true
+            max_size_mb: 4096
+    query_scheduler:
+      grpc_client_config:
+        grpc_compression: snappy
+        max_recv_msg_size: 204857600
+        max_send_msg_size: 204857600
+      max_outstanding_requests_per_tenant: 4096
     ruler:
       alertmanager_url: prometheus-alertmanager.observability.svc.cluster.local:80
       enable_alertmanager_v2: true
@@ -52,6 +110,7 @@
           insecure: false
           region: ${agora_environment_region}
           s3forcepathstyle: false
+        type: s3
     schema_config:
       configs:
       - from: "2022-01-11"
@@ -63,9 +122,17 @@
         store: boltdb-shipper
     server:
       grpc_listen_port: 9095
+      grpc_server_max_recv_msg_size: 204857600
+      grpc_server_max_send_msg_size: 204857600
       http_listen_port: 3100
+      http_server_idle_timeout: 600s
+      http_server_read_timeout: 900s
+      http_server_write_timeout: 60s
     storage_config:
+      boltdb_shipper:
+        cache_ttl: 48h
       hedging:
         at: 250ms
         max_per_second: 20
         up_to: 3
+      max_parallel_get_chunk: 1000
diff -du ../loki-3.2.1-agora4/kustomization.yaml ../loki-3.8.0-agora2/kustomization.yaml
--- ../loki-3.2.1-agora4/kustomization.yaml	2022-11-16 16:41:22.000000000 +0900
+++ ../loki-3.8.0-agora2/kustomization.yaml	2022-12-20 08:39:22.000000000 +0900
@@ -5,6 +5,7 @@
   - configmap-loki.yaml
   - poddisruptionbudget-loki-read.yaml
   - poddisruptionbudget-loki-write.yaml
+  - prometheusrule-loki-alerts.yaml
   - prometheusrule-loki-rules.yaml
   - service-loki-memberlist.yaml
   - service-loki-read-headless.yaml
@@ -13,6 +14,7 @@
   - service-loki-write.yaml
   - serviceaccount-loki.yaml
   - servicemonitor-loki.yaml
+  - storageclass-loki-ebs-sc.yaml
   - statefulset-loki-read.yaml
   - statefulset-loki-write.yaml
   - service-loki.yaml
diff -du ../loki-3.2.1-agora4/poddisruptionbudget-loki-read.yaml ../loki-3.8.0-agora2/poddisruptionbudget-loki-read.yaml
--- ../loki-3.2.1-agora4/poddisruptionbudget-loki-read.yaml	2022-11-16 16:41:22.000000000 +0900
+++ ../loki-3.8.0-agora2/poddisruptionbudget-loki-read.yaml	2022-12-20 08:39:22.000000000 +0900
@@ -5,10 +5,10 @@
 metadata:
   name: loki-read
   labels:
-    helm.sh/chart: loki-3.2.1
+    helm.sh/chart: loki-3.8.0
     app.kubernetes.io/name: loki
     app.kubernetes.io/instance: loki
-    app.kubernetes.io/version: "2.6.1"
+    app.kubernetes.io/version: "2.7.0"
     app.kubernetes.io/managed-by: Helm
     app.kubernetes.io/component: read
 spec:
diff -du ../loki-3.2.1-agora4/poddisruptionbudget-loki-write.yaml ../loki-3.8.0-agora2/poddisruptionbudget-loki-write.yaml
--- ../loki-3.2.1-agora4/poddisruptionbudget-loki-write.yaml	2022-11-16 16:41:22.000000000 +0900
+++ ../loki-3.8.0-agora2/poddisruptionbudget-loki-write.yaml	2022-12-20 08:39:22.000000000 +0900
@@ -5,10 +5,10 @@
 metadata:
   name: loki-write
   labels:
-    helm.sh/chart: loki-3.2.1
+    helm.sh/chart: loki-3.8.0
     app.kubernetes.io/name: loki
     app.kubernetes.io/instance: loki
-    app.kubernetes.io/version: "2.6.1"
+    app.kubernetes.io/version: "2.7.0"
     app.kubernetes.io/managed-by: Helm
     app.kubernetes.io/component: write
 spec:
Only in ../loki-3.8.0-agora2: prometheusrule-loki-alerts.yaml
diff -du ../loki-3.2.1-agora4/prometheusrule-loki-rules.yaml ../loki-3.8.0-agora2/prometheusrule-loki-rules.yaml
--- ../loki-3.2.1-agora4/prometheusrule-loki-rules.yaml	2022-11-16 16:41:22.000000000 +0900
+++ ../loki-3.8.0-agora2/prometheusrule-loki-rules.yaml	2022-12-20 08:39:22.000000000 +0900
@@ -4,10 +4,10 @@
 kind: PrometheusRule
 metadata:
   labels:
-    helm.sh/chart: loki-3.2.1
+    helm.sh/chart: loki-3.8.0
     app.kubernetes.io/name: loki
     app.kubernetes.io/instance: loki
-    app.kubernetes.io/version: "2.6.1"
+    app.kubernetes.io/version: "2.7.0"
     app.kubernetes.io/managed-by: Helm
     agora-prom-injected: "true"
   name: loki-rules
@@ -17,78 +17,74 @@
     - name: loki_rules
       rules:
         - expr: histogram_quantile(0.99, sum(rate(loki_request_duration_seconds_bucket[1m])) by (le, job))
+          labels:
+            cluster: loki
           record: job:loki_request_duration_seconds:99quantile
         - expr: histogram_quantile(0.50, sum(rate(loki_request_duration_seconds_bucket[1m])) by (le, job))
+          labels:
+            cluster: loki
           record: job:loki_request_duration_seconds:50quantile
         - expr: sum(rate(loki_request_duration_seconds_sum[1m])) by (job) / sum(rate(loki_request_duration_seconds_count[1m])) by (job)
+          labels:
+            cluster: loki
           record: job:loki_request_duration_seconds:avg
         - expr: sum(rate(loki_request_duration_seconds_bucket[1m])) by (le, job)
+          labels:
+            cluster: loki
           record: job:loki_request_duration_seconds_bucket:sum_rate
         - expr: sum(rate(loki_request_duration_seconds_sum[1m])) by (job)
+          labels:
+            cluster: loki
           record: job:loki_request_duration_seconds_sum:sum_rate
         - expr: sum(rate(loki_request_duration_seconds_count[1m])) by (job)
+          labels:
+            cluster: loki
           record: job:loki_request_duration_seconds_count:sum_rate
         - expr: histogram_quantile(0.99, sum(rate(loki_request_duration_seconds_bucket[1m])) by (le, job, route))
+          labels:
+            cluster: loki
           record: job_route:loki_request_duration_seconds:99quantile
         - expr: histogram_quantile(0.50, sum(rate(loki_request_duration_seconds_bucket[1m])) by (le, job, route))
+          labels:
+            cluster: loki
           record: job_route:loki_request_duration_seconds:50quantile
         - expr: sum(rate(loki_request_duration_seconds_sum[1m])) by (job, route) / sum(rate(loki_request_duration_seconds_count[1m])) by (job, route)
+          labels:
+            cluster: loki
           record: job_route:loki_request_duration_seconds:avg
         - expr: sum(rate(loki_request_duration_seconds_bucket[1m])) by (le, job, route)
+          labels:
+            cluster: loki
           record: job_route:loki_request_duration_seconds_bucket:sum_rate
         - expr: sum(rate(loki_request_duration_seconds_sum[1m])) by (job, route)
+          labels:
+            cluster: loki
           record: job_route:loki_request_duration_seconds_sum:sum_rate
         - expr: sum(rate(loki_request_duration_seconds_count[1m])) by (job, route)
+          labels:
+            cluster: loki
           record: job_route:loki_request_duration_seconds_count:sum_rate
         - expr: histogram_quantile(0.99, sum(rate(loki_request_duration_seconds_bucket[1m])) by (le, namespace, job, route))
+          labels:
+            cluster: loki
           record: namespace_job_route:loki_request_duration_seconds:99quantile
         - expr: histogram_quantile(0.50, sum(rate(loki_request_duration_seconds_bucket[1m])) by (le, namespace, job, route))
+          labels:
+            cluster: loki
           record: namespace_job_route:loki_request_duration_seconds:50quantile
         - expr: sum(rate(loki_request_duration_seconds_sum[1m])) by (namespace, job, route) / sum(rate(loki_request_duration_seconds_count[1m])) by (namespace, job, route)
+          labels:
+            cluster: loki
           record: namespace_job_route:loki_request_duration_seconds:avg
         - expr: sum(rate(loki_request_duration_seconds_bucket[1m])) by (le, namespace, job, route)
+          labels:
+            cluster: loki
           record: namespace_job_route:loki_request_duration_seconds_bucket:sum_rate
         - expr: sum(rate(loki_request_duration_seconds_sum[1m])) by (namespace, job, route)
+          labels:
+            cluster: loki
           record: namespace_job_route:loki_request_duration_seconds_sum:sum_rate
         - expr: sum(rate(loki_request_duration_seconds_count[1m])) by (namespace, job, route)
-          record: namespace_job_route:loki_request_duration_seconds_count:sum_rate
-    - name: loki_alerts
-      rules:
-        - alert: LokiRequestErrors
-          annotations:
-            message: |
-              {{ $labels.job }} {{ $labels.route }} is experiencing {{ printf "%.2f" $value }}% errors.
-          expr: |
-            100 * sum(rate(loki_request_duration_seconds_count{status_code=~"5.."}[1m])) by (namespace, job, route)
-              /
-            sum(rate(loki_request_duration_seconds_count[1m])) by (namespace, job, route)
-              > 10
-          for: 15m
-          labels:
-            severity: critical
-        - alert: LokiRequestPanics
-          annotations:
-            message: |
-              {{ $labels.job }} is experiencing {{ printf "%.2f" $value }}% increase of panics.
-          expr: |
-            sum(increase(loki_panic_total[10m])) by (namespace, job) > 0
-          labels:
-            severity: critical
-        - alert: LokiRequestLatency
-          annotations:
-            message: |
-              {{ $labels.job }} {{ $labels.route }} is experiencing {{ printf "%.2f" $value }}s 99th percentile latency.
-          expr: |
-            namespace_job_route:loki_request_duration_seconds:99quantile{route!~"(?i).*tail.*"} > 1
-          for: 15m
-          labels:
-            severity: critical
-        - alert: LokiTooManyCompactorsRunning
-          annotations:
-            message: |
-              {{ $labels.namespace }} has had {{ printf "%.0f" $value }} compactors running for more than 5m. Only one compactor should run at a time.
-          expr: |
-            sum(loki_boltdb_shipper_compactor_running) by (namespace) > 1
-          for: 5m
           labels:
-            severity: warning
+            cluster: loki
+          record: namespace_job_route:loki_request_duration_seconds_count:sum_rate
diff -du ../loki-3.2.1-agora4/service-loki-memberlist.yaml ../loki-3.8.0-agora2/service-loki-memberlist.yaml
--- ../loki-3.2.1-agora4/service-loki-memberlist.yaml	2022-11-16 16:41:22.000000000 +0900
+++ ../loki-3.8.0-agora2/service-loki-memberlist.yaml	2022-12-20 08:39:22.000000000 +0900
@@ -5,10 +5,10 @@
 metadata:
   name: loki-memberlist
   labels:
-    helm.sh/chart: loki-3.2.1
+    helm.sh/chart: loki-3.8.0
     app.kubernetes.io/name: loki
     app.kubernetes.io/instance: loki
-    app.kubernetes.io/version: "2.6.1"
+    app.kubernetes.io/version: "2.7.0"
     app.kubernetes.io/managed-by: Helm
 spec:
   type: ClusterIP
diff -du ../loki-3.2.1-agora4/service-loki-read.yaml ../loki-3.8.0-agora2/service-loki-read.yaml
--- ../loki-3.2.1-agora4/service-loki-read.yaml	2022-11-16 16:41:22.000000000 +0900
+++ ../loki-3.8.0-agora2/service-loki-read.yaml	2022-12-20 08:39:22.000000000 +0900
@@ -5,10 +5,10 @@
 metadata:
   name: loki-read
   labels:
-    helm.sh/chart: loki-3.2.1
+    helm.sh/chart: loki-3.8.0
     app.kubernetes.io/name: loki
     app.kubernetes.io/instance: loki
-    app.kubernetes.io/version: "2.6.1"
+    app.kubernetes.io/version: "2.7.0"
     app.kubernetes.io/managed-by: Helm
     app.kubernetes.io/component: read
 spec:
diff -du ../loki-3.2.1-agora4/service-loki-write.yaml ../loki-3.8.0-agora2/service-loki-write.yaml
--- ../loki-3.2.1-agora4/service-loki-write.yaml	2022-11-16 16:41:22.000000000 +0900
+++ ../loki-3.8.0-agora2/service-loki-write.yaml	2022-12-20 08:39:22.000000000 +0900
@@ -5,10 +5,10 @@
 metadata:
   name: loki-write
   labels:
-    helm.sh/chart: loki-3.2.1
+    helm.sh/chart: loki-3.8.0
     app.kubernetes.io/name: loki
     app.kubernetes.io/instance: loki
-    app.kubernetes.io/version: "2.6.1"
+    app.kubernetes.io/version: "2.7.0"
     app.kubernetes.io/managed-by: Helm
     app.kubernetes.io/component: write
 spec:
diff -du ../loki-3.2.1-agora4/serviceaccount-loki.yaml ../loki-3.8.0-agora2/serviceaccount-loki.yaml
--- ../loki-3.2.1-agora4/serviceaccount-loki.yaml	2022-11-16 16:41:22.000000000 +0900
+++ ../loki-3.8.0-agora2/serviceaccount-loki.yaml	2022-12-20 08:39:22.000000000 +0900
@@ -5,9 +5,9 @@
 metadata:
   name: loki
   labels:
-    helm.sh/chart: loki-3.2.1
+    helm.sh/chart: loki-3.8.0
     app.kubernetes.io/name: loki
     app.kubernetes.io/instance: loki
-    app.kubernetes.io/version: "2.6.1"
+    app.kubernetes.io/version: "2.7.0"
     app.kubernetes.io/managed-by: Helm
 automountServiceAccountToken: true
diff -du ../loki-3.2.1-agora4/servicemonitor-loki.yaml ../loki-3.8.0-agora2/servicemonitor-loki.yaml
--- ../loki-3.2.1-agora4/servicemonitor-loki.yaml	2022-11-16 16:41:22.000000000 +0900
+++ ../loki-3.8.0-agora2/servicemonitor-loki.yaml	2022-12-20 08:39:22.000000000 +0900
@@ -5,10 +5,10 @@
 metadata:
   name: loki
   labels:
-    helm.sh/chart: loki-3.2.1
+    helm.sh/chart: loki-3.8.0
     app.kubernetes.io/name: loki
     app.kubernetes.io/instance: loki
-    app.kubernetes.io/version: "2.6.1"
+    app.kubernetes.io/version: "2.7.0"
     app.kubernetes.io/managed-by: Helm
     agora-prom-injected: "true"
 spec:
@@ -31,4 +31,11 @@
           targetLabel: job
         - replacement: "loki"
           targetLabel: cluster
-      scheme: http
+        - replacement: ${agora_environment_cluster}
+          targetLabel: cluster
+      scheme: https
+      tlsConfig:
+        caFile: /etc/prom-certs/root-cert.pem
+        certFile: /etc/prom-certs/cert-chain.pem
+        insecureSkipVerify: true
+        keyFile: /etc/prom-certs/key.pem
diff -du ../loki-3.2.1-agora4/statefulset-loki-read.yaml ../loki-3.8.0-agora2/statefulset-loki-read.yaml
--- ../loki-3.2.1-agora4/statefulset-loki-read.yaml	2022-11-16 16:41:22.000000000 +0900
+++ ../loki-3.8.0-agora2/statefulset-loki-read.yaml	2022-12-20 08:39:22.000000000 +0900
@@ -6,10 +6,10 @@
   name: loki-read
   labels:
     app.kubernetes.io/part-of: memberlist
-    helm.sh/chart: loki-3.2.1
+    helm.sh/chart: loki-3.8.0
     app.kubernetes.io/name: loki
     app.kubernetes.io/instance: loki
-    app.kubernetes.io/version: "2.6.1"
+    app.kubernetes.io/version: "2.7.0"
     app.kubernetes.io/managed-by: Helm
     app.kubernetes.io/component: read
 spec:
@@ -28,14 +28,13 @@
   template:
     metadata:
       annotations:
-        checksum/config: 245f54037a7aad323b9b7652d5cb1a6ec759640abd1321f4fea0d4ef53c2c4e7
-        prometheus.io/port: "3100"
-        prometheus.io/scrape: "true"
+        checksum/config: 8c5227426829bc04b482d03bdcdc34be6bae64c2fa78b3e619ae1c26d7a4f21d
       labels:
         app.kubernetes.io/part-of: memberlist
         app.kubernetes.io/name: loki
         app.kubernetes.io/instance: loki
         app.kubernetes.io/component: read
+        name: read
     spec:
       serviceAccountName: loki
       automountServiceAccountToken: true
@@ -47,7 +46,7 @@
       terminationGracePeriodSeconds: 30
       containers:
         - name: read
-          image: docker.artifactory-ha.tri-ad.tech:443/grafana/loki:2.6.1
+          image: docker.artifactory-ha.tri-ad.tech:443/grafana/loki:2.7.1
           imagePullPolicy: IfNotPresent
           args:
             - -config.file=/etc/loki/config/config.yaml
@@ -81,7 +80,11 @@
               mountPath: /tmp
             - name: data
               mountPath: /var/loki
-          resources: {}
+          resources:
+            limits:
+              memory: 12Gi
+            requests:
+              memory: 8Gi
       affinity:
         podAntiAffinity:
           requiredDuringSchedulingIgnoredDuringExecution:
@@ -115,6 +118,7 @@
       spec:
         accessModes:
           - ReadWriteOnce
+        storageClassName: loki-ebs-sc
         resources:
           requests:
-            storage: "10Gi"
+            storage: "100Gi"
diff -du ../loki-3.2.1-agora4/statefulset-loki-write.yaml ../loki-3.8.0-agora2/statefulset-loki-write.yaml
--- ../loki-3.2.1-agora4/statefulset-loki-write.yaml	2022-11-16 16:41:22.000000000 +0900
+++ ../loki-3.8.0-agora2/statefulset-loki-write.yaml	2022-12-20 08:39:22.000000000 +0900
@@ -5,10 +5,10 @@
 metadata:
   name: loki-write
   labels:
-    helm.sh/chart: loki-3.2.1
+    helm.sh/chart: loki-3.8.0
     app.kubernetes.io/name: loki
     app.kubernetes.io/instance: loki
-    app.kubernetes.io/version: "2.6.1"
+    app.kubernetes.io/version: "2.7.0"
     app.kubernetes.io/managed-by: Helm
     app.kubernetes.io/component: write
     app.kubernetes.io/part-of: memberlist
@@ -28,13 +28,12 @@
   template:
     metadata:
       annotations:
-        checksum/config: 245f54037a7aad323b9b7652d5cb1a6ec759640abd1321f4fea0d4ef53c2c4e7
-        prometheus.io/port: "3100"
-        prometheus.io/scrape: "true"
+        checksum/config: 8c5227426829bc04b482d03bdcdc34be6bae64c2fa78b3e619ae1c26d7a4f21d
       labels:
         app.kubernetes.io/name: loki
         app.kubernetes.io/instance: loki
         app.kubernetes.io/component: write
+        name: write
         app.kubernetes.io/part-of: memberlist
     spec:
       serviceAccountName: loki
@@ -47,7 +46,7 @@
       terminationGracePeriodSeconds: 300
       containers:
         - name: write
-          image: docker.artifactory-ha.tri-ad.tech:443/grafana/loki:2.6.1
+          image: docker.artifactory-ha.tri-ad.tech:443/grafana/loki:2.7.1
           imagePullPolicy: IfNotPresent
           args:
             - -config.file=/etc/loki/config/config.yaml
@@ -79,7 +78,11 @@
               mountPath: /etc/loki/config
             - name: data
               mountPath: /var/loki
-          resources: {}
+          resources:
+            limits:
+              memory: 46Gi
+            requests:
+              memory: 24Gi
       affinity:
         podAntiAffinity:
           requiredDuringSchedulingIgnoredDuringExecution:
@@ -111,6 +114,7 @@
       spec:
         accessModes:
           - ReadWriteOnce
+        storageClassName: loki-ebs-sc
         resources:
           requests:
-            storage: "10Gi"
+            storage: "300Gi"
Only in ../loki-3.8.0-agora2: storageclass-loki-ebs-sc.yaml
diff -du ../loki-3.2.1-agora4/virtualservice-loki.yaml ../loki-3.8.0-agora2/virtualservice-loki.yaml
--- ../loki-3.2.1-agora4/virtualservice-loki.yaml	2022-11-16 16:41:22.000000000 +0900
+++ ../loki-3.8.0-agora2/virtualservice-loki.yaml	2022-12-20 08:39:22.000000000 +0900
@@ -61,10 +61,13 @@
             prefix: "/prometheus/api/v1/alerts"
           port: 3111
         - uri:
-            prefix: "/ruler/ring"
+            prefix: "/loki/api/v1/status/buildinfo"
           port: 3111
         - uri:
-            prefix: "/loki/api/v1/status/buildinfo"
+            prefix: "/compactor/"
+          port: 3111
+        - uri:
+            prefix: "/scheduler/"
           port: 3111
       route:
         - destination:
@@ -74,14 +77,23 @@
     - name: loki-write
       match:
         - uri:
-            prefix: "/loki/api/v1/push"
+            prefix: "/ruler/ring"
           port: 3111
         - uri:
-            prefix: "/loki/api/v1/rules/"
+            prefix: "/loki/api/v1/push"
           port: 3111
         - uri:
             prefix: "/api/prom/push"
           port: 3111
+        - uri:
+            prefix: "/distributor/"
+          port: 3111
+        - uri:
+            prefix: "/ingester/"
+          port: 3111
+        - uri:
+            prefix: "/ring"
+          port: 3111
       route:
         - destination:
             host: loki-write.logging.svc.cluster.local
```

## Fluent-bit

```diff
diff -du ../fluent-bit-0.20.9-agora3/README.md ../fluent-bit-0.21.0-agora1/README.md
--- ../fluent-bit-0.20.9-agora3/README.md	2022-11-16 16:41:22.000000000 +0900
+++ ../fluent-bit-0.21.0-agora1/README.md	2022-12-20 08:26:43.000000000 +0900
@@ -1,4 +1,4 @@
-# fluent-bit-0.20.9-agora3
+# fluent-bit-0.21.0-agora1
 
-Generated by "https://github.tri-ad.tech/cityos-platform/infrastructure/k8s/common/logging/bin/import -t fluent-bit -f fluent-bit-values-agora3.yaml -r agora3"
-using Values from "https://github.tri-ad.tech/cityos-platform/infrastructure/k8s/common/logging/bin/fluent-bit-values-agora3.yaml"
+Generated by "https://github.tri-ad.tech/cityos-platform/infrastructure/k8s/common/logging/bin/import -t fluent-bit -f fluent-bit-values-0.21.0-agora1.yaml -r agora1"
+using Values from "https://github.tri-ad.tech/cityos-platform/infrastructure/k8s/common/logging/bin/fluent-bit-values-0.21.0-agora1.yaml"
diff -du ../fluent-bit-0.20.9-agora3/clusterrole-fluent-bit.yaml ../fluent-bit-0.21.0-agora1/clusterrole-fluent-bit.yaml
--- ../fluent-bit-0.20.9-agora3/clusterrole-fluent-bit.yaml	2022-11-16 16:41:22.000000000 +0900
+++ ../fluent-bit-0.21.0-agora1/clusterrole-fluent-bit.yaml	2022-12-20 08:26:43.000000000 +0900
@@ -5,10 +5,10 @@
 metadata:
   name: fluent-bit
   labels:
-    helm.sh/chart: fluent-bit-0.20.9
+    helm.sh/chart: fluent-bit-0.21.0
     app.kubernetes.io/name: fluent-bit
     app.kubernetes.io/instance: fluent-bit
-    app.kubernetes.io/version: "1.9.9"
+    app.kubernetes.io/version: "2.0.4"
     app.kubernetes.io/managed-by: Helm
 rules:
   - apiGroups:
diff -du ../fluent-bit-0.20.9-agora3/clusterrolebinding-fluent-bit.yaml ../fluent-bit-0.21.0-agora1/clusterrolebinding-fluent-bit.yaml
--- ../fluent-bit-0.20.9-agora3/clusterrolebinding-fluent-bit.yaml	2022-11-16 16:41:22.000000000 +0900
+++ ../fluent-bit-0.21.0-agora1/clusterrolebinding-fluent-bit.yaml	2022-12-20 08:26:43.000000000 +0900
@@ -5,10 +5,10 @@
 metadata:
   name: fluent-bit
   labels:
-    helm.sh/chart: fluent-bit-0.20.9
+    helm.sh/chart: fluent-bit-0.21.0
     app.kubernetes.io/name: fluent-bit
     app.kubernetes.io/instance: fluent-bit
-    app.kubernetes.io/version: "1.9.9"
+    app.kubernetes.io/version: "2.0.4"
     app.kubernetes.io/managed-by: Helm
 roleRef:
   apiGroup: rbac.authorization.k8s.io
diff -du ../fluent-bit-0.20.9-agora3/configmap-fluent-bit.yaml ../fluent-bit-0.21.0-agora1/configmap-fluent-bit.yaml
--- ../fluent-bit-0.20.9-agora3/configmap-fluent-bit.yaml	2022-11-16 16:41:22.000000000 +0900
+++ ../fluent-bit-0.21.0-agora1/configmap-fluent-bit.yaml	2022-12-20 08:26:44.000000000 +0900
@@ -5,10 +5,10 @@
 metadata:
   name: fluent-bit
   labels:
-    helm.sh/chart: fluent-bit-0.20.9
+    helm.sh/chart: fluent-bit-0.21.0
     app.kubernetes.io/name: fluent-bit
     app.kubernetes.io/instance: fluent-bit
-    app.kubernetes.io/version: "1.9.9"
+    app.kubernetes.io/version: "2.0.4"
     app.kubernetes.io/managed-by: Helm
 data:
   custom_parsers.conf: |
@@ -22,7 +22,7 @@
     [SERVICE]
         Daemon Off
         Flush 1
-        Log_Level debug
+        Log_Level info
         Parsers_File parsers.conf
         Parsers_File custom_parsers.conf
         HTTP_Server On
@@ -62,3 +62,5 @@
         Labels job=fluentbit, cloud=${agora_environment_cloud}, environment=${agora_environment}, region=${agora_environment_region}, cluster=${agora_environment_cluster}, mystream=$stream, container=$kubernetes['container_name'], pod=$kubernetes['pod_name'], host=$kubernetes['host'], namespace=$kubernetes['namespace_name']
         Auto_Kubernetes_Labels Off
         Tls Off
+        Net.connect_timeout 60
+        Net.keepalive_max_recycle 4000
diff -du ../fluent-bit-0.20.9-agora3/daemonset-fluent-bit.yaml ../fluent-bit-0.21.0-agora1/daemonset-fluent-bit.yaml
--- ../fluent-bit-0.20.9-agora3/daemonset-fluent-bit.yaml	2022-11-16 16:41:22.000000000 +0900
+++ ../fluent-bit-0.21.0-agora1/daemonset-fluent-bit.yaml	2022-12-20 08:26:44.000000000 +0900
@@ -5,10 +5,10 @@
 metadata:
   name: fluent-bit
   labels:
-    helm.sh/chart: fluent-bit-0.20.9
+    helm.sh/chart: fluent-bit-0.21.0
     app.kubernetes.io/name: fluent-bit
     app.kubernetes.io/instance: fluent-bit
-    app.kubernetes.io/version: "1.9.9"
+    app.kubernetes.io/version: "2.0.4"
     app.kubernetes.io/managed-by: Helm
 spec:
   selector:
@@ -18,7 +18,7 @@
   template:
     metadata:
       annotations:
-        checksum/config: ad82f1db9125be1da34bf826ce1116f419a04ae8f0442f66a292e5077d6e51d4
+        checksum/config: 6bd856033329e15a9a382245e235d16529f97c738f219bd60a03e5f149314a65
         checksum/luascripts: e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855
         prometheus.io/path: /api/v1/metrics/prometheus
         prometheus.io/port: "2020"
@@ -41,7 +41,7 @@
             readOnlyRootFilesystem: true
             runAsNonRoot: true
             runAsUser: 1000
-          image: "docker.artifactory-ha.tri-ad.tech/fluent/fluent-bit:1.9.9"
+          image: "docker.artifactory-ha.tri-ad.tech/fluent/fluent-bit:2.0.4"
           imagePullPolicy: Always
           ports:
             - name: http
@@ -100,6 +100,3 @@
           operator: Exists
         - effect: NoSchedule
           key: cityos.nodepool
-        - effect: NoSchedule
-          key: eks.amazonaws.com/compute-type
-          value: fargate
diff -du ../fluent-bit-0.20.9-agora3/pod-fluent-bit-test-connection.yaml ../fluent-bit-0.21.0-agora1/pod-fluent-bit-test-connection.yaml
--- ../fluent-bit-0.20.9-agora3/pod-fluent-bit-test-connection.yaml	2022-11-16 16:41:22.000000000 +0900
+++ ../fluent-bit-0.21.0-agora1/pod-fluent-bit-test-connection.yaml	2022-12-20 08:26:43.000000000 +0900
@@ -5,10 +5,10 @@
 metadata:
   name: "fluent-bit-test-connection"
   labels:
-    helm.sh/chart: fluent-bit-0.20.9
+    helm.sh/chart: fluent-bit-0.21.0
     app.kubernetes.io/name: fluent-bit
     app.kubernetes.io/instance: fluent-bit
-    app.kubernetes.io/version: "1.9.9"
+    app.kubernetes.io/version: "2.0.4"
     app.kubernetes.io/managed-by: Helm
   annotations:
     "helm.sh/hook": test-success
diff -du ../fluent-bit-0.20.9-agora3/prometheusrule-fluent-bit.yaml ../fluent-bit-0.21.0-agora1/prometheusrule-fluent-bit.yaml
--- ../fluent-bit-0.20.9-agora3/prometheusrule-fluent-bit.yaml	2022-11-16 16:41:22.000000000 +0900
+++ ../fluent-bit-0.21.0-agora1/prometheusrule-fluent-bit.yaml	2022-12-20 08:26:43.000000000 +0900
@@ -5,10 +5,10 @@
 metadata:
   name: fluent-bit
   labels:
-    helm.sh/chart: fluent-bit-0.20.9
+    helm.sh/chart: fluent-bit-0.21.0
     app.kubernetes.io/name: fluent-bit
     app.kubernetes.io/instance: fluent-bit
-    app.kubernetes.io/version: "1.9.9"
+    app.kubernetes.io/version: "2.0.4"
     app.kubernetes.io/managed-by: Helm
     agora-prom-injected: "true"
 spec:
diff -du ../fluent-bit-0.20.9-agora3/service-fluent-bit.yaml ../fluent-bit-0.21.0-agora1/service-fluent-bit.yaml
--- ../fluent-bit-0.20.9-agora3/service-fluent-bit.yaml	2022-11-16 16:41:22.000000000 +0900
+++ ../fluent-bit-0.21.0-agora1/service-fluent-bit.yaml	2022-12-20 08:26:43.000000000 +0900
@@ -5,10 +5,10 @@
 metadata:
   name: fluent-bit
   labels:
-    helm.sh/chart: fluent-bit-0.20.9
+    helm.sh/chart: fluent-bit-0.21.0
     app.kubernetes.io/name: fluent-bit
     app.kubernetes.io/instance: fluent-bit
-    app.kubernetes.io/version: "1.9.9"
+    app.kubernetes.io/version: "2.0.4"
     app.kubernetes.io/managed-by: Helm
 spec:
   type: ClusterIP
diff -du ../fluent-bit-0.20.9-agora3/serviceaccount-fluent-bit.yaml ../fluent-bit-0.21.0-agora1/serviceaccount-fluent-bit.yaml
--- ../fluent-bit-0.20.9-agora3/serviceaccount-fluent-bit.yaml	2022-11-16 16:41:22.000000000 +0900
+++ ../fluent-bit-0.21.0-agora1/serviceaccount-fluent-bit.yaml	2022-12-20 08:26:43.000000000 +0900
@@ -5,8 +5,8 @@
 metadata:
   name: fluent-bit
   labels:
-    helm.sh/chart: fluent-bit-0.20.9
+    helm.sh/chart: fluent-bit-0.21.0
     app.kubernetes.io/name: fluent-bit
     app.kubernetes.io/instance: fluent-bit
-    app.kubernetes.io/version: "1.9.9"
+    app.kubernetes.io/version: "2.0.4"
     app.kubernetes.io/managed-by: Helm
diff -du ../fluent-bit-0.20.9-agora3/servicemonitor-fluent-bit.yaml ../fluent-bit-0.21.0-agora1/servicemonitor-fluent-bit.yaml
--- ../fluent-bit-0.20.9-agora3/servicemonitor-fluent-bit.yaml	2022-11-16 16:41:22.000000000 +0900
+++ ../fluent-bit-0.21.0-agora1/servicemonitor-fluent-bit.yaml	2022-12-20 08:26:43.000000000 +0900
@@ -6,10 +6,10 @@
   name: fluent-bit
   namespace: logging
   labels:
-    helm.sh/chart: fluent-bit-0.20.9
+    helm.sh/chart: fluent-bit-0.21.0
     app.kubernetes.io/name: fluent-bit
     app.kubernetes.io/instance: fluent-bit
-    app.kubernetes.io/version: "1.9.9"
+    app.kubernetes.io/version: "2.0.4"
     app.kubernetes.io/managed-by: Helm
     agora-prom-injected: "true"
 spec:
@@ -19,6 +19,12 @@
       path: /api/v1/metrics/prometheus
       interval: 5s
       scrapeTimeout: 5s
+      scheme: https
+      tlsConfig:
+        caFile: /etc/prom-certs/root-cert.pem
+        certFile: /etc/prom-certs/cert-chain.pem
+        insecureSkipVerify: true
+        keyFile: /etc/prom-certs/key.pem
   namespaceSelector:
     matchNames:
       - logging
``` 
