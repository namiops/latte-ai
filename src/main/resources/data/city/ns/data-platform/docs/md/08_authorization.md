# Authorization

## Overview

As shown in the diagrams, regardless of data source, all queries are routed through Trino, including ones initiated via Superset. This means that you, as a service team, control who can access your data. 

## Getting Started

To get started with file-based authorization in Trino, you must first set up a JSON file containing the authorization rules. The hierarchy in Trino is catalog -> schema -> table.

Example:

```permissions.json
{
  "catalogs": [
    {
      "user": "user1",
      "catalog": "catalog1",
      "allow": "ALL"
    },
    {
      "user": "user2",
      "catalog": "catalog2",
      "allow": "read-only"
    }
  ],
  "schemas": [
    {
      "user": "user3",
      "catalog": "catalog3",
      "schema": "schema1",
      "owner": true
    }
  ],
  "tables": [
    {
      "user": "user4",
      "catalog": "catalog4",
      "schema": "schema2",
      "table": "table1",
      "allow": "SELECT"
    }
  ]
}```

In this example, `user1` has all privileges for `catalog1`, `user2` has only read privileges for `catalog2`, `user3` has ownership privileges (including alteration) for `schema1` in `catalog3`, and `user4` has only SELECT privileges for `table1` in `schema2` in `catalog4`.

Add this filename to your values config (see Query) and it will be automatically added to your Trino deployment. 

## Masking

Masking is a method used to protect sensitive data by replacing it with random characters or completely hiding it. In Trino, you can use view-based column masking. To implement this, you can add a masking function to your permissions file. 

Example:

```permissions.json
{
  "tables": [
    {
      "user": "user1",
      "catalog": "catalog1",
      "schema": "public",
      "table": "sample",
      "privileges": [
        "SELECT"
      ],
      "columns": [
        {
          "name": "name",
          "mask": "concat('***', substring(name,4))"
        }
      ]
    }
  ]
}
```

This will mask the data for user1 in the `name` column of `catalog1.public.sample` by replacing three characters with `***`, resulting in:

```
trino> select * from "trino-demo-postgres"."public"."sample";
             ts             |   name    
----------------------------+-----------
 2023-11-03 12:00:00.000000 | ***n      
 2023-11-03 12:15:00.000000 | ***ce     
 2023-11-03 12:30:00.000000 | ***       
 2023-11-03 12:45:00.000000 | ***       
 2023-11-03 13:00:00.000000 | ***rlie   
 2023-11-03 13:15:00.000000 | ***ce     
 2023-11-03 13:30:00.000000 | ***id     
 2023-11-03 13:45:00.000000 | ***via    
 2023-11-03 14:00:00.000000 | ***hia    
```

The value may be completely replaced, or other functions may be used.

## Superset

These controls can also be added to dashboards and queries from Superset. The username (usually the email address) of the logged-in user will be applied to all operations in Superset conducted through your Trino datasource, and permissions will be applied accordingly. This needs to be enabled on your datasource in Superset, please contact the Orchestration team.
