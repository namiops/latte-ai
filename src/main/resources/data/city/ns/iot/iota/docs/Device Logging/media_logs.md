# Media Logs Quickstart


## Overview

This quickstart will show you how to upload log files from your IoT devices to your AWS S3 bucket in 1 click using the `iotactl` CLI tool.


## Why use this feature

While text logs are a crucial part of observability, media logs, such as video footage or audio recordings, are not less important for incidents detection by human operators or AI.

IoTA CLI provides a seamless interface to upload the log files to your own pre-registered bucket.


## What you’ll need


Before starting this tutorial, you should already have the following prepared:

* The latest version of `iotactl`
    * [darwin_amd64](https://artifactory-ha.tri-ad.tech/artifactory/wcm-cityos/ns/iot/iota/cmd/iotactl/iotactl_release_darwin_amd64-latest)
    * [darwin_arm64](https://artifactory-ha.tri-ad.tech/artifactory/wcm-cityos/ns/iot/iota/cmd/iotactl/iotactl_release_darwin_arm64-latest)
    * [linux_amd64](https://artifactory-ha.tri-ad.tech/artifactory/wcm-cityos/ns/iot/iota/cmd/iotactl/iotactl_release_linux_amd64-latest)
    * [linux_arm](https://artifactory-ha.tri-ad.tech/artifactory/wcm-cityos/ns/iot/iota/cmd/iotactl/iotactl_release_linux_arm-latest)
    * [linux_arm64](https://artifactory-ha.tri-ad.tech/artifactory/wcm-cityos/ns/iot/iota/cmd/iotactl/iotactl_release_linux_arm64-latest)
    * [windows_amd64](https://artifactory-ha.tri-ad.tech/artifactory/wcm-cityos/ns/iot/iota/cmd/iotactl/iotactl_release_windows_amd64-latest.exe)
* A registered tenant
* A provisioned device that you want to upload from
* A pre-registered AWS S3 bucket with the name `<tenant>-agora-log-collector-dev`
* Configure a bucket policy to allow access from Log Collector (more details [here](https://developer.woven-city.toyota/docs/default/component/telemetry-collector/s3_logs))


## Steps

### 1. Upload logs to S3

Run the following command in `iotactl`:

```bash
iotactl upload logs <device-name> --group <group-name> -f <path/to/timestamp-file-1.ext> -f <path/to/timestamp-file-2.ext>
```

This will upload your log files to the tenant's S3 bucket under `/<group-name>/<device-name>/timestamp-file-1.ext` and `/<group-name>/<device-name>/timestamp-file-2.ext`.

Optionally, you can provide an override key argument should you want a different s3 object key from the path extracted one.

Then, the command will look as follows:

```bash
iotactl upload logs <device-name> --group <group-name> -f <path/to/timestamp-file-1.ext> -k <override-key-1> -f <path/to/timestamp-file-2.ext> -k <override-key-2>
```

The log files in the S3 bucket will be stored then under `/<group-name>/<device-name>/<override-key-1>` and `/<group-name>/<device-name>/<override-key-2>`.

## Conclusion

Congrats! You should now have uploaded the log files to your S3 bucket from your device.

There is currently no functionality to view the uploaded files but please stay tuned for the updates.

If you run into any issues or would like to suggest an idea, please reach out to our team on the Slack `#wcm-org-agora-services` channel.
