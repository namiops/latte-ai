## [DEPRECATED] Use Case: Batch processing

!!!Warning 
    Scheduler is currently on a sunset path and will be removed soon!

Although scheduler was designed to be a perfect fit for scheduled notifications, following the standard RFC definitions for calendars, it works fairly well with use cases involving batch processing and recurring tasks (think of linux `at` and `cron` commands) thanks to the ability to publish payloads in dediated Kafka topics.

![alt_text](diagrams/batch_processing.png "Connection Manager")

As an example, in the image above, we can see how a developer can post via REST API an event containing a payload to the scheduler. The scheduler then will publish it at the right time in kafka on the `energy.simulations` topic from which a dedicated simulation engine owned by the Energy team will be receiving the scheduled task payload and, in an hypotetical scenario, process data and publish results real time to time series or a bucket.

!!! Note

    the payload needs to be base64 encoded, but it can be a custom structure such as
    ```JSON
    {
        "algoId":"a123",
        "dataSourceBucketUrl":"s3://presignedUrl123",
        "destinationBucketUrl":"s3://destination.presignedURL444"
    }
    ```

Scheduler allows also for recurring events, therefore such batch processing can be scheduled on recurring times useing RRULE definition.
