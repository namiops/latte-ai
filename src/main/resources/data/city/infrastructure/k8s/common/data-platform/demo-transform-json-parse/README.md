# demo-transform-json-parse

This set of jobs demonstrates several things that aren't covered by other sample jobs so far.

1. Ingestion of CSV data with the escape character option. Have a look at `hoodie.streamer.csv.escape` in hudi-json-parse-ingest-values.yaml.
2. JSON parse inside the transformation job. Have a look at `sql_transform` in hudi-json-parse-transform-values.yaml.
    - Refer to [Spark SQL reference](https://spark.apache.org/docs/latest/sql-ref-functions-builtin.html#:~:text=JSON%20Functions) for the list of JSON functions available.

## Parsing JSON in Superset

This example demonstrates an ingestion job that writes a column with JSON string and a transformation job that parses the JSON string column (and writes to another table).

Another way to parse JSON string column is to do it in Superset (more precisely, Trino) directly.

Here's one example SQL query upon the table written by the ingestion job in Superset.

```sql
select event_id, 
    event_data_id, 
    json_extract_scalar(json, '$.location.lat') lat,
    json_extract_scalar(json, '$.location.lng') lng,
    json_extract_scalar(json, '$.tracking_id') tracking_id,
    json_extract_scalar(json, '$.detection_id') detection_id,
    event_data
from "data-platform-demo"."hudi-json-parse-ingest"
cross join UNNEST(cast(json_extract(event_data, '$.people') AS array(json)) ) as t(json)
limit 10
```

Note the functions available in Trino is different from those of the Hudi (Spark) jobs.
Refer to [Trino docs](https://trino.io/docs/current/functions/json.html).
