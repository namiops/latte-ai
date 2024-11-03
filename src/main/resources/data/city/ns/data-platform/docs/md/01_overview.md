# Overview

## What is the Agora data platform?

As the city platform evolves, common patterns are emerging for data-related operations from service teams and tenants. In an effort to standardize and provide scaffolding for service teams to build on top of, the data platform contains a collection of tools to accomplish common tasks.

## Components of the data platform

* *Kafka*: Kafka is an extremely efficient, write-optimized, distributed data streaming system. Throughput is very fast, with the ability to process thousands of messages per second. Kafka is the industry-standard tool, with wide adoption and a large selection of client libraries.

* *Spark*: Apache Spark is a distributed computing framework designed specifically to process large-scale data efficiently. Because tasks are distributed across a cluster of workers, it enables parallel processing and in-memory computation for complex operations like data transformations, machine learning, and graph processing.

* *Hudi*: Apache Hudi is specifically designed to handle changing and evolving datasets in object storage, like S3. Standard object storage provides a way to store and retrieve files, but Hudi offers built-in mechanisms for managing updates, deletes, and incremental changes to data, ensuring data consistency and accuracy over time. Hudi also integrates with data processing frameworks like Spark, making it easier to perform advanced data transformations and analytics directly on the evolving datasets, without the need for complex data migration processes.

* *Datahub*: Datahub is a centralized data catalog. It enables efficient discovery and management of diverse data assets, their schemas, and lineage, as well as governance and metadata management. 

## Phases of the data platform

* *Import*: Data is brought in from external or within-Agora sources: services, devices, and applications, generally over a Kafka topic. This data is written, with its schema, into object storage.

* *Manage*: Schemas are synchronized with the data catalog, and the appropriate tags and annotations are applied, enabling governance and discovery by other service teams

* *Transform*: Incremental processing of data is possible, as well as merging with other data sets and further transformation.

* *Analyze*: Data can be queried as-is, or sent to other analytics tools for further insights.

