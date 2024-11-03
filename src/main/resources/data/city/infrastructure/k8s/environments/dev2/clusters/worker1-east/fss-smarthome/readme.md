## SmartHome Data Pipeline

### Overview
This directory holds the deployment manifests for SmartHome's data pipeline. The data from SmartHome's AWS Timestream database is periodically unloaded into Smart Home's S3 bucket. We then use data platform tools to ingest the data from S3 into the Hudi table, transform, and query. 

The Hudi table is queriable through Grafana and Superset.

The dataset from the Hudi is read by a spark job (Hudi Metrics Transformer) that extracts and transforms into time series metrics, and stores into the Mimir Time series database. The Mimir data can be queried using the Mimir data source in the Grafana.

The dataset from the Hudi is also queryable through the Superset dashboard for further data exploration and visualization.

Here's a brief architecture of the sequence.
![plot](./docs/Smart-Home-Flow.png)

### Sample Dashboards
TODO (after deployment)

### References
[Data Platform Import and dashboard creation](https://developer.woven-city.toyota/docs/default/Component/data-platform/05_import_and_db/)

[IoTA Data pipeline experiments: TN-0337](https://docs.google.com/document/d/1-YSg8QOsP5pkDsG17Xg7NAJtdxjxcRlvDKVN9l3Hfzc/edit#heading=h.5qm13wuvtiz9)

[SmartHome Proposal](https://docs.google.com/document/d/1q1PEthsAZFsvQQImr73po3noYcYT0R1-mnx0vNHaHzM/edit#heading=h.ijhpemmrl606)
