# Unity Catalog Overview

**Unity Catalog** is the a tool to manage metadata, enforce data access policies, and track data lineage within  Databricks. 

#### Why do we need it? What benefits does it provide?

1. **Centralized Metadata Management**:
   Unity Catalog provides a central repository for managing metadata across different data assets and cloud environments. This repository ensures consistency and data integrity, as well as facilitating data discovery by teams in other data domains.

2. **Fine-Grained Access Control**:
   Access policies may be written at various levels, including table-level, row-level, and column-level permissions. Role-based access controls (RBAC) are also possible, allowing standardized permissions across groups of users. 

3. **Data Lineage and Auditability**:
   Data stored in Unity Catalog has full lineage information, allowing engineers to trace data from its origin through its transformation processes. All access and modification of data is logged automatically, including the timestamp and principal.

4. **Managed Storage**:
   By default, data is kept in managed storage - under the hood, everything is stored as Parquet files in S3 buckets specific to your workspace, but Databricks manages all storage and implementation details. Access to your data from within your workspace, as well as from other workspaces, is mediated through Unity Catalog, giving control over your team's data both internally and externally. Data may also be stored elsewhere, called an External Location, and only the metadata stored and managed in Unity Catalog.

5. **Data Sharing and Collaboration**:
   Unity Catalog supports secure data sharing across teams and projects. Both read-only and collaborative access patterns are supported.

### Main components

#### Catalogs
A **catalog** in Unity Catalog is the highest-level container for organizing data assets. It serves as a namespace for multiple schemas, allowing for logical separation of data.

#### Schemas
A **schema** is a logical grouping of tables, views, and other data objects within a catalog. Schemas organize data assets within a catalog.

#### Tables
A **table** is a structured data asset organized into rows and columns, similar to a table in a relational database. Tables store the actual data and can be managed or external.

- **Managed Tables**: Data is stored within the Databricks environment, and Databricks manages the lifecycle of the data.
- **External Tables**: Data is stored outside of Databricks (e.g., in an external data lake), but metadata is managed within Unity Catalog.

#### Views
A **view** is a saved query that provides a virtual table. Views do not store data themselves but display data stored in tables based on specific query logic. **Materialized views** are registered in the catalog and are first-class Unity Catalog attributes, and can be queried by anyone (provided the appropriate permissions are in place) and shared and managed in the same manner as regular tables. **Temporary views** are scoped to a notebook or script and are not stored in the catalog. 
