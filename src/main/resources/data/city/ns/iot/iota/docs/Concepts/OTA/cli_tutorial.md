# OTA CLI Quickstart Tutorial


## Overview
<!---
Brief introduction or summary of the document – what are you trying to do and what is it used for? This should be short (no more than 1-2 paragraphs) and give a general overview of the topic. No need to expand on specific concepts – use inline links instead, or redirect readers to more detailed documents in the “More information” section at the end.
--->

This quickstart will show you how to distribute updates to your IoT devices over the air (OTA) using the `iotactl` CLI tool.


## What you'll learn
<!---
List of steps that make up the tutorial. Ideally they should be linkable to the relevant sections if available. Use the imperative voice and try to be a bit descriptive – what is the purpose of performing each step?
--->

From this tutorial, you will learn how to:

1. [Upload a new Release](#1-upload-a-new-release)
2. [Distribute a Release to Device or a Group](#2-distribute-a-release-to-a-device-or-a-group)


## What you’ll need
<!---
List of pre-requisites or implementations you need to have completed before starting this tutorial. Link to previous tutorials/documents if available.
--->

Before starting this tutorial, you should already have the following prepared:

* The latest version of `iotactl` (download it [here](https://artifactory-ha.tri-ad.tech/ui/native/wcm-cityos/ns/iot/iota/cmd/iotactl/))
* A provisioned device that you want to release to
* The release file that you want to distribute to your device

!!! Warning
    The file you are trying to upload must be less than 2GB


## Steps

### 1. Upload a new Release
<!---
Step titles should match the “What You’ll Learn” section above. You can split a step into further sub-steps if necessary.
The contents of the steps should include example inputs and outputs to help users check if they done things correctly and got the expected outcomes. You can also include points to take note of while performing the step.
--->

#### Upload the release file
Before we can distribute your new release to your device, we first need to upload that release to our communal S3 bucket.

!!! Note
    Releases are tied to a group. To send one release to multiple groups, you will need to upload it to each one individually.

Run the following command in `iotactl`:

```bash
iotactl upload release --group <group-name> --release <release-name> --file-upload <path-to-upload-file>
```
This will upload your release file to OTA's repository. The release name is unique. OTA treats release files as immutable data, which mean there should be no file overwrite/update to the same release name. We recommend to name the release with meaningful word for your release process, e.g. autobot-firmware-1.0.0

#### Verify your upload

You can use `iotactl` to list the releases for your group and check if your release has been successfully stored in our S3 bucket:

```bash
iotactl get releases --group <group-name>
```

### 2. Distribute a release to a device or a group

After you have uploaded your release, you need to distribute it to your device(s) or group(s). You will need to prepare the release name and the release-type. The release type should be a keyword that helps the device understand where and how it should install the release file, e.g. firmware, software, app-docker-image. The release-type must match your device's logic as a key to fetch the correct release from the map in the shadow delta topic message. 

#### a. A single device, or a subset of devices in a group

```bash
iotactl set release devices <comma-separated-list-of-devices> --release <release-name> --release-type <release-type> --group <group-name>
```
!!! Warning
    All the devices listed here *MUST* be in the same group!

!!! Note
    if release-type is not specified, it will be set to `default`
#### b. An entire group

```bash
iotactl set release group --release <release-name> --release-type <release-type> --group <group-name>
```

!!! Note
    if release-type is not specified, it will be set to `default`

#### Verify your distribution

You should get outputs detailing which device shadows have been updated correctly and which ones have not:

```bash
Releasing the version <release-name> to devices in <group-name>
================================================

> Failed Devices <

Device Name			Provisioned			Error
===========			===========			===========
<device-name>       <true/false>        <error>
================================================

> Success Devices <

Device Name			Provisioned
===========			===========
<device-name 2>     <true/false>
```

Devices that successfully had their shadow updated will receive the following message on the `shadow/delta` topic:

```json
{
  "x-agora-ota":{
    "releaseId":"<release-id>", // deprecated, please use the `releases` field below
    "releases": {
        "<release-type-1>": {
                "releaseId": "<release-id-1>",
                "releaseType": "<release-type-1>"
                },
        "<release-type-2>": {
                "releaseId": "<release-id-2>",
                "releaseType": "<release-type-2>"
                }
        }
    },
   "x-agora-transient-data": {
        "presignURL": "<generated-url>", // deprecated, please use the `releases` field below
        "releases": {
            "<release-type-1>": {
                "presignedURL": "<generated-url-1>",
                "releaseId": "<release-id-1>",
            },
            "<release-type-2>": {
                "presignedURL": "<generated-url-2>",
                "releaseId": "<release-id-2>",
            }
        }
    }
}
```

!!! Note
    The generated `presignURL` will be encoded for transport, we use [standard Go JSON encoding](https://pkg.go.dev/encoding/json#Marshal:~:text=String%20values%20encode,u2028%22%2C%20and%20%22%5Cu2029%22)
    this means that special characters will be encoded (e.g. `&` --> `\u0026`).
    You will need to instruct your JSON unmarshaller to decode these value, otherwise your URL will not work.

!!! Tip
    You can check that the device shadow has been set correctly with:
    ```bash
    iotactl get-shadow device <device> --group <group-name>
    ```

It is then up to your application code to: 

1. Download the release from the presigned URL
2. Apply that release
3. Update the device state to show it is using the new `releaseId`


!!! Warning
    The presigned URL will have a short time to live (TTL). Do not update the `reported` state of your device(s) until the release is successfully downloaded.

!!! Tip
    If you report an outdated or empty `releaseId`, you will get a new presigned URL on the `delta` topic to use on the `shadow/reported` topic of your device(s).
    These are generated every time the `delta` is calculated. Anything that doesn't match the "desired" state results in a new presigned URL.

!!! Note
    When you have successfully downloaded the release, make sure to update the state of your device with the new release ID under `x-agora-ota` structure:
    ```json
        {
            "x-agora-ota": {
                "releaseId": "<release-id>",
                "releases": {
                    "<release-type-1>": {
                        "releaseId": "<release-id-1>",
                        "releaseType": "<release-type-1>"
                    },
                    "<release-type-2>": {
                        "releaseId": "<release-id-2>",
                        "releaseType": "<release-type-2>"
                    }
                }
            }
        }
    ```
    You have the option to include an `x-agora-transient-data` section, but anything within it will be ignored.


## Conclusion
<!---
Congratulate the reader for finishing the tutorial and list next steps or related links if available. You can also use this section to link to more in-depth documents to expand on specific points in the tutorial, or overview conceptual docs if you feel like more explanation is needed to help the user understand what they're implementing.
--->

Congrats! You should now have sent a presigned URL to your device(s), which can then go on to download the release.

If you run into any issues, please reach out to our team on the Slack `#wcm-org-agora-services` channel.
