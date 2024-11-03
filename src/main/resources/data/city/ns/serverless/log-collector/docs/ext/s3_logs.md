# Saving Logs to S3

While Log Collector already handles text-based logs in various formats, you might want to have an interface to upload entire files of logs.
One of the most common storage solutions for those would be AWS S3, which is what Log Collector now supports.

!!! Note
    If you would like to use this feature with IoT devices, please refer to the complete solution in [IoTA docs page](https://developer.woven-city.toyota/docs/default/Component/iota-service/Device%20Logging/media_logs/)

## Prerequisites

Log Collector provides an interface that operates on the principle Bring Your Own Bucket(BYOB), so the user will need to prepare their own S3 bucket.

### Bucket Name

The name of the pre-registered bucket should correspond to the `<subject>-agora-log-collector-dev` format, where `subject` is extracted from XFCC header and can be one of the following:

* For external traffic, it will be the Common Name(CN) in the header's subject.
* For Agora internal traffic, it will be the namespace as user identifier from URI like `spiffe://cluster.local/ns/<namespace>/sa/test-service“
* And for specific internal services, like IoTA, if there is an override header `X-Agora-Log-Source`, it will be that value.

### Bucket Policy

In order to delegate the access to your S3 Bucket for Log Collector, you need to define a bucket policy, for example in AWS S3 Console in Permissions tab.

The content of the policy should be as follows:
```json
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Principal": {
                "AWS": "arn:aws:iam::835215587209:role/agora-dev-irsa-log-collector-s3"
            },
            "Action": [
                "s3:GetObject",
                "s3:PutObject",
                "s3:ListBucket"
            ],
            "Resource": [
                "arn:aws:s3:::<your-bucket-name-in-the-correct-format>/*",
                "arn:aws:s3:::<your-bucket-name-in-the-correct-format>"
            ]
        }
    ]
}
```

This policy will allow the Log Collector, assuming the IAM role `agora-dev-irsa-log-collector-s3`, to access your bucket an execute the listed actions.

## Calling the Endpoints

Currently, Log Collector provides only one endpoint to generate a presigned PUT url at `/s3/put-presigned-url`.

Example curl:
```bash
curl --location "<log-collector-url>/s3/put-presigned-url" \
--header 'Content-Type: application/json' \
-d "[
   {
     "object_key": "<group>/<device>/<timestamp>-video-1.mp4",
     "checksum": "<sha-256-checksum>"
   },
   {
     "object_key": "<group>/<device>/<timestamp>-video-2.mp4",
     "checksum": "<sha-256-checksum>"
   },
]"
```

Note, that the `checksum` field is optional and may be omitted.
