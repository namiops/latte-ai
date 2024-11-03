# IoTA onboarding guide for the DEV2 environment

## Overview

This document instructs existing (i.e., DEV) clients on how to onboard their devices onto the DEV2 cluster. Note that you will have to re-onboard all your devices with a new endpoint, new certificate, and new secret, and you will have to set the desired shadow status.

## Migration requirements

Below are the changes that you need to make on the client side:

* [**iotactl**](#iotactl)
* **Data**
  * [**Groups and devices**](#groups-and-devices)
  * [**Desired shadow status**](#shadow-status) 
  * [**Publish your device to RabbitMQ**](#publish-your-device-to-rabbitmq)
* [**iotad**](#iotad)
* [**Update your log collector endpoints**](#update-your-log-collector-endpoints)

!!! Note

The Agora Services Team will migrate your tenant name and users to DEV2. If you encounter any issues during the onboarding process, feel free to contact us through the [#wcm-org-agora-services](https://toyotaglobal.enterprise.slack.com/archives/C042AQ2TU4A) Slack channel.

### iotactl

First, update the configuration files with iotactl for both your operator and your device.

Create a `.toml` file with your iotactl context as the filename. For this example, we are using `dev2e` to signify that the device is connecting to the `dev2-worker1-east` cluster.

```bash
vim ~/.iota dev2e.toml
```

Edit the file with the following data:

```
client-id = 'iota-client'
idp-url = 'https://id.agora-dev.w3n.io'
service-url = 'https://iot.agora-dev.w3n.io'
group = '<your-group-here>' 
tenant = '<your-tenant-here>'
```

Note `group` and `tenant` are optional here, and you can only specify one group at a time. If you set these values now, you don't have to specify them again in the future running commands in iotactl. 

The next step is to set the iotactl context. Make sure that this matches the name of the `.toml` you created above.

```bash
iotactl set context dev2e
```

### Data

#### Groups and devices

Add your group and devices to the new cluster. You can do so in one of two ways:

* **`iotactl:`** See [here](https://developer.woven-city.toyota/docs/default/component/iota-service/Tasks/iotactl/#iotactl-add-group) for instructions.
* **Agora UI:** Access the IoTA devices panel [here](https://agora-ui.agora-dev.w3n.io/admin/iota-devices).

!!! Note
Make sure to note down the provisioning secret for later use.

Now, provision your device with the new provisioning secret. This can be done in one of the following ways:

1. On the [IoTA admin UI](https://agora-ui.agora-lab.woven-planet.tech/admin/iota-devices)

2. Using the following command on iotactl:
   ```bash
   iotactl provision <device-name> --provision-secret <provision-secret>
   ```
   (For more information, refer to the [IoTA documentation](https://developer.woven-city.toyota/docs/default/Component/iota-service/Tasks/iotactl/#iotactl-provision).)

3. Copy the newly generated certificate files to the following directory on your device:
   ```
   $HOME/.iota/<context>/<tenant>/<group>/<device>_*
   ```

#### Shadow status

Next, set your desired shadow status as follows:

* **`iotactl:`** Use the `iotactl set-shadow group` or `iotactl set-shadow device` command.
* **Agora UI:** Go to the device shadow viewer on [Agora UI](https://agora-ui.agora-dev.w3n.io/admin/iota-devices).


#### Publish your device to RabbitMQ

If your device publishes messages via MQTT to the Agora IoT RabbitMQ broker for OTA, log, tracing, metrics or device shadow functions, or if you have custom resources in Agora IoT RabbitMQ, you need to update the endpoint to `iot.agora-dev.w3n.io` in your MQTT configuration.

If you are not using the iotactl-generated certificate folder directly, replace the `*.pem` files with the newly provisioned certificate files from the iotactl provisioning results as mentioned in the [iotactl](#iotactl) section above.

### iotad

You also need to update your `context`, `service_url`, and `provision_secret` configurations in `$HOME/.iota/iotad.json`:

```json
{
    "service_url": "https://iot.agora-dev.w3n.io",
    "context": "dev2e",
    "tenant": "tenant-name",
    "group": "group-name",
    "device": "device-name",
    "provision_secret": "<NEW GROUP PROVISIONING SECRET>",
    "ttl": 48,
    "cert_refresh_threshold": 24
}
```

### Update your log collector endpoints

Finally, you need update the endpoints of your DEV2 `log-collector` to the following formats for access via HTTP:

* Default format handler: `https://****.agora-dev.w3n.io`
* OTEL logs handler: `https://****.agora-dev.w3n.io/otel/v1/logs`
* OTEL traces handler: `https://****.agora-dev.w3n.io/otel/v1/traces`

This concludes the onboarding process for your IoT device to the new cluster.
