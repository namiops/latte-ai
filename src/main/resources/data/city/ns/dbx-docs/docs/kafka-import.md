# Import Data from Kafka into Databricks

For sources that stream data, the easiest channel to move data from a variety of sources into Databricks is via Kafka. This guide won’t cover Kafka concepts or getting data into Kafka, and will assume that you already have a topic with data being written to it.

## Delta Live Tables

Delta Live Tables (DLT) (https://docs.databricks.com/en/delta-live-tables/index.html) is a service within Databricks that provides a convenient UI and several data quality features for managing data pipelines. It handles pipeline dependencies, and automatically manages the compute and tables. The definition of tasks is done in a notebook, in either SQL or Python.

## Kafka authentication

Due to Kafka’s usage of AWS IAM for authentication and current Databricks limitations, standard compute is required. Authentication is handled via an instance profile, and Databricks cannot currently assign an instance profile to serverless compute. The instance profile for your workspace has been created for you, 

### 1. **Create your notebook**
Notebooks in Databricks are how code is stored, then executed on one of several types of compute, depending on your application. Notebooks are interactive by default, but also power automated jobs. 

The simplest way to develop is to create an All Purpose Compute cluster, then interactively debug and develop your notebook. When you are satisfied that it works correctly, you can then apply the same notebook to your Delta Live Tables pipelines. 

Navigate to the Workspace tab and select the location where your notebook is to be stored, then use the Create button to create a new notebook.

```
# All DLT-related functions are in the DLT library
import dlt
from pyspark.sql.functions import *
from pyspark.sql.types import *

# Required to use AWS IAM for Kafka
kafka_options = {
    "kafka.sasl.mechanism": "AWS_MSK_IAM",
    "kafka.sasl.jaas.config": "shadedmskiam.software.amazon.msk.auth.iam.IAMLoginModule required;",
    "kafka.security.protocol": "SASL_SSL",
    "kafka.sasl.client.callback.handler.class": "shadedmskiam.software.amazon.msk.auth.iam.IAMClientCallbackHandler"
}

# Use Spark Streaming to read directly from Kafka
raw_kafka_events = spark \
    .readStream \
    .format("kafka") \
    .option("kafka.bootstrap.servers", "my-kafka-broker:9098") \
    .option("subscribe", "my-topic") \
    .option("startingOffsets", "earliest") \
    .option("failOnDataLoss", False) \
    .options(**kafka_options) \
    .load()

# Specify the schema for the table
event_schema = StructType([
    StructField("id", IntegerType(), True),
    StructField("name", StringType(), True),
])

# This decorator instructs DLT to create a new table with the return value of the decorated function. Return should be a DataFrame.
@dlt.table(
    table_properties={"pipelines.reset.allowed":"false"}
)
def agora_dlt_test_topic2():
  return raw_kafka_events \
        .select(from_json(col("value").cast("string"), event_schema).alias("event")) \
        .select("event.*")
```

### 2: Defining Your Delta Live Tables Pipeline

1. **Create a DLT Pipeline**:
   - Click on "Delta Live Tables" and then "Create Pipeline".
   - Provide a name for your pipeline.
   - As mentioned above, Serverless cannot be used in this case. If the data source is something other than Kafka, Serverless is a possibility.
   - Select Triggered or Continuous for your pipeline. If your data is constantly streaming and needs close-to-real-time updates, select Continuous, for one-time or scheduled batch jobs, select Triggered. **Note that Continuous pipelines do not terminate and will be charged the compute fees until the pipeline is deleted.**

2. **Specify the Notebook Path**:
   - In the "Source Code" section, specify the path to the notebook you created earlier.

3. **Destination**:
   - Select Unity Catalog to have your data written to managed storage. Select the target catalog and schema.

4. **Compute**:
   - Select the minimum compute needed depending on your application. Note that compute can become very expensive very quickly, where possible we suggest fixed-size clusters with small numbers of workers. 
   - Select your workspace's instance profile to allow authentication to Kafka. You will be able to access any topics prepended by your workspace name.

5. **Create**:
   - You're ready to go! Click 'Create' to launch your pipeline.

## Step 7: Monitoring and Managing Your Pipeline

1. **Pipeline Dashboard**: Use the Delta Live Tables dashboard to monitor the progress and health of your pipeline. The dashboard provides insights into the processing metrics and any potential issues.

2. **Logs and Metrics**: Access detailed logs and metrics to troubleshoot any issues. You can view the logs in the "Event Log" section of the pipeline dashboard.

3. **Scaling and Optimization**: Based on the performance metrics, you might need to adjust the cluster size or optimize your transformations for better performance.

