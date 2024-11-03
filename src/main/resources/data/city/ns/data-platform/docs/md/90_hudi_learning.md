# Hudi learning materials

Hudi provides the official [FAQs | Apache Hudi](https://hudi.apache.org/docs/faq/), but it may not address all of your questions. 
Here are some additional documents:

### Partitioning

#### Which schema key is suitable for [the partitionKey](https://github.com/wp-wcm/city/blob/552558e65b20004903be5f3c8f1a316e7b0c11d8/ns/hoodiestreamer_job/hoodiestreamer_job_template_schema.yaml#L25)?

A column frequently used in query filters would be a good candidate for partitioning.

!!!Note
    Do not use a field containing PII as the partition key, as it will write the data as the folder names, and you may unintentionally expose sensitive data.

According to [Build your Apache Hudi data lake on AWS using Amazon EMR – Part 1 | AWS Big Data Blog](https://aws.amazon.com/jp/blogs/big-data/part-1-build-your-apache-hudi-data-lake-on-aws-using-amazon-emr/), for large-scale use cases with evolving query patterns, it is recommended to use coarse-grained partitioning (such as date), while using fine-grained data layout optimization techniques (clustering) within each partition. This opens the possibility of data layout evolution. 

If you're wondering about the appropriate partition size for large-scale use cases, it generally depends on the trade-off between faster writes and faster reads for `COPY_ON_WRITE` tables. For faster writes, you may reduce the partition size, while for faster reads, you may increase it. The default targeted file size for Parquet-based files is 120MB, configurable with `hoodie.parquet.max.file.size` as described in [File Sizing - Auto-sizing during writes | Apache Hudi](https://hudi.apache.org/docs/file_sizing/#auto-sizing-during-writes) documentation.

[Another blog](https://www.onehouse.ai/blog/top-3-things-you-can-do-to-get-fast-upsert-performance-in-apache-hudi) says:
> A common pitfall is setting partitions too granularly, such as dividing partitions by `<city>/<day>/<hour>`. Depending on your workload, there might not be enough data at the hourly granularity, resulting in many small files of only a few kilobytes. If you’re familiar with the small file problem, more small files cost more disks seeks and degrades query performance. Secondly, on the ingestion side, small files also impact the index lookup because it will take longer to prune irrelevant files. Depending on what index strategy you’re implementing, this may negatively affect write performance. I recommend users always start with a coarser partitioning scheme like `<city>/<day>` to avoid the pitfalls of small files. If you still feel the need to have granular partitions, I recommend re-evaluating your partitioning scheme based on query patterns and/or you can potentially take advantage of the clustering service to balance ingestion and query performance.

#### How to configure the time granularity for my time-based partition key?

Suppose your typical queries contain the time range (e.g. the number of users registered during Dec2023), provided your records have some field indicating _time_. In this case, the _time_ field is a good candidate for the partition key.

But let's say your time values are too granular (e.g. milliseconds). As written in [another section](#which-schema-key-is-suitable-for-the-partitionkey), partitioning the data as-is may result in too many small files and does NOT perform well.

In such scenario, you can control the granularity of the time-based partition key (e.g. per date, per hour, etc) through the optional [`timebased_keygen` field in the Hudi Streamer Zebra target](https://github.com/wp-wcm/city/blob/d35333a663ad8572509e0ff9eb3ff7a7eb58b022/ns/hoodiestreamer_job/hoodiestreamer_job_template_schema.yaml#L27).

Let's take a look at the example configuration.

```yaml
partition_key: datetime # e.g. "2023-12-12T10:10:23.456Z"
timebased_keygen:
  type: "DATE_STRING" # one of ["UNIX_TIMESTAMP", "DATE_STRING", "EPOCHMILLISECONDS"]
  input_dateformat: "yyyy-MM-dd'T'HH:mm:ss.SSSZ" # mandatory if type="DATE_STRING"
  output_dateformat: "yyyy-MM-dd" # partition by date
  timezone: "JST" # optional
```

With the above example setting:
1. The Hudi Streamer job will look at `datetime` field in your record.
2. The field value is parsed with the format `yyyy-MM-dd'T'HH:mm:ss.SSSZ`
3. The parsed datetime is formatted to `yyyy-MM-dd` and used as the partition key.

`output_dateformat` is the key element when you control the time granularity (per day in this case). If you think the date is too coarse, you can choose `yyyy-MM-dd--HH-mm` or even more granular.

Here are the different types of key generation. Choose the right one based on your record value.

* *EPOCHMILLISECONDS*: Treat the value as Unix timestamp in milliseconds (e.g. 1702352471000). The field is expected to be typed as `long` in its schema.
* *UNIX_TIMESTAMP*: Treat the value as Unix timestamp in seconds (e.g. 1702352471). The field is expected to be typed as `long` in its schema.
* *DATE_STRING*: Treat the value as a String formatted as `input_dateformat` specifies.

References:
* https://hudi.apache.org/docs/key_generation
