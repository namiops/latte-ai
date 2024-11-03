# PostgreSQL Observability Overview

## Setup
You need to enable monitoring for your PostgreSQL cluster to use the dashboards and alerts. Please check here(TODO) to see how to do that.

## Links
Garfana [https://observability.cityos-dev.woven-planet.tech/grafana/dashboards/f/fPEHsCJ4k/postgresql](https://observability.cityos-dev.woven-planet.tech/grafana/dashboards/f/fPEHsCJ4k/postgresql).

## Grafana
Grafana allows us to create interactive dashboards monitoring and analyzing data about PostgreSQL cluster status. In this section, we will go over each dashboard for PostgreSQL cluster.

### Variables
You will need to use variables to control what the dashboard shows you. The variables change from dashboard to dashboard, but most of them have a few in common:

* namespace: the namespace where your PostgreSQL cluster exists.
* cluster: the name of your cluster. Usually, we encourage one cluster per namespace, so this variable will be automatically selected when you select a namespace.

We will cover the specific variables later when we come across them.

### Time Range
The dashboard also heavily depends on the time range you can select in the top right corner. You can adjust the range to see how the graphs change over time.

One important note is that the variables are also affected by the range. For example, if you want to view an old pod that might have crashed, it will only show up in the variable list if the range covers the time when the pod existed.

### Hyperlinks
On the top right are helpful links for navigating between the dashboards. If you go through these links, Grafana will try to pre-fill the variables it can (usually namespace and cluster), which can be helpful when you quickly want to check multiple things.

### PostgreSQL Details Dasboard
This dashboard provides insight into the PostgreSQL cluster and its current status. You can use it to see the possible bottlenecks of your PostgreSQL cluster.

* namespace: Your namespace.
* cluster: Your cluster name.
* pod: Multiselect. By default, this selects all the pods.
* Database: Multiselect. By default selects all databases.

Here is a quick overview of the panels:

* Backup Status: The last time a backup was taken of the cluster. Green is good. Orange means that a backup has not been taken in more than a day and may warrant investigation.
* Active Connections: How many clients are connected to the database. Too many clients connected could impact performance and, for values approaching 100%, can lead to clients being unable to connect.
* Idle in Transaction: How many clients have a connection state of “idle in transaction”. Too many clients in this state can cause performance issues and, in certain cases, maintenance issues.
* Idle: How many clients are connected but are in an “idle” state.
* TPS: The number of “transactions per second” that are occurring. Usually needs to be combined with another metric to help with analysis. “Higher is better” when performing benchmarking.
* Connections: An aggregated view of active, idle, and idle in transaction connections.
* Database Size: How large databases are within a PostgreSQL cluster. Typically combined with another metric for analysis. Helps keep track of overall disk usage and if any triage steps need to occur around PVC size.
* WAL Size: How much space write-ahead logs (WAL) are taking up on disk. This can contribute to extra space being used on your data disk, or can give you an indication of how much space is being utilized on a separate WAL PVC. If you are using replication slots, this can help indicate if a slot is not being acknowledged if the numbers are much larger than the max_wal_size setting (the PostgreSQL Operator does not use slots by default).
* Row Activity: The number of rows that are selected, inserted, updated, and deleted. This can help you determine what percentage of your workload is read vs. write, and help make database tuning decisions based on that, in conjunction with other metrics.
* Replication Status: Provides guidance information on how much replication lag there is between primary and replica PostgreSQL instances, both in bytes and time. This can provide an indication of how much data could be lost in the event of a failover.

![PostgreSQL Details Dashboard](dashboards/postgres-details.png)

### POD Details Dashboard
This dashboard provides useful information about specific pods in your PostgreSQL cluster. 

* namespace: Your namespace.
* cluster: Your cluster name.
* pod: Specific pod you want to inspect.

Here is a quick overview of the panels:

* Disk Usage: How much space is being consumed by a volume.
* Disk Activity: How many reads and writes are occurring on a volume.
* Memory: Various information about memory utilization, including the request and limit as well as actually utilization.
* CPU: The amount of CPU being utilized by a Pod
* Network Traffic: The amount of networking traffic passing through each network device.
* Container Resources: The CPU and memory limits and requests.

![POD Details Dashboard](dashboards/pod-details.png)

### PostgreSQL Overview Dashboard
This small dashboard shows how your cluster is doing at a quick glance. Clicking on the panel will open a dropdown with the hyperlinks for this particular cluster. 

* namespace: Your namespace.
* cluster: Multiselect. Default is set to `All`. Your cluster name.

![PostgreSQL Overview Dashboard](dashboards/postgres-overview.png)

### PostgreSQL Service Health
This dashboard provides information about the traffic for the PostgreSQL cluster.

* namespace: Your namespace.
* cluster: Your cluster name.
* role: Either `Master` or `Replica`.

Quick overview of the panels:

* Saturation: How much of the available network to the Service is being consumed. High saturation may cause degraded performance to clients or create an inability to connect to the PostgreSQL cluster.
* Traffic: Displays the number of transactions per minute that the Service is handling.
* Errors: Displays the total number of errors occurring at a particular Service.
* Latency: What the overall network latency is when interfacing with the Service.

![PostgreSQL Service Health Dashboard](dashboards/postgres-service-health.png)

### Query Statistics Dashboard
You can use this dashboard to determine where and what uses the database the most. It can also help you see if some queries take the longest.

* namespace: your namespace.
* cluster: your cluster name.
* service: either `master` or `replica`.
* dbname: the name of the database.
* dbuser: the name of the user using the database.

Quick overview of the panels:

* Queries Executed: The total number of queries executed on a system during the period.
* Query runtime: The aggregate runtime of all the queries combined across the system that were executed in the period.
* Query mean runtime: The average query time across all queries executed on the system in the given period.
* Rows retrieved or affected: The total number of rows in a database that were either retrieved or had modifications made to them.

![Query Statistics Dashboard](dashboards/query-statistics.png)

### pgBackRest Dashboard
This dashboard shows you the status of the backups for your cluster. It's good to keep an eye out on this dashboard to make sure that your backups are in order.

* namespace: your namespace.
* cluster: your cluster name.

Quick panel overview:

* Recovery Window: This is an indicator of how far back you are able to restore your data from. This represents all of the backups and archives available in your backup repository. Typically, your recovery window should be close to your overall data retention specifications.
* Time Since Last Backup: this indicates how long it has been since your last backup. This is broken down into pgBackRest backup type (full, incremental, differential) as well as time since the last WAL archive was pushed.
Backup Runtimes: How long the last backup of a given type (full, incremental differential) took to execute. If your backups are slow, consider providing more resources to the backup jobs and tweaking pgBackRest’s performance tuning settings.
* Backup Size: How large the backups of a given type (full, incremental, differential).
* WAL Stats: Shows the metrics around WAL archive pushes. If you have failing pushes, you should to see if there is a transient or permanent error that is preventing WAL archives from being pushed. If left untreated, this could end up causing issues for your PostgreSQL cluster.

![pgBackRest Dashboard](dashboards/pgBackRest.png)

### Prometheus Alerts Dashboard
This has not been updated to the Agora standard and is not currently used.

### More Informatiom
We copied these dashboards from the official crunchy data monitoring page. They were updated to a newer version of Grafana and made to work with Agora. Still, if you want to read more about it, you can do so in the [official documentation](https://access.crunchydata.com/documentation/postgres-operator/v5/architecture/monitoring/).

## AlertManager
The agora team monitors all databases set up in the cluster on [#wcm-org-agora-storage-alerts](https://woven-by-toyota.slack.com/archives/C04SNV7MFTM) slack channel. If you are curious, you are welcome to peek at it. Although it's not required, we will ping you if something happens to your database. Unfortunately, we don't have as much information in the alerts as in Grafana because the AlertManager is configured differently. We can mostly see the namespace and the pod name. When something fails, alerts warn about a problem but require more investigation to narrow down the problem.

### Alert List

* `PGIdleTxn`: There are too many connections that are in the “idle in transaction” state.
* `PGQueryTime`: A single PostgreSQL query is taking too long to run. Issues a warning at 12 hours and goes critical after 24.
* `PGConnPerc`: Indicates that there are too many connection slots being used. Issues a warning at 75% and goes critical above 90%.
* `PGDiskSize`: Indicates that a PostgreSQL database is too large and could be in danger of running out of disk space. Issues a warning at 75% and goes critical at 90%.
* `PGReplicationByteLag`: Indicates that a replica is too far behind a primary instance, which could risk data loss in a failover scenario. Issues a warning at 50MB an goes critical at 100MB.
* `PGReplicationSlotsInactive`: Indicates that a replication slot is inactive. Not attending to this can lead to out-of-disk errors.
* `PGXIDWraparound`: Indicates that a PostgreSQL instance is nearing transaction ID wraparound. Issues a warning at 50% and goes critical at 75%. It’s important that you vacuum your database to prevent this.
* `PGEmergencyVacuum`: Indicates that autovacuum is not running or cannot keep up with ongoing changes, i.e. it’s past its “freeze” age. Issues a warning at 110% and goes critical at 125%.
* `PGArchiveCommandStatus`: Indicates that the archive command, which is used to ship WAL archives to pgBackRest, is failing.
* `PGSequenceExhaustion`: Indicates that a sequence is over 75% used.
* `PGSettingsPendingRestart`: Indicates that there are settings changed on a PostgreSQL instance that requires a restart.
* `PGBackRestLastRuntimeDiff_main`: Indicates that the expected runtime of diff backup for stanza has exceeded 1 hour.
