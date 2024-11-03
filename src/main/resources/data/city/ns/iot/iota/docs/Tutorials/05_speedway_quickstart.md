# IoTA Speedway Quickstart

## Overview
The purpose of this tutorial is to get your environment setup and ready to use IoTA and `iotactl` in Speedway Production.

## What You'll Learn

From this tutorial you will learn how to set up your tenant in Production, configure `iotactl` to
connect to Production, and be made aware of any differences that exist between PreProduction and Production.

1. [Configuring `iotactl`](#configuring-iotactl)
2. [Differences between PreProd and Production](#differences-between-preprod-and-production)
3. [Migrate your Xenia BYOB Configuration](#migrate-your-xenia-byob-configuration)
4. [How to contact the Services team if you have any issues](#how-to-contact-the-services-team-if-you-have-any-issues)

## What You'll Need

To get started you'll need the following:

* The latest version of `iotactl` (please refer to the steps [here](../index.md#prerequisites)).
* A tenant in our Production environment (you can use our [tenant-creation stack](../Tasks/onboarding.md#system)).
* A device to add/provision/send messages to (strictly speaking this is *optional*).

If you have all of that set up then you're ready to start.

## Configuring `iotactl`

To configure `iotactl` please add this file `~/.iota/prod.toml`:
``` 
client-id = 'iota-client'
idp-url = 'https://id.woven-city.toyota'
service-url = 'https://iot.woven-city-api.toyota'
tenant = '<your tenant>'
```

After doing this you can call `iotactl set context prod` which will switch `iotactl` to use the new configuration file, 
and start calling IoTA Production.

From here you are able to call all of the commands that you normally use e.g: `iotactl add group <group-name>` or 
`iotactl add device <device-name> --group <group name>`

## Differences between PreProd and Production

For your devices there are a couple of notable differences between PreProd and Prod:

* RabbitMQ should be referenced with our stable URL `iot.woven-city-api.toyota:8883`
* Our endpoints are now versioned - you can find our API map [here](https://developer.woven-city.toyota/catalog/default/api/iota-api-stable/definition)

## Migrate your Xenia BYOB Configuration

As part of migrating to Speedway Prod you will need to update a couple of things in your Terraform setup for Xenia BYOB,
please reach out to the Services team for the correct values for:

* `accessor_arn`
* `queue_arn`


## How to contact the Services team if you have any issues

You can ask a question in the `#wcm-org-agora-ama` channel, or if you want direct contact with the Services team you can
 reach out to the `#wcm-agora-services` channel.
